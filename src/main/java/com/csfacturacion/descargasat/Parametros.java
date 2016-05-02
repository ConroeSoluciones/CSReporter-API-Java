/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.descargasat;

import java.util.Date;

/**
 * Clase que define los parámetros de búsqueda disponibles en el WS, a través
 * de una API fluida.
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

    private RFC rfcBusqueda;

    private Date fechaInicio;

    private Date fechaFin = new Date();

    private Status status;

    private Tipo tipo;

    private Servicio servicio = Servicio.CSREPORTER;

    public Parametros servicio(Servicio servicio) {
        this.servicio = servicio;
        return this;
    }

    public Parametros rfcBusqueda(RFC emisor) {
        this.rfcBusqueda = emisor;
        return this;
    }

    public Parametros fechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
        return this;
    }

    public Parametros fechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
        return this;
    }

    public Parametros status(Status status) {
        this.status = status;
        return this;
    }

    public Parametros tipo(Tipo tipo) {
        this.tipo = tipo;
        return this;
    }

    public RFC getRfcBusqueda() {
        return rfcBusqueda;
    }

    public void setRfcBusqueda(RFC rfcBusqueda) {
        this.rfcBusqueda = rfcBusqueda;
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
