/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

/**
 *
 * @author emerino
 */
public class ConsultaInvalidaException extends Exception {

    public ConsultaInvalidaException(String message) {
        super(message);
    }

    public ConsultaInvalidaException(Throwable cause) {
        super(cause);
    }
    
}
