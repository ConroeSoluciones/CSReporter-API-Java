/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl;

import com.csfacturacion.csreporter.impl.http.UserAgent;
import com.csfacturacion.csreporter.CFDIMeta;
import com.csfacturacion.csreporter.Consulta;
import com.csfacturacion.csreporter.Parametros;
import com.csfacturacion.csreporter.ResultadosInsuficientesException;
import com.csfacturacion.csreporter.XMLNoEncontradoException;
import com.csfacturacion.csreporter.impl.http.Response;
import com.csfacturacion.csreporter.impl.util.RequestFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private Integer paginas;

    private Long totalResultados;

    private RequestFactory requestFactory;

    private Status status;

    private Parametros parametros;

    protected ConsultaImpl(
            Parametros parametros,
            UUID folio,
            RequestFactory requestFactory,
            UserAgent userAgent) {

        this.folio = folio;
        this.userAgent = userAgent;
        this.requestFactory = requestFactory;
        this.parametros = parametros;
    }

    @Override
    public Parametros getParametros() {
        return parametros;
    }

    /**
     *
     */
    @Override
    public Status getStatus() {
        Status statusNuevo = status;
        
        if (statusNuevo == null) {
            JsonObject progreso = userAgent.open(
                    requestFactory.newStatusRequest(folio))
                    .getAsJson()
                    .getAsJsonObject();

            statusNuevo = Status.valueOf(progreso.get("estado").getAsString());

            if (statusNuevo.isCompletado()) {
                status = statusNuevo;
            }
        }

        return statusNuevo;
    }

    @Override
    public boolean isTerminada() {
        return getStatus().isCompletado();
    }

    @Override
    public boolean isFallo() {
        return getStatus().isFallo();
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
        if (totalResultados == null) {
            validarTerminada();
            JsonObject resumen = userAgent.open(
                    requestFactory.newResumenRequest(folio))
                    .getAsJson()
                    .getAsJsonObject();

            totalResultados = Long.valueOf(resumen.get("total").toString());
        }

        return totalResultados;
    }

    @Override
    public int getPaginas() {
        if (paginas == null) {
            validarTerminada();
            JsonObject resumen = userAgent.open(
                    requestFactory.newResumenRequest(folio))
                    .getAsJson()
                    .getAsJsonObject();

            paginas = Integer.valueOf(resumen.get("paginas").toString());
        }

        return paginas;
    }

    @Override
    public List<CFDIMeta> getResultados(int pagina)
            throws ResultadosInsuficientesException {

        return getResultados(pagina, CFDIMeta.class);
    }

    @Override
    public <T extends CFDIMeta> List<T> getResultados(
            int pagina, Class<T> clazz) {

        validarTerminada();
        validarResultadosSuficientes(pagina);

        List<T> resultados = newResultadosList(userAgent.open(
                requestFactory.newResultadosRequest(folio, pagina)),
                clazz);

        return resultados;
    }

    protected <T extends CFDIMeta> List<T> newResultadosList(
            Response response,
            Class<T> clazz) {

        JsonArray array = response.getAsJson().getAsJsonArray();

        List<T> lst = new ArrayList<T>();
        for (final JsonElement json : array) {
            T entity = userAgent.getGson().fromJson(json, clazz);
            lst.add(entity);
        }

        return lst;
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
    public String getCFDIXML(CFDIMeta cfdi) throws XMLNoEncontradoException {
        return getCFDIXML(cfdi.getFolio());
    }

    @Override
    public String getCFDIXML(UUID folioCFDI) throws XMLNoEncontradoException {
        validarTerminada();
        Response response = getUserAgent().open(
                getRequestFactory().newDescargaRequest(folio, folioCFDI));

        if (response.getCode() != 200) {
            throw new XMLNoEncontradoException("No se encontró el XML para el "
                    + "folio: " + folioCFDI);
        }

        return response.getRawResponse();
    }

    protected void validarTerminada() {
        if (!isTerminada()) {
            throw new IllegalStateException("La consulta no ha terminado.");
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.folio);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConsultaImpl other = (ConsultaImpl) obj;
        if (!Objects.equals(this.folio, other.folio)) {
            return false;
        }
        return true;
    }

}
