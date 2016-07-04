/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

import com.csfacturacion.csreporter.CFDIMeta;
import com.csfacturacion.csreporter.CFDIMeta.Status;
import com.csfacturacion.csreporter.CFDIMeta.Tipo;
import com.csfacturacion.csreporter.EmpresaFiscal;
import com.csfacturacion.csreporter.impl.ConsultaImpl;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author emerino
 */
public class CFDIMetaBuilder {

    private UUID folio;

    private EmpresaFiscal emisor;

    private EmpresaFiscal receptor;

    private Date fechaEmision = new Date();

    private Date fechaCertificacion;

    private EmpresaFiscal PACCertificador;

    private BigDecimal total;

    private Tipo tipo;

    private Status status;

    private final ConsultaImpl consulta;

    public CFDIMetaBuilder(ConsultaImpl consulta) {
        this.consulta = consulta;
    }

    public CFDIMetaBuilder folio(String folio) {
        return folio(UUID.fromString(folio.trim()));
    }

    public CFDIMetaBuilder folio(UUID folio) {
        this.folio = folio;
        return this;
    }

    public CFDIMetaBuilder emisor(EmpresaFiscal emisor) {
        this.emisor = emisor;
        return this;
    }

    public CFDIMetaBuilder receptor(EmpresaFiscal receptor) {
        this.receptor = receptor;
        return this;
    }

    public CFDIMetaBuilder fechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
        return this;
    }

    public CFDIMetaBuilder fechaCertificacion(Date fechaCertificacion) {
        this.fechaCertificacion = fechaCertificacion;
        return this;
    }

    public CFDIMetaBuilder PACCertificador(EmpresaFiscal certificador) {
        this.PACCertificador = certificador;
        return this;
    }

    public CFDIMetaBuilder total(BigDecimal total) {
        this.total = total;
        return this;
    }

    public CFDIMetaBuilder tipo(Tipo tipo) {
        this.tipo = tipo;
        return this;
    }

    public CFDIMetaBuilder status(Status status) {
        this.status = status;
        return this;
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

    public ConsultaImpl getConsulta() {
        return consulta;
    }

    public CFDIMeta build() {
            // TODO: Validate
        // se debe devolver una copia, para evitar que se pueda modificar
        // el CFDIMeta a través del builder después de ser creado.
        return new CFDIMeta(this);
    }
}
