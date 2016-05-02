/*
 * Copyright 2016 CSFacturación, todos los derechos reservados.
 */
package com.csfacturacion.descargasat;

/**
 * Credenciales genéricas de acceso.
 *
 * @author emerino
 */
public class Credenciales {
    
    private String usuario;

    private String password;

    public Credenciales(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }
    
}
