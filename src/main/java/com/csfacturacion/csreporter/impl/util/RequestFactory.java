/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl.util;

import com.csfacturacion.csreporter.Credenciales;
import com.csfacturacion.csreporter.Parametros;
import com.csfacturacion.csreporter.impl.http.Request;
import com.google.common.collect.Maps;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;
import org.apache.http.client.utils.URIBuilder;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author emerino
 */
public class RequestFactory {

    private static final String HOST_DEFAULT = "www.csfacturacion.com";

    private static final String PATH_DEFAULT = "/webservices/csdescargasat";

    private static final String SCHEMA_DEFAULT = "https";

    private static DateTimeFormatter dateFormatter;

    private final String wsSchema;
    private final String wsHost;
    private final String wsPath;

    public RequestFactory() {
        this(SCHEMA_DEFAULT, HOST_DEFAULT, PATH_DEFAULT);
    }

    public RequestFactory(String wsSchema, String wsHost, String wsPath) {
        this.wsHost = wsHost;
        this.wsPath = wsPath;
        this.wsSchema = wsSchema;
    }

    protected URIBuilder newBaseURIBuilder() {
        return newBaseURIBuilder("");
    }

    protected URIBuilder newBaseURIBuilder(String path) {
        return new URIBuilder()
                .setScheme(wsSchema)
                .setHost(wsHost)
                .setPath(wsPath + path);
    }

    public Request newConsultaRequest(Credenciales csCredenciales,
            Credenciales satCredenciales,
            Parametros params) {

        try {
            DateTimeFormatter df = getDateFormatter();
            URIBuilder consultaUriBuilder = newBaseURIBuilder()
                    .setParameter("method", "ObtenerCfdisV2")
                    .setParameter("cRfcContrato", csCredenciales.getUsuario())
                    .setParameter("cRfc", satCredenciales.getUsuario())
                    .setParameter("cPassword", satCredenciales.getPassword())
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
            return new Request(consultaUriBuilder.build(),
                    Request.HttpMethod.GET);
        } catch (URISyntaxException e) {
            throw new InvalidRequest(e);
        }
    }

    public Request newRepetirConsultaRequest(UUID folio) {
        try {
            Map<String, String> entity = Maps.newHashMap();
            entity.put("idConsulta", folio.toString());
            return new Request(
                    newBaseURIBuilder("/repetir").build(),
                    Request.HttpMethod.POST,
                    entity);
        } catch (URISyntaxException e) {
            throw new InvalidRequest(e);
        }
    }

    public Request newStatusRequest(UUID folio) {
        try {
            return new Request(
                    newResultadosURIBuilder(folio, "/progreso")
                    .build(),
                    Request.HttpMethod.POST);
        } catch (URISyntaxException e) {
            // log
            throw new InvalidRequest(e);
        }
    }

    protected URIBuilder newResultadosURIBuilder(UUID folio, String path) {
        return newBaseURIBuilder("/resultados/" + folio + path);
    }

    public Request newResumenRequest(UUID folio) {
        return newResultadosRequest(folio, "/resumen");
    }

    public Request newResultadosRequest(UUID folio, int pagina) {
        return newResultadosRequest(folio, "/" + pagina);
    }

    private Request newResultadosRequest(UUID folio, String path) {
        try {
            return new Request(
                    newResultadosURIBuilder(folio, path)
                    .build(),
                    Request.HttpMethod.GET);
        } catch (URISyntaxException e) {
            // log
            throw new InvalidRequest(e);
        }
    }

    public Request newDescargaRequest(UUID folio, UUID folioCFDI) {
        try {
            return new Request(
                    newDescargaURIBuilder(folio, folioCFDI)
                    .build(),
                    Request.HttpMethod.GET)
                    .setAcceptMediaType(Request.MediaType.TEXT_XML);
        } catch (URISyntaxException e) {
            // log
            throw new RuntimeException();
        }
    }

    protected URIBuilder newDescargaURIBuilder(UUID folio, UUID folioCFDI) {
        return newBaseURIBuilder("/descargas/" + folio + "/" + folioCFDI);
    }

    protected static DateTimeFormatter getDateFormatter() {
        if (dateFormatter == null) {
            dateFormatter = ISODateTimeFormat.dateHourMinuteSecond();
        }

        return dateFormatter;
    }
}
