/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl;

import com.csfacturacion.csreporter.impl.http.UserAgent;
import com.csfacturacion.csreporter.CFDI;
import com.csfacturacion.csreporter.Consulta;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;

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

    ConsultaImpl(UUID folio, UserAgent userAgent) {
        this.folio = folio;
        this.userAgent = userAgent;
    }

    static URI getResultadosURI(UUID folio, String path) {
        try {
            return new URIBuilder()
                    .setScheme("https")
                    .setHost(csHost)
                    .setPath("/webservices/csdescargasat/resultados/"
                            + folio
                            + path)
                    .build();
        } catch (URISyntaxException e) {
            // log
            throw new RuntimeException();
        }
    }

    static URI getProgresoURI(UUID folio) {
        return getResultadosURI(folio, "/progreso");
    }

    /**
     *
     */
    @Override
    public Status getStatus() {
        HttpPost progresoPost = new HttpPost(getProgresoURI(folio));
        JsonObject progreso = userAgent.open(progresoPost)
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
        validarTerminada();

        if (!initResultados) {
            HttpGet resumenRequest = new HttpGet(getResumenURI());
            JsonObject resumen = userAgent.open(resumenRequest)
                    .getAsJson()
                    .getAsJsonObject();

            totalResultados = Long.valueOf(resumen.get("total").toString());
            initResultados = true;
        }

        return totalResultados;
    }

    @Override
    public int getPaginas() {
        validarTerminada();

        if (!initPaginas) {
            HttpGet resumenRequest = new HttpGet(getResumenURI());
            JsonObject resumen = userAgent.open(resumenRequest)
                    .getAsJson()
                    .getAsJsonObject();

            paginas = Integer.valueOf(resumen.get("paginas").toString());
            initPaginas = true;
        }

        return paginas;
    }

    private URI getResumenURI() {
        return getResultadosURI(folio, "/resumen");
    }

    private URI getResultadosURI(int pagina) {
        return getResultadosURI(folio, "/" + pagina);
    }

    @Override
    public List<CFDI> getResultados(int pagina) {
        validarTerminada();

        HttpGet resultadosRequest = new HttpGet(getResultadosURI(pagina));

        List<CFDI> resultados = userAgent.open(resultadosRequest)
                .getAs(new TypeToken<List<CFDI>>() {
                });

        return resultados;
    }

    private URI getDescargasURI(UUID folioCFDI) {
        try {
            return new URIBuilder()
                    .setScheme("https")
                    .setHost(csHost)
                    .setPath("/webservices/csdescargasat/descargas/"
                            + folio + "/"
                            + folioCFDI)
                    .build();
        } catch (URISyntaxException e) {
            // log
            throw new RuntimeException();
        }
    }

    @Override
    public CFDI getCFDI(UUID folio) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCFDIXML(CFDI cfdi) {
        return getCFDIXML(cfdi.getFolio());
    }

    @Override
    public String getCFDIXML(UUID folioCFDI) {
        validarTerminada();

        HttpGet descargaRequest = new HttpGet(getDescargasURI(folioCFDI));
        return userAgent.open(descargaRequest).getRawResponse();
    }

    private void validarTerminada() {
        if (!isTerminada()) {
            throw new IllegalStateException("La consulta no ha terminado.");
        }
    }

}
