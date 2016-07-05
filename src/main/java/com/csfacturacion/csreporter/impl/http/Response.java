/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter.impl.http;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Simplificación de una respuesta HTTP.
 *
 * @author emerino
 */
public class Response {

    private String rawResponse;
    private static final JsonParser jsonParser = new JsonParser();
    private final Gson gson;
    private int code;

    public Response(Gson gson, String rawResponse, int code) {
        this.rawResponse = rawResponse;
        this.gson = gson;
        this.code = code;
    }

    /**
     * Convierte la respuesta a una estructura JSON.
     * @return la estructura en formato Json (JsonElement)
     */
    public JsonElement getAsJson() {
        return jsonParser.parse(rawResponse);
    }

    /**
     * Convierte la respuesta de acuerdo al TypeToken dado.
     * @param <T> El tipo de los datos que serán convertidos.
     * @param token El typetoken que contiene el tipo a usar.
     * @return el objeto convertido al tipo provisto.
     */
    public <T> T getAs(TypeToken<T> token) {
        T object = gson.fromJson(rawResponse, token.getType());

        return object;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public int getCode() {
        return code;
    }

}
