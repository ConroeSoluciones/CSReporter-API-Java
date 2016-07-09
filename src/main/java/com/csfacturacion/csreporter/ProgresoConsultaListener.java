/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

import com.csfacturacion.csreporter.Consulta.Status;

/**
 * Permite verificar el estado de una consulta en curso.
 *
 * @author emerino
 */
public interface ProgresoConsultaListener {
    
    /**
     * Este método es llamado cada vez que cambia el {@link Consulta.Status} de
     * una consulta en curso. Es importante leer el status de la consulta desde
     * la variable provista, ya que los métodos que checan el status en el 
     * objeto consulta realizan una llamada al webservice.
     * 
     * @param status actual de la consulta.
     * @param consulta la consulta cuyo status ha cambiado.
     */
    void onStatusChanged(Status status, Consulta consulta);
}
