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
        RECIBIDAS
    }

    public enum Servicio {

        CSREPORTER(8),
        CSDESCARGASAT(11);

        private final int numero;

        Servicio(int numero) {
            this.numero = numero;
        }

        public int getNumero() {
            return numero;
        }

    }

    private final RFC rfcBusqueda;

    private final Date fechaInicio;

    private final Date fechaFin;

    private final Status status;

    private final Tipo tipo;

    private final Servicio servicio;

    Parametros(ParametrosBuilder builder) {
        this.rfcBusqueda = builder.getRfcBusqueda();
        this.fechaInicio = builder.getFechaInicio();
        this.fechaFin = builder.getFechaFin();
        this.status = builder.getStatus();
        this.tipo = builder.getTipo();
        this.servicio = builder.getServicio();
    }

    public RFC getRfcBusqueda() {
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
