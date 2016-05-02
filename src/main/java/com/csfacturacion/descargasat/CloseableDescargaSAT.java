/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.descargasat;

import java.io.IOException;

/**
 *
 * @author emerino
 */
public interface CloseableDescargaSAT extends DescargaSAT {
    
    /**
     * Realiza el cierre de los recursos utilizados por la implementación
     * del DescargaSAT. Cada implementeación es responsable de informar si
     * el DescargaSAT puede ser usado después de cerrarse o no.
     * @throws IOException 
     */
    void close() throws IOException;
}
