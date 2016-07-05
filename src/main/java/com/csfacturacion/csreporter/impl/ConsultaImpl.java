/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl;

import com.csfacturacion.csreporter.impl.http.UserAgent;
import com.csfacturacion.csreporter.CFDIMeta;
import com.csfacturacion.csreporter.Consulta;
import com.csfacturacion.csreporter.ResultadosInsuficientesException;
import com.csfacturacion.csreporter.impl.util.RequestFactory;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.UUID;

/**
 * Implementación de una {@link Consulta} que utiliza Apache HTTPClient.
 *
 * @author emerino
 */
public class ConsultaImpl implements Consulta {

    private static final String csHost = "www.csfacturacion.com";

    private final UserAgent userAgent;
    private final UUID folio;

    private int paginas;

    private long totalResultados;

    private boolean initResultados;

    private boolean initPaginas;

    private RequestFactory requestFactory;

    protected ConsultaImpl(UUID folio,
            RequestFactory requestFactory,
            UserAgent userAgent) {

        this.folio = folio;
        this.userAgent = userAgent;
        this.requestFactory = requestFactory;
    }

    /**
     *
     */
    @Override
    public Status getStatus() {
        JsonObject progreso = userAgent.open(
                requestFactory.newStatusRequest(folio))
                .getAsJson()
                .getAsJsonObject();

        Status status = Status.valueOf(progreso.get("estado").getAsString());

        return status;
    }

    @Override
    public boolean isTerminada() {
        Status status = getStatus();
        return status.toString().startsWith("COMPLETADO")
                || isFallo(status);
    }

    @Override
    public boolean isFallo() {
        return isFallo(getStatus());
    }

    private boolean isFallo(Status status) {
        return status.toString().startsWith("FALLO");
    }

    @Override
    public boolean isRepetir() {
        return getStatus() == Status.REPETIR;
    }

    @Override
    public UUID getFolio() {
        return folio;
    }

    @Override
    public long getTotalResultados() {
        if (!initResultados) {
            validarTerminada();
            JsonObject resumen = userAgent.open(
                    requestFactory.newResumenRequest(folio))
                    .getAsJson()
                    .getAsJsonObject();

            totalResultados = Long.valueOf(resumen.get("total").toString());
            initResultados = true;
        }

        return totalResultados;
    }

    @Override
    public int getPaginas() {
        if (!initPaginas) {
            validarTerminada();
            JsonObject resumen = userAgent.open(
                    requestFactory.newResumenRequest(folio))
                    .getAsJson()
                    .getAsJsonObject();

            paginas = Integer.valueOf(resumen.get("paginas").toString());
            initPaginas = true;
        }

        return paginas;
    }

    @Override
    public List<? extends CFDIMeta> getResultados(int pagina)
            throws ResultadosInsuficientesException {

        validarTerminada();
        validarResultadosSuficientes(pagina);

        List<CFDIMeta> resultados = userAgent.open(
                requestFactory.newResultadosRequest(folio, pagina))
                .getAs(new TypeToken<List<CFDIMeta>>() {
                });

        return resultados;
    }

    @Override
    public boolean hasResultados() {
        return getPaginas() > 0;
    }

    protected void validarResultadosSuficientes(int pagina) {
        if (!hasResultados() || pagina > getPaginas()) {
            throw new ResultadosInsuficientesException("No existen suficientes "
                    + "resultados para mostrar, total páginas: " + getPaginas());
        }

    }

    protected UserAgent getUserAgent() {
        return userAgent;
    }

    protected RequestFactory getRequestFactory() {
        return requestFactory;
    }

    @Override
    public CFDIMeta getCFDI(UUID folio) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCFDIXML(CFDIMeta cfdi) {
        return getCFDIXML(cfdi.getFolio());
    }

    @Override
    public String getCFDIXML(UUID folioCFDI) {
        validarTerminada();
        return userAgent.open(
                requestFactory.newDescargaRequest(folio, folioCFDI))
                .getRawResponse();
    }

    protected void validarTerminada() {
        if (!isTerminada()) {
            throw new IllegalStateException("La consulta no ha terminado.");
        }
    }

}
