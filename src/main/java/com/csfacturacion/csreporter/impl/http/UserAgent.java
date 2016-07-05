/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl.http;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

/**
 * Se encarga de manejar toda la comunicación HTTP y devolverla en un formato de
 * fácil acceso (e.g. un String o una lista).
 *
 * @author emerino
 */
public class UserAgent {

    private final CloseableHttpClient httpClient;
    private final Gson gson;

    public UserAgent(CloseableHttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    /**
     * Realiza una petición HTTP y devuelve un Response, con el que se pueden
     * objetener distintas estructuras de datos a partir de la respuesta
     * obtenida.
     *
     * @param request La petición http.
     * @return la respuesta disponible en distintos formatos.
     */
    public Response open(Request request) {
        HttpUriRequest r;
        if (request.getMethod() == Request.HttpMethod.GET) {
            r = new HttpGet(request.getUri());
        } else {
            HttpPost p = new HttpPost(request.getUri());
            if (request.getEntity() != null) {
                switch (request.getMediaType()) {
                    case X_WWW_FORM_URLENCODED:
                        handleFormEncoded(p, request.getEntity());
                        break;
                    case JSON:
                        handleJson(p, request.getEntity());
                        break;
                    default:
                        throw new IllegalStateException("method not handled");
                }

            }
            r = p;
        }

        if (request.getAcceptMediaType() != null) {
            r.setHeader(
                    HttpHeaders.ACCEPT,
                    request.getAcceptMediaType().getName());
        }

        RawResponse rawResponse = openRaw(r);
        return new Response(gson,
                rawResponse.getContent(),
                rawResponse.getCode());
    }

    private void handleFormEncoded(HttpPost p, Map<String, String> entity) {
        List<NameValuePair> formparams = Lists.newArrayList();
        for (Map.Entry<String, String> kv
                : entity.entrySet()) {

            formparams.add(new BasicNameValuePair(
                    kv.getKey(),
                    kv.getValue()));
        }
        UrlEncodedFormEntity e
                = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        p.setEntity(e);
    }

    private void handleJson(HttpPost p, Map<String, String> entity) {
        StringEntity e = new StringEntity(gson.toJson(entity),
                ContentType.APPLICATION_JSON);

        p.setEntity(e);
    }

    private RawResponse openRaw(HttpUriRequest request) {
        try {
            CloseableHttpResponse response
                    = httpClient.execute(request, new BasicHttpContext());

            try {
                HttpEntity entity = response.getEntity();
                return (entity != null)
                        ? new RawResponse(IOUtils.toString(entity.getContent()),
                                response.getStatusLine().getStatusCode())
                        : null;
            } finally {
                response.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        httpClient.close();
    }

    private static class RawResponse {

        private String content;
        private int code;

        public RawResponse(String content, int code) {
            this.content = content;
            this.code = code;
        }

        public String getContent() {
            return content;
        }

        public int getCode() {
            return code;
        }

    }
}
