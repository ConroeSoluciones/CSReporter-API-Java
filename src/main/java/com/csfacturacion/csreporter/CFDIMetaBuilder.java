/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

import com.csfacturacion.csreporter.CFDIMeta.Status;
import com.csfacturacion.csreporter.CFDIMeta.Tipo;
import com.csfacturacion.csreporter.impl.ConsultaImpl;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author emerino
 */
public abstract class CFDIMetaBuilder<
        C extends CFDIMeta, B extends CFDIMetaBuilder<C, B>> {

    private UUID folio;

    private EmpresaFiscal emisor;

    private EmpresaFiscal receptor;

    private Date fechaEmision = new Date();

    private Date fechaCertificacion;

    private EmpresaFiscal PACCertificador;

    private BigDecimal total;

    private Tipo tipo;

    private Status status;

    protected abstract B thisBuilder();

    public B folio(String folio) {
        return folio(UUID.fromString(folio.trim()));
    }

    public B folio(UUID folio) {
        this.folio = folio;
        return thisBuilder();
    }

    public B emisor(EmpresaFiscal emisor) {
        this.emisor = emisor;
        return thisBuilder();
    }

    public B receptor(EmpresaFiscal receptor) {
        this.receptor = receptor;
        return thisBuilder();
    }

    public B fechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
        return thisBuilder();
    }

    public B fechaCertificacion(Date fechaCertificacion) {
        this.fechaCertificacion = fechaCertificacion;
        return thisBuilder();
    }

    public B PACCertificador(EmpresaFiscal certificador) {
        this.PACCertificador = certificador;
        return thisBuilder();
    }

    public B total(BigDecimal total) {
        this.total = total;
        return thisBuilder();
    }

    public B tipo(Tipo tipo) {
        this.tipo = tipo;
        return thisBuilder();
    }

    public B status(Status status) {
        this.status = status;
        return thisBuilder();
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

    public abstract C build();
}
