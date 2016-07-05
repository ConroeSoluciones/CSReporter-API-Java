/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

/**
 *
 * @author emerino
 */
public class ConsultaInexistenteException extends ConsultaInvalidaException {

    public ConsultaInexistenteException(String message) {
        super(message);
    }

    public ConsultaInexistenteException(Throwable cause) {
        super(cause);
    }
    
}
