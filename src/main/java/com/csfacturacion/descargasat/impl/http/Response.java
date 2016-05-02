/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.descargasat.impl.http;

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
     * @return 
     */
    public JsonElement getAsJson() {
        return jsonParser.parse(rawResponse);
    }

    /**
     * Convierte la respuesta de acuerdo al TypeToken dado.
     * @param <T>
     * @param token
     * @return 
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
