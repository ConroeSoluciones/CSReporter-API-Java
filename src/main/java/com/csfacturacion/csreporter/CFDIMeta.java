/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csfacturacion.csreporter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Una representación de los metadatos de un CFDI. Estos datos son los que
 * se encuentran disponibles en el portal del SAT, el contenido del CFDI
 * se encuentra como cadena en formato XML.
 * 
 * NOTE: La clase incluye anotaciones JPA, por lo que está lista para ser
 * extendida y proveer el nombre de la tabla que tendrá en la db.
 *
 * @author emerino
 */
@MappedSuperclass
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

    @Column(length = 36, nullable = false, unique = true)
    private String folio;

    @AttributeOverrides({
        @AttributeOverride(name = "rfc", column = @Column(name = "rfcEmisor")),
        @AttributeOverride(name = "razonSocial",
                column = @Column(name = "razonSocialEmisor"))
    })
    @Embedded
    private EmpresaFiscal emisor;

    @AttributeOverrides({
        @AttributeOverride(name = "rfc", column = @Column(name = "rfcReceptor")),
        @AttributeOverride(name = "razonSocial",
                column = @Column(name = "razonSocialReceptor"))
    })
    @Embedded
    private EmpresaFiscal receptor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEmision;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCertificacion;

    @AttributeOverrides({
        @AttributeOverride(name = "rfc",
                column = @Column(name = "rfcPACCertificador")),
        @AttributeOverride(name = "razonSocial",
                column = @Column(name = "razonSocialPACCertificador"))
    })
    private EmpresaFiscal PACCertificador;

    @Column(precision = 11, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected CFDIMeta() {
    }

    protected CFDIMeta(CFDIMetaBuilder<?, ?> builder) {
        this.folio = builder.getFolio().toString();
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
        return UUID.fromString(folio);
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

    public void setStatus(Status status) {
        this.status = status;
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
