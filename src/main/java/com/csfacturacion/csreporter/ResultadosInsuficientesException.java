/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

/**
 *
 * @author emerino
 */
public class ResultadosInsuficientesException extends RuntimeException {

    public ResultadosInsuficientesException(Throwable cause) {
        super(cause);
    }

    public ResultadosInsuficientesException(String message) {
        super(message);
    }
    
}
