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
 * Una representación de los metadatos de un CFDI. Estos datos son los que
 * se encuentran disponibles en el portal del SAT, el contenido del CFDI
 * se encuentra como cadena en formato XML.
 * 
 * @author emerino
 */
public class CFDIMeta implements Comparable<CFDIMeta>{

    /**
     * Tipo marcado en el CFDIMeta.
     */
    public enum Tipo {

        INGRESO,
        EGRESO,
        TRASLADO
    }

    /**
     * Status del CFDIMeta, de acuerdo a los valores reportados por el SAT.
     */
    public enum Status {

        CANCELADO,
        VIGENTE
    }

    private UUID folio;

    private EmpresaFiscal emisor;

    private EmpresaFiscal receptor;

    private Date fechaEmision;

    private Date fechaCertificacion;

    private EmpresaFiscal PACCertificador;

    private BigDecimal total;

    private Tipo tipo;

    private Status status;

    protected CFDIMeta() {
    }

    CFDIMeta(CFDIMetaBuilder builder) {
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

    public EmpresaFiscal getEmisor() {
        return emisor;
    }

    public EmpresaFiscal getReceptor() {
        return receptor;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public Date getFechaCertificacion() {
        return fechaCertificacion;
    }

    public EmpresaFiscal getPACCertificador() {
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
        final CFDIMeta other = (CFDIMeta) obj;
        if ((this.folio == null) ? (other.folio != null) : !this.folio.equals(other.folio)) {
            return false;
        }
        return this.status == other.status;
    }

    @Override
    public int compareTo(CFDIMeta o) {
        int comp = getFechaEmision().compareTo(o.getFechaEmision());
        if (comp == 0) {
            comp = getFolio().compareTo(o.getFolio());
        }
        return comp;
    }
}
