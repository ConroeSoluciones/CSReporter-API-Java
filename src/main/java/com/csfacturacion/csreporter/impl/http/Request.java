/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author emerino
 */
public class Request {

    public enum HttpMethod {
        GET,
        POST
    }
    
    private URI uri;

    private HttpMethod method;

    private Map<String, String> entity;

    public Request(URI uri, HttpMethod method) {
        this(uri, method, null);
    }

    public Request(URI uri, HttpMethod method, Map<String, String> entity) {
        this.uri = uri;
        this.method = method;
        this.entity = entity;
    }

    public URI getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getEntity() {
        return entity;
    }


}
