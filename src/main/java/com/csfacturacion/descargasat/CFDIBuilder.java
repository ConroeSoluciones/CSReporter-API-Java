/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.descargasat;

import com.csfacturacion.descargasat.CFDI;
import com.csfacturacion.descargasat.CFDI.Status;
import com.csfacturacion.descargasat.CFDI.Tipo;
import com.csfacturacion.descargasat.RFC;
import com.csfacturacion.descargasat.impl.ConsultaHttpClient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author emerino
 */
public class CFDIBuilder {

    private UUID folio;

    private RFC emisor;

    private RFC receptor;

    private Date fechaEmision = new Date();

    private Date fechaCertificacion;

    private RFC PACCertificador;

    private BigDecimal total;

    private Tipo tipo;

    private Status status;

    private final ConsultaHttpClient consulta;

    public CFDIBuilder(ConsultaHttpClient consulta) {
        this.consulta = consulta;
    }

    public CFDIBuilder folio(String folio) {
        return folio(UUID.fromString(folio.trim()));
    }

    public CFDIBuilder folio(UUID folio) {
        this.folio = folio;
        return this;
    }

    public CFDIBuilder emisor(RFC emisor) {
        this.emisor = emisor;
        return this;
    }

    public CFDIBuilder receptor(RFC receptor) {
        this.receptor = receptor;
        return this;
    }

    public CFDIBuilder fechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
        return this;
    }

    public CFDIBuilder fechaCertificacion(Date fechaCertificacion) {
        this.fechaCertificacion = fechaCertificacion;
        return this;
    }

    public CFDIBuilder PACCertificador(RFC certificador) {
        this.PACCertificador = certificador;
        return this;
    }

    public CFDIBuilder total(BigDecimal total) {
        this.total = total;
        return this;
    }

    public CFDIBuilder tipo(Tipo tipo) {
        this.tipo = tipo;
        return this;
    }

    public CFDIBuilder status(Status status) {
        this.status = status;
        return this;
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

    public ConsultaHttpClient getConsulta() {
        return consulta;
    }

    public CFDI build() {
            // TODO: Validate
        // se debe devolver una copia, para evitar que se pueda modificar
        // el CFDI a través del builder después de ser creado.
        return new CFDI(this);
    }
}
