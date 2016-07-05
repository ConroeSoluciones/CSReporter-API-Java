/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl.http;

import java.net.URI;
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

    public enum MediaType {
        X_WWW_FORM_URLENCODED("x-www-form-urlencoded"),
        JSON("application/json"), 
        TEXT_XML("text/xml");

        private String name;

        MediaType(String nombre) {
            this.name = nombre;
        }

        public String getName() {
            return name;
        }

    }
    
    private URI uri;

    private HttpMethod method;

    private Map<String, String> entity;

    private MediaType mediaType = MediaType.X_WWW_FORM_URLENCODED;

    private MediaType acceptMediaType;

    public Request(URI uri, HttpMethod method) {
        this(uri, method, null);
    }

    public Request(URI uri, HttpMethod method, Map<String, String> entity) {
        this.uri = uri;
        this.method = method;
        this.entity = entity;
    }

    public MediaType getAcceptMediaType() {
        return acceptMediaType;
    }

    public Request setAcceptMediaType(MediaType acceptMediaType) {
        this.acceptMediaType = acceptMediaType;
        return this;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Request setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
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

    public Request setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Request setEntity(Map<String, String> entity) {
        this.entity = entity;
        return this;
    }


}
