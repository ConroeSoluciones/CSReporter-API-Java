/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

/**
 * Facilita la validación y manejo general de un RFC.
 *
 * @author emerino
 */
public class RFC {

    private String rfc;

    protected RFC() {
    }

    public RFC(String rfc) {
        this.rfc = rfc.trim().toUpperCase();
    }

    @Override
    public String toString() {
        return rfc;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.rfc != null ? this.rfc.hashCode() : 0);
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
        final RFC other = (RFC) obj;
        return !((this.rfc == null) ? (other.rfc != null) : !this.rfc.equals(other.rfc));
    }
    
}

