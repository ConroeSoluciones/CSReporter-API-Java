/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

/**
 * Permite verificar el estado de una consulta en curso.
 *
 * @author emerino
 */
public interface ProgresoConsultaListener {
    
    /**
     * Este método es llamado cada vez que cambia el {@link Consulta.Status} de
     * una consulta en curso.
     * 
     * @param status actual de la consulta.
     */
    void onStatusChanged(Consulta status);
}
