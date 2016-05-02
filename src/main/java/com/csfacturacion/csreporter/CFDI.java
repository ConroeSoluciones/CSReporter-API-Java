/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csfacturacion.csreporter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Una representación de un CFDI. Gestiona la información principal de un CFDI.
 * NOTE: El ID de esta entidad es el folio del CFDI real.
 *
 * @author emerino
 */
public class CFDI implements Comparable<CFDI>{

    /**
     * Tipo marcado en el CFDI.
     */
    public enum Tipo {

        INGRESO,
        EGRESO,
        TRASLADO
    }

    /**
     * Status del CFDI, de acuerdo a los valores reportados por el SAT.
     */
    public enum Status {

        CANCELADO,
        VIGENTE
    }


    private final UUID folio;

    private final RFC emisor;

    private final RFC receptor;

    private final Date fechaEmision;

    private final Date fechaCertificacion;

    private final RFC PACCertificador;

    private final BigDecimal total;

    private final Tipo tipo;

    private final Status status;

    private String xml;

    CFDI(CFDIBuilder builder) {
        this.folio = builder.getFolio();
        this.emisor = builder.getEmisor();
        this.receptor = builder.getReceptor();
        this.fechaEmision = builder.getFechaEmision();
        this.fechaCertificacion = builder.getFechaCertificacion();
        this.PACCertificador = builder.getPACCertificador();
        this.total = builder.getTotal();
        this.status = builder.getStatus();
        this.tipo = builder.getTipo();
    }

    public UUID getFolio() {
        return folio;
    }

    public RFC getEmisor() {
        return emisor;
    }

    public RFC getReceptor() {
        return receptor;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public Date getFechaCertificacion() {
        return fechaCertificacion;
    }

    public RFC getPACCertificador() {
        return PACCertificador;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.folio != null ? this.folio.hashCode() : 0);
        hash = 59 * hash + (this.status != null ? this.status.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CFDI other = (CFDI) obj;
        if ((this.folio == null) ? (other.folio != null) : !this.folio.equals(other.folio)) {
            return false;
        }
        return this.status == other.status;
    }

    @Override
    public int compareTo(CFDI o) {
        int comp = getFechaEmision().compareTo(o.getFechaEmision());
        if (comp == 0) {
            comp = getFolio().compareTo(o.getFolio());
        }
        return comp;
    }
}
