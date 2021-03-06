/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

import com.csfacturacion.csreporter.Parametros.ModoBusqueda;
import com.csfacturacion.csreporter.Parametros.Servicio;
import com.csfacturacion.csreporter.Parametros.Status;
import com.csfacturacion.csreporter.Parametros.Tipo;
import java.util.Date;
import org.joda.time.DateTime;

/**
 * Builder para crear instancias de Parametros.
 *
 * @author emerino
 */
public class ParametrosBuilder {

    private String rfcBusqueda;

    private Date fechaInicio;

    private Date fechaFin = new Date();

    private Status status = Status.TODOS;

    private Tipo tipo;

    private Servicio servicio = Servicio.CSREPORTER;

    private ModoBusqueda modoBusqueda = ModoBusqueda.NORMAL;

    public ModoBusqueda getModoBusqueda() {
        return modoBusqueda;
    }

    public ParametrosBuilder modoBusqueda(ModoBusqueda modoBusqueda) {
        this.modoBusqueda = modoBusqueda;
        return this;
    }

    /**
     * El Servicio a utilizar en la consulta, por defecto CSREPORTER.
     *
     * @param servicio a utilizar.
     * @return este builder, para encadenamiento.
     */
    public ParametrosBuilder servicio(Servicio servicio) {
        this.servicio = servicio;
        return this;
    }

    /**
     * El RFC del emisor/receptor, según el status definido.
     *
     * @param rfc a buscar.
     * @return este builder, para encadenamiento.
     */
    public ParametrosBuilder rfcBusqueda(String rfc) {
        this.rfcBusqueda = rfc;
        return this;
    }

    /**
     * Fecha de emisión inicial de los CFDIs a buscar.
     *
     * @param fechaInicio para buscar.
     * @return este builder, para encadenamiento.
     */
    public ParametrosBuilder fechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
        return this;
    }

    /**
     * Fecha de emisión final de los CFDIs a buscar.
     *
     * @param fechaFin para buscar.
     * @return este builder, para encadenamiento.
     */
    public ParametrosBuilder fechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
        return this;
    }

    /**
     * El status de los CFDIs a buscar, por defecto es TODOS.
     *
     * @param status a buscar.
     * @return este builder, para encadenamiento.
     */
    public ParametrosBuilder status(Status status) {
        if (status != null) {
            this.status = status;
        }
        return this;
    }

    /**
     * El tipo de comprobantes a buscar (emitidos/recibidos).
     *
     * @param tipo de comprobante.
     * @return este builder, para encadenamiento.
     */
    public ParametrosBuilder tipo(Tipo tipo) {
        this.tipo = tipo;
        return this;
    }

    public Parametros build() {
        if (new DateTime(fechaInicio).withTimeAtStartOfDay().compareTo(
                new DateTime(fechaFin).withTimeAtStartOfDay()) > 0) {

            throw new IllegalStateException("La fecha de inicio debe ser <= fechaFin");
        }

        return new Parametros(this);
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
