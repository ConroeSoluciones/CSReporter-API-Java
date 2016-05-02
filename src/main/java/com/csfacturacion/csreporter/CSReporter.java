/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

import java.util.UUID;

/**
 * Ésta es la interfaz principal para iniciar la comunicación con el WS.
 * A partir de aquí, se pueden realizar nuevas consultas, búsquedas de folios
 * previamente consultados y repetir consultas pasadas.
 *
 * @author emerino
 */
public interface CSReporter {
    
    /**
     * Realiza una consulta para obtener los CFDIs que correspondan de acuerdo
     * a los parámetros especificados. 
     * 
     * @param credenciales credenciales de acceso al SAT.
     * @param params los parámetros de búsqueda.
     * @return la consulta que contiene la funcionalidad para obtener el status 
     * y resultados de la misma.
     * @throws ConsultaInvalidaException si ocurre un problema con los 
     * parámetros de la consulta.
     */
    Consulta consultar(Credenciales credenciales, Parametros params)
            throws ConsultaInvalidaException;

    /**
     * Realiza una consulta para obtener los CFDIs que correspondan de acuerdo
     * a los parámetros especificados. Si se especifica el callback, éste será
     * llamado una vez la consulta se encuentre terminada.
     * 
     * @param credenciales credenciales de acceso al SAT.
     * @param params los parámetros de búsqueda.
     * @param listener implementación de un {@link ProgresoConsultaListener} que
     * será utilizado para manejar los cambios de {@link Consulta.Status} de
     * la consulta resultante.
     * @return la consulta que contiene la funcionalidad para obtener el status 
     * y resultados de la misma.
     * @throws ConsultaInvalidaException si ocurre un problema con los 
     * parámetros de la consulta.
     */
    Consulta consultar(Credenciales credenciales, Parametros params,
            ProgresoConsultaListener listener)
            throws ConsultaInvalidaException;
    
    /**
     * Es posible buscar consultas por folio específico, en caso que se hayan
     * realizado previamente y se quiera consultar sus resultados.
     * 
     * @param folio de la consulta previamente realizada.
     * @throws ConsultaInvalidaException si no se encuentra ninguna consulta 
     * con el folio especificado.
     * @return la consulta con el folio especificado.
     */
    Consulta buscar(UUID folio) throws ConsultaInvalidaException;

    /**
     * Es posible buscar consultas por folio específico, en caso que se hayan
     * realizado previamente y se quiera consultar sus resultados. Este método
     * permite utilizar un ProgresoConsultaListener, el cual es útil cuando
     * la consulta que se busca tiene status REPETIR, al ser utilizado este
     * método se comprobará automáticamente si tiene ese status, de ser así
     * se repite la consulta y se notifican los cambios de status al listener.
     * 
     * @param folio de la consulta previamente realizada.
     * @param listener que será utilizado para manejar los cambios de status.
     * @throws ConsultaInvalidaException si no se encuentra ninguna consulta 
     * con el folio especificado.
     * @return la consulta con el folio especificado.
     */
    Consulta buscar(UUID folio, ProgresoConsultaListener listener) 
            throws ConsultaInvalidaException;

    /**
     * Si la consulta con el folio dado está en status REPETIR, este método
     * repetirá la consulta para obtener los resultados necesarios.
     * 
     * @param folio de la consulta previamente realizada.
     * @throws ConsultaInvalidaException si no es posible repetir la consulta 
     * (e.g. status != "REPETIR" o no existe el folio).
     * @return la consulta que se repetirá.
     */
    Consulta repetir(UUID folio) throws ConsultaInvalidaException;

    /**
     * Si la consulta con el folio dado está en status REPETIR, este método
     * repetirá la consulta para obtener los resultados necesarios.
     * 
     * @param folio de la consulta previamente realizada.
     * @param listener implementación de un {@link ProgresoConsultaListener} que
     * será utilizado para manejar los cambios de {@link Consulta.Status} de
     * la consulta a repetir.
     * @throws ConsultaInvalidaException si no es posible repetir la consulta 
     * (e.g. status != "REPETIR" o no existe el folio).
     * @return la consulta que se repetirá.
     */
    Consulta repetir(UUID folio, ProgresoConsultaListener listener) 
            throws ConsultaInvalidaException;
}
