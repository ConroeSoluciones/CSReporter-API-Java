/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.csreporter;

import java.io.IOException;

/**
 *
 * @author emerino
 */
public interface CloseableCSReporter extends CSReporter {
    
    /**
     * Realiza el cierre de los recursos utilizados por la implementación
 del CSReporter. Cada implementeación es responsable de informar si
 el CSReporter puede ser usado después de cerrarse o no.
     * @throws IOException 
     */
    void close() throws IOException;
}
