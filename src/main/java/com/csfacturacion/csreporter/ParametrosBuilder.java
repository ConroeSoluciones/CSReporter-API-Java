/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

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

    private EmpresaFiscal rfcBusqueda;

    private Date fechaInicio;

    private Date fechaFin = new Date();

    private Status status = Status.TODOS;

    private Tipo tipo;

    private Servicio servicio = Servicio.CSREPORTER;

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
    public ParametrosBuilder rfcBusqueda(EmpresaFiscal rfc) {
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
        if (this.tipo == null) {
            throw new IllegalStateException("Se debe definir un tipo para la consulta");
        }

        if (new DateTime(fechaInicio).withTimeAtStartOfDay().compareTo(
                new DateTime(fechaFin).withTimeAtStartOfDay()) > 0) {

            throw new IllegalStateException("La fecha de inicio debe ser <= fechaFin");
        }

        return new Parametros(this);
    }

    EmpresaFiscal getRfcBusqueda() {
        return rfcBusqueda;
    }

    Date getFechaInicio() {
        return fechaInicio;
    }

    Date getFechaFin() {
        return fechaFin;
    }

    Status getStatus() {
        return status;
    }

    Tipo getTipo() {
        return tipo;
    }

    Servicio getServicio() {
        return servicio;
    }

}
