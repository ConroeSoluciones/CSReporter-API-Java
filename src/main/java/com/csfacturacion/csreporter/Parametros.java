/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

import java.util.Date;

/**
 * Clase que define los parámetros de búsqueda disponibles en el WS.
 *
 * @author emerino
 */
public class Parametros {

    public enum Status {

        VIGENTE,
        CANCELADO,
        TODOS
    }

    public enum Tipo {

        EMITIDAS,
        RECIBIDAS,
        TODAS
    }

    /**
     * Para las consultas de comprobantes recibidos, es necesario permitir
     * especificar el tipo de búsqueda que se quiere realizar, ya que en
     * ocasiones, el portal del SAT devuelve resultados erróneos si se busca en
     * rangos de tiempo que contienen muchos CFDIs (más de 500), en esos casos
     * es necesario acotar los rangos de búsqueda. Sin embargo, si se realiza
     * esta acción para todas las consultas, estas tardaran mucho más tiempo del
     * actual, siendo innecesario en la mayoría de los casos.
     *
     * @author emerino
     */
    public enum ModoBusqueda {

        NORMAL, // busca por día
        EXHAUSTIVA // busca por hora
    }

    public enum Servicio {

        CSREPORTER(8),
        CSDESCARGASAT(14);

        private final int numero;

        Servicio(int numero) {
            this.numero = numero;
        }

        public int getNumero() {
            return numero;
        }

    }

    private final String rfcBusqueda;

    private final Date fechaInicio;

    private final Date fechaFin;

    private final Status status;

    private final Tipo tipo;

    private final ModoBusqueda modoBusqueda;

    private final Servicio servicio;

    Parametros(ParametrosBuilder builder) {
        this.rfcBusqueda = builder.getRfcBusqueda();
        this.fechaInicio = builder.getFechaInicio();
        this.fechaFin = builder.getFechaFin();
        this.status = builder.getStatus();
        this.tipo = (builder.getTipo() != null)
                ? builder.getTipo()
                : Tipo.TODAS;
        this.servicio = builder.getServicio();
        this.modoBusqueda = builder.getModoBusqueda();
    }

    public ModoBusqueda getModoBusqueda() {
        return modoBusqueda;
    }

    public String getRfcBusqueda() {
        return rfcBusqueda;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public Status getStatus() {
        return status;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Servicio getServicio() {
        return servicio;
    }

}
