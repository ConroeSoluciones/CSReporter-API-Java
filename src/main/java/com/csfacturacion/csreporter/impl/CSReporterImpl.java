/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl;

import com.csfacturacion.csreporter.impl.http.UserAgent;
import com.csfacturacion.csreporter.CloseableCSReporter;
import com.csfacturacion.csreporter.Consulta;
import com.csfacturacion.csreporter.ConsultaInvalidaException;
import com.csfacturacion.csreporter.Credenciales;
import com.csfacturacion.csreporter.Parametros;
import com.csfacturacion.csreporter.ProgresoConsultaListener;
import com.csfacturacion.csreporter.impl.http.Response;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Implementación por defecto de DescargaSAT. Se utiliza un DescargaSAT por
 * contrato (csfacturación), un DescargaSAT puede manejar distintas consultas y
 * búsquedas para un contrato.
 *
 * @author emerino
 */
public class CSReporterImpl implements CloseableCSReporter {

    private static final String csHost = "www.csfacturacion.com";

    private Credenciales csCredenciales;

    private int statusCheckTimeout = 15000; // en milisegundos

    private StatusChecker statusChecker;

    private final UserAgent userAgent;

    private static DateTimeFormatter dateFormatter;

    private final ScheduledExecutorService scheduler
            = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> statusCheckerHandle;

    private DateTimeFormatter getDateFormatter() {
        if (dateFormatter == null) {
            dateFormatter = ISODateTimeFormat.dateHourMinuteSecond();
        }

        return dateFormatter;
    }

    public CSReporterImpl() {
        statusChecker = new StatusChecker();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

        userAgent = new UserAgent(httpClient, gson);
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

        statusCheckerHandle = scheduler.scheduleAtFixedRate(
                statusChecker,
                timeout,
                timeout,
                TimeUnit.MILLISECONDS);
    }

    private void validarCredenciales() {
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

        DateTimeFormatter df = getDateFormatter();

        try {
            URIBuilder consultaUriBuilder = new URIBuilder()
                    .setScheme("https")
                    .setHost(csHost)
                    .setPath("/webservices/csdescargasat")
                    .setParameter("method", "ConsultaSat")
                    .setParameter("cRfcContrato", csCredenciales.getUsuario())
                    .setParameter("cRfc", credenciales.getUsuario())
                    .setParameter("cPassword", credenciales.getPassword())
                    .setParameter("cFchI",
                            df.print(params.getFechaInicio().getTime()))
                    .setParameter("cFchF",
                            df.print(params.getFechaFin().getTime()))
                    .setParameter("cConsulta",
                            (params.getTipo() == Parametros.Tipo.EMITIDAS)
                                    ? "emitidas"
                                    : "recibidas")
                    .setParameter("cRfcSearch",
                            (params.getRfcBusqueda() != null)
                                    ? params.getRfcBusqueda().toString()
                                    : "todos")
                    .setParameter("cServicio",
                            String.valueOf(params.getServicio().getNumero()));

            String status;

            switch (params.getStatus()) {
                case VIGENTE:
                    status = "vigentes";
                    break;
                case CANCELADO:
                    status = "cancelados";
                    break;
                default:
                    status = "todos";
                    break;
            }

            consultaUriBuilder.setParameter("cEstatus", status);

            HttpGet httpGet = new HttpGet(consultaUriBuilder.build());
            JsonObject consultaJson = userAgent.open(httpGet)
                    .getAsJson()
                    .getAsJsonObject();

            String folioRaw = consultaJson.get("UUID").getAsString();
            if (folioRaw.isEmpty()) {
                // TODO: Debería mandar al log la estructura JSON recibida,
                // para arreglar el problema en caso que se presente.
                String msg = (consultaJson.has("MENSAJE"))
                        ? consultaJson.get("MENSAJE").getAsString()
                        : "Ocurrió un error desconocido al realizar la consulta.";

                throw new ConsultaInvalidaException(msg);
            }

            UUID folio = UUID.fromString(consultaJson.get("UUID").getAsString());
            return consultarGenerico(folio, listener);
        } catch (URISyntaxException e) {
            throw new ConsultaInvalidaException(e);
        }
    }

    private Consulta consultarGenerico(UUID folio,
            ProgresoConsultaListener listener)
            throws ConsultaInvalidaException {

        Consulta consulta = new ConsultaImpl(folio, userAgent);
        if (listener != null) {
            statusChecker.addConsulta(consulta, listener);
        }
        return consulta;
    }

    @Override
    public Consulta buscar(UUID folio) throws ConsultaInvalidaException {
        validarExistente(folio);

        return new ConsultaImpl(folio, userAgent);
    }

    @Override
    public Consulta buscar(UUID folio, ProgresoConsultaListener listener)
            throws ConsultaInvalidaException {

        Consulta consulta = buscar(folio);
        if (consulta.isRepetir()) {
            // repetir de ser necesario
            consulta = repetir(folio, listener);
        } else if (!consulta.isTerminada()) {
            // si aún no ha terminado, agrega la consulta para ser verificada
            // con el status checker
            statusChecker.addConsulta(consulta, listener);
        } else {
            // si ya había terminado, simplemente llama al método onStatusChanged
            // directamente
            listener.onStatusChanged(consulta);
        }

        return consulta;
    }

    private void validarExistente(UUID folio) throws ConsultaInvalidaException {
        validarCredenciales();
        HttpPost statusPost = new HttpPost(
                ConsultaImpl.getProgresoURI(folio));

        Response response = userAgent.open(statusPost);

        // verifica que la respuesta sea la esperada, de lo contrario
        // no existe una consulta asociada con el folio dado
        if (response.getCode() != 200) {
            throw new ConsultaInvalidaException("No existe ninguna consulta con"
                    + " el UUID dado.");
        }

    }

    private URI getRepetirConsultaURI() {
        try {
            return new URIBuilder()
                    .setScheme("https")
                    .setHost(csHost)
                    .setPath("/webservices/csdescargasat/repetir")
                    .build();
        } catch (URISyntaxException e) {
            // log
            throw new RuntimeException();
        }
    }

    @Override
    public Consulta repetir(UUID folio) throws ConsultaInvalidaException {
        return repetir(folio, null);
    }

    @Override
    public Consulta repetir(UUID folio, ProgresoConsultaListener listener)
            throws ConsultaInvalidaException {

        validarExistente(folio);

        HttpPost repetirRequest = new HttpPost(getRepetirConsultaURI());
        List<NameValuePair> formparams = Lists.newArrayList();
        formparams.add(new BasicNameValuePair("idConsulta", folio.toString()));
        UrlEncodedFormEntity entity
                = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        repetirRequest.setEntity(entity);

        userAgent.open(repetirRequest);

        return consultarGenerico(folio, listener);
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

    /**
     * Las instancias de esta clase se encargan de checar el estado actual de
     * una o varias consultas, cada N segundos.
     */
    private static class StatusChecker implements Runnable {

        private final Deque<ConsultaHolder> consultas = Queues.newArrayDeque();

        private final ReentrantLock consultasLock;

        public StatusChecker() {
            this.consultasLock = new ReentrantLock();
        }

        public void addConsulta(Consulta consulta,
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
                    Consulta.Status status = holder.consulta.getStatus();
                    // notifica de ser necesario
                    if (holder.progresoListener != null
                            && (status != holder.statusAnterior
                            || status == Consulta.Status.COMPLETADO)) {

                        holder.progresoListener.onStatusChanged(
                                holder.consulta);
                    }

                    if (holder.consulta.isTerminada()) {
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

        private final Consulta consulta;
        private final ProgresoConsultaListener progresoListener;
        private Consulta.Status statusAnterior;

        public ConsultaHolder(Consulta consulta,
                ProgresoConsultaListener progresoListener) {

            this.consulta = consulta;
            this.progresoListener = progresoListener;
            this.statusAnterior = consulta.getStatus();
        }
    }
}
