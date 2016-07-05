/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 * Facilita la validación y manejo general de una empresa fiscal
 * (rfc + razón social).
 *
 * @author emerino
 */
@Embeddable
public class EmpresaFiscal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String rfc;

    @Lob
    @Column(nullable = true)
    private String razonSocial;

    protected EmpresaFiscal() {
    }

    public EmpresaFiscal(String rfc, String razonSocial) {
        this.rfc = rfc.trim().toUpperCase();
        this.razonSocial = razonSocial;
    }

    public String getRfc() {
        return rfc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmpresaFiscal other = (EmpresaFiscal) obj;
        if (!Objects.equals(this.rfc, other.rfc)) {
            return false;
        }
        return Objects.equals(this.razonSocial, other.razonSocial);
    }

}

