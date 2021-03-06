/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl;

import com.csfacturacion.csreporter.impl.http.UserAgent;
import com.csfacturacion.csreporter.CloseableCSReporter;
import com.csfacturacion.csreporter.Consulta;
import com.csfacturacion.csreporter.Consulta.Status;
import com.csfacturacion.csreporter.ConsultaInexistenteException;
import com.csfacturacion.csreporter.ConsultaInvalidaException;
import com.csfacturacion.csreporter.Credenciales;
import com.csfacturacion.csreporter.Parametros;
import com.csfacturacion.csreporter.ProgresoConsultaListener;
import com.csfacturacion.csreporter.impl.http.Request;
import com.csfacturacion.csreporter.impl.http.Response;
import com.csfacturacion.csreporter.impl.util.RequestFactory;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación por defecto de DescargaSAT. Se utiliza un DescargaSAT por
 * contrato (csfacturación), un DescargaSAT puede manejar distintas consultas y
 * búsquedas para un contrato.
 *
 * @author emerino
 */
public class CSReporterImpl implements CloseableCSReporter {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(CSReporterImpl.class);

    private static final int DEFAULT_TIMEOUT = 15; //seconds

    private Credenciales csCredenciales;

    private int statusCheckTimeout = 15000; // en milisegundos

    private StatusChecker statusChecker;

    private final UserAgent userAgent;

    private final ScheduledExecutorService scheduler
            = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> statusCheckerHandle;

    private RequestFactory requestFactory;

    public CSReporterImpl() {
        this(new RequestFactory(), DEFAULT_TIMEOUT * 1000);
    }

    public CSReporterImpl(UserAgent userAgent) {
        this(null, userAgent, DEFAULT_TIMEOUT * 1000);
    }

    public CSReporterImpl(RequestFactory requestFactory) {
        this(requestFactory, DEFAULT_TIMEOUT * 1000);
    }

    public CSReporterImpl(RequestFactory requestFactory, int timeout) {
        this(requestFactory, new UserAgent(
                HttpClients.createDefault(),
                new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create()),
                timeout);
    }

    public CSReporterImpl(RequestFactory requestFactory, UserAgent userAgent) {
        this(requestFactory, userAgent, DEFAULT_TIMEOUT * 1000);
    }

    public CSReporterImpl(RequestFactory requestFactory,
            UserAgent userAgent,
            int timeout) {

        this.requestFactory = requestFactory;
        this.statusChecker = new StatusChecker();
        this.userAgent = userAgent;

        statusCheckerHandle = scheduler.scheduleAtFixedRate(
                statusChecker,
                timeout,
                timeout,
                TimeUnit.MILLISECONDS);
    }

    /**
     * Crea una nueva instancia de un DescargaSATHttpClient, usando el usuario y
     * pass del contrato con CS Facturación.
     *
     * @param csCredenciales del contrato con CSFacturación
     * @param timeout para el statusChecker
     */
    public CSReporterImpl(Credenciales csCredenciales,
            int timeout) {

        this();
        this.csCredenciales = csCredenciales;

    }

    protected void validarCredenciales() {
        if (csCredenciales == null) {
            throw new IllegalStateException("No se han establecido las "
                    + "credenciales del contrato.");
        }

    }

    @Override
    public Consulta consultar(Credenciales credenciales, Parametros params)
            throws ConsultaInvalidaException {

        return consultar(credenciales, params, null);
    }

    @Override
    public Consulta consultar(Credenciales credenciales,
            Parametros params,
            ProgresoConsultaListener listener)
            throws ConsultaInvalidaException {

        validarCredenciales();

        Request request = requestFactory.newConsultaRequest(
                csCredenciales, credenciales, params);

        Response response = userAgent.open(request);
        JsonObject consultaJson = response
                .getAsJson()
                .getAsJsonObject();

        String folioRaw = consultaJson.get(getJsonConsultaIdParamName())
                .getAsString();

        if (response.getCode() != 200) {
            throw new ConsultaInvalidaException("Ocurrió un error al "
                    + "comunicarse con el servidor de descarga masiva."
                    + "Código del servidor: "
                    + response.getCode());
        }

        if (folioRaw.isEmpty()) {
            // TODO: Debería mandar al log la estructura JSON recibida,
            // para arreglar el problema en caso que se presente.
            String msg = (consultaJson.has("MENSAJE"))
                    ? consultaJson.get("MENSAJE").getAsString()
                    : "Ocurrió un error desconocido al realizar la consulta.";

            throw new ConsultaInvalidaException(msg);
        }

        return newConsultaWithChecker(
                params, 
                UUID.fromString(folioRaw), 
                listener);
    }

    protected String getJsonConsultaIdParamName() {
        return "UUID";
    }

    protected ConsultaImpl newConsulta(Parametros parametros, UUID folio)
            throws ConsultaInvalidaException {

        return new ConsultaImpl(parametros, folio, requestFactory, userAgent);
    }

    private ConsultaImpl newConsultaWithChecker(
            Parametros parametros,
            UUID folio,
            ProgresoConsultaListener listener)
            throws ConsultaInvalidaException {

        ConsultaImpl consulta = newConsulta(parametros, folio);

        if (listener != null) {
            statusChecker.addConsulta(consulta, listener);
        }

        return consulta;
    }

    @Override
    public ConsultaImpl buscar(UUID folio) throws ConsultaInvalidaException {
        validarExistente(folio);

        // TODO: Actualmente, el WS no devuelve los parámetros de búsqueda
        // utilizados para generar la consulta que se busca, por lo que los
        // parámetros serán nulos
        return newConsultaWithChecker(null, folio, null);
    }

    @Override
    public ConsultaImpl buscar(UUID folio, ProgresoConsultaListener listener)
            throws ConsultaInvalidaException {

        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        ConsultaImpl consulta = buscar(folio);
        Status status = consulta.getStatus();
        if (status == Status.REPETIR) {
            // repetir de ser necesario
            consulta = repetir(folio, listener);
        } else if (!status.isCompletado()) {
            // si aún no ha terminado, agrega la consulta para ser verificada
            // con el status checker
            statusChecker.addConsulta(consulta, listener);
        } else {
            // si ya había terminado, simplemente llama al método onStatusChanged
            // directamente
            listener.onStatusChanged(status, consulta);
        }

        return consulta;
    }

    private void validarExistente(UUID folio) throws ConsultaInvalidaException {
        validarCredenciales();

        Response response = userAgent.open(
                requestFactory.newStatusRequest(folio));

        // verifica que la respuesta sea la esperada, de lo contrario
        // no existe una consulta asociada con el folio dado
        if (response.getCode() != 200) {
            throw new ConsultaInexistenteException("No existe ninguna consulta "
                    + "con el UUID dado.");
        }

    }

    @Override
    public ConsultaImpl repetir(UUID folio) throws ConsultaInvalidaException {
        return repetir(folio, null);
    }

    @Override
    public ConsultaImpl repetir(UUID folio, ProgresoConsultaListener listener)
            throws ConsultaInvalidaException {

        validarExistente(folio);
        userAgent.open(requestFactory.newRepetirConsultaRequest(folio));

        return newConsultaWithChecker(null, folio, listener);
    }

    /**
     * Después de ser llamado este método, no puede volver a utilizarse este
     * DescargaSAT.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        statusCheckerHandle.cancel(false);
        userAgent.close();
    }

    public Credenciales getCsCredenciales() {
        return csCredenciales;
    }

    public void setCsCredenciales(Credenciales csCredenciales) {
        this.csCredenciales = csCredenciales;
    }

    public int getStatusCheckTimeout() {
        return statusCheckTimeout;
    }

    protected StatusChecker getStatusChecker() {
        return statusChecker;
    }

    protected UserAgent getUserAgent() {
        return userAgent;
    }

    protected RequestFactory getRequestFactory() {
        return requestFactory;
    }

    /**
     * Las instancias de esta clase se encargan de checar el estado actual de
     * una o varias consultas, cada N segundos.
     */
    protected static class StatusChecker implements Runnable {

        private final List<ConsultaHolder> consultas = Lists.newArrayList();

        private final ReentrantLock consultasLock;

        public StatusChecker() {
            this.consultasLock = new ReentrantLock();
        }

        public void addConsulta(ConsultaImpl consulta,
                ProgresoConsultaListener progresoListener) {

            consultasLock.lock();

            try {
                consultas.add(new ConsultaHolder(consulta, progresoListener));
            } finally {
                consultasLock.unlock();
            }
        }

        @Override
        public void run() {
            List<ConsultaHolder> terminadas = Lists.newArrayList();
            consultasLock.lock();

            try {
                for (ConsultaHolder holder : consultas) {
                    Consulta.Status status;

                    try {
                        status = holder.consulta.getStatus();
                        
                        // notifica de ser necesario
                        if (holder.progresoListener != null
                                && status != holder.statusAnterior) {

                            holder.statusAnterior = status;

                            if (status.isCompletado()) {
                                terminadas.add(holder);
                            }

                            holder.progresoListener.onStatusChanged(
                                    status,
                                    holder.consulta);

                        }

                    } catch (Exception e) {
                        // actualmente si no se puede comunicar con el servidor,
                        // la instancia de UserAgent utilizada falla, lo que
                        // causa que el Thread actual termine
                        // TODO: Se necesita una excepción más específica,
                        // y probablemente verificada
                        LOGGER.debug("Hubo un problema al intentar conectarse "
                                + "al servidor de cfdis descarga", e);

                        // también da por terminada la consulta
                        // TODO: Notificar
                        terminadas.add(holder);
                    }

                }

                for (ConsultaHolder terminada : terminadas) {
                    consultas.remove(terminada);
                }
            } finally {
                consultasLock.unlock();
            }
        }
    }

    private static class ConsultaHolder {

        private final ConsultaImpl consulta;
        private final ProgresoConsultaListener progresoListener;
        private Consulta.Status statusAnterior;

        public ConsultaHolder(ConsultaImpl consulta,
                ProgresoConsultaListener progresoListener) {

            this.consulta = consulta;
            this.progresoListener = progresoListener;
            this.statusAnterior = consulta.getStatus();
        }
    }
}
