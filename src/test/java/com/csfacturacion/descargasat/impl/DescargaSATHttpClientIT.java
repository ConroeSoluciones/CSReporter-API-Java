/*
 * Copyright 2016 NueveBit, todos los derechos reservados.
 */
package com.csfacturacion.descargasat.impl;

import com.csfacturacion.descargasat.CFDI;
import com.csfacturacion.descargasat.Consulta;
import com.csfacturacion.descargasat.ConsultaInvalidaException;
import com.csfacturacion.descargasat.Credenciales;
import com.csfacturacion.descargasat.Parametros;
import com.csfacturacion.descargasat.ProgresoConsultaListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * Realiza pruebas de integración con el webservice de descargas.
 *
 * Las credenciales, tanto el para CSFacturación como para el portal del SAT, se
 * encuentran en los archivos test/resources/csCredenciales.json y
 * test/resources/satCredenciales.json respectivamente. Es necesario crear
 * manualmente estos archivos, ver los archivos .template que llevan el mismo
 * nombre para tomarlos como referencia.
 *
 * También se necesita el archivo test/resources/config.json, con los parámetros
 * que ahí se enlistan.
 *
 * NOTE: Sólo están habilitadas las operaciones de sólo lectura, aquellas que
 * generan un cambio en el servidor están anotadas con @Ignore.
 *
 * @author emerino
 */
public class DescargaSATHttpClientIT {

    private static DescargaSATHttpClient descargaSAT;

    private static Credenciales csCredenciales;

    private static Credenciales satCredenciales;

    private static UUID consultaFolio;

    private static UUID cfdiFolio;

    private static String cfdiXml;

    private volatile boolean consultaTerminada;

    public DescargaSATHttpClientIT() {
    }

    @BeforeClass
    public static void globalSetup() throws Exception {
        Gson gson = new GsonBuilder().create();
        csCredenciales = gson.fromJson(
                IOUtils.toString(DescargaSATHttpClientIT.class
                        .getResourceAsStream("/csCredenciales.json")),
                Credenciales.class);

        satCredenciales = gson.fromJson(
                IOUtils.toString(DescargaSATHttpClientIT.class
                        .getResourceAsStream("/satCredenciales.json")),
                Credenciales.class);

        descargaSAT = new DescargaSATHttpClient(csCredenciales, 2000);

        JsonParser jsonParser = new JsonParser();
        JsonObject config = jsonParser.parse(new InputStreamReader(
                DescargaSATHttpClientIT.class
                .getResourceAsStream("/config.json"))).getAsJsonObject();

        consultaFolio = UUID.fromString(config.get("consultaFolio")
                .getAsString());

        cfdiFolio = UUID.fromString(config.get("cfdiFolio").getAsString());
        cfdiXml = config.get("cfdiXml").getAsString().trim();
    }

    public void setup() {
        consultaTerminada = false;
    }

    @Test
    @Ignore
    public void consultarAsync() throws Exception {
        // este método termina al obtener el UUID de la consulta realizada,
        // recibe el listener (callback) como parámetro, el cuál será ejecutado
        // cada vez que cambie el status de la consulta
        Consulta consulta = descargaSAT.consultar(
                satCredenciales,
                new Parametros()
                .tipo(Parametros.Tipo.EMITIDAS)
                .status(Parametros.Status.TODOS)
                .fechaInicio(new DateTime()
                        .withDate(2015, 1, 1)
                        .withTimeAtStartOfDay()
                        .toDate())
                .fechaFin(new DateTime()
                        .withDate(2015, 1, 10)
                        .withTimeAtStartOfDay()
                        .withTime(23, 59, 59, 0)
                        .toDate()),
                new ProgresoConsultaListener() {

                    @Override
                    public void onStatusChanged(Consulta consulta) {
                        // todo lo que hay en este método se ejecuta en un 
                        // Thread distinto, cada vez que hay un cambio de estado
                        // en la consulta.
                        DescargaSATHttpClientIT.this.onStatusChanged(consulta);
                    }
                });

        esperarConsulta(consulta);
    }

    @Test
    @Ignore
    public void consultarSync() throws Exception {
        // este método devuelve la consutla de inmediato, como no toma
        // el listener (callback) como parámetro, esta funcionalidad queda
        // a cargo del código cliente
        Consulta consulta = descargaSAT.consultar(
                satCredenciales,
                new Parametros()
                .tipo(Parametros.Tipo.EMITIDAS)
                .status(Parametros.Status.TODOS)
                .fechaInicio(new DateTime()
                        .withDate(2015, 1, 1)
                        .withTimeAtStartOfDay()
                        .toDate())
                .fechaFin(new DateTime()
                        .withDate(2015, 1, 10)
                        .withTimeAtStartOfDay()
                        .withTime(23, 59, 59, 0)
                        .toDate()));

        while (!consulta.isTerminada()) {
            System.out.println(consulta.getStatus());
            Thread.sleep(1000);
        }

        assertTrue(consulta.isTerminada());
    }

    @Test
    public void buscarSync() throws Exception {
        Consulta consulta = descargaSAT.buscar(consultaFolio);

        assertTrue(consulta.isRepetir() || consulta.isTerminada());
    }

    @Test
    public void buscarAsync() throws Exception {
        Consulta consulta = descargaSAT.buscar(consultaFolio,
                new ProgresoConsultaListener() {

                    @Override
                    public void onStatusChanged(Consulta c) {
                        System.out.println(c.getStatus());
                        DescargaSATHttpClientIT.this.onStatusChanged(c);
                    }
                });

        esperarConsulta(consulta);

        assertTrue(consulta.isTerminada());
    }

    @Test(expected = ConsultaInvalidaException.class)
    public void buscarConsultaInexistente() throws ConsultaInvalidaException {
        // a menos que exista el random UUID, debe lanzar excepción
        Consulta consulta = descargaSAT.buscar(UUID.randomUUID());
    }

    @Test
    @Ignore
    public void repetirConsultaAnterior() throws Exception {
        Consulta consulta = descargaSAT.repetir(
                consultaFolio,
                new ProgresoConsultaListener() {

                    @Override
                    public void onStatusChanged(Consulta status) {
                        DescargaSATHttpClientIT.this.onStatusChanged(status);
                    }
                });

        esperarConsulta(consulta);
    }

    @Test(expected = ConsultaInvalidaException.class)
    public void repetirInexistente() throws ConsultaInvalidaException {
        descargaSAT.repetir(UUID.randomUUID(), null);
    }

    @Test
    public void iterarResultados() throws Exception {
        descargaSAT.buscar(consultaFolio,
                new ConsultaTerminadaListener() {

                    @Override
                    public void onTerminada(Consulta c) {
                        assertTrue(!c.isFallo());
                        assertTrue(c.getTotalResultados() > 0);
                        assertTrue(c.getPaginas() > 0);

                        for (int i = 1; i <= c.getPaginas(); i++) {
                            // cada lista contiene hasta 20 cfdis, estos NO
                            // se almacenan en memoria, son descartados tan 
                            // pronto como deje de usarse la lista devuelta
                            List<CFDI> resultados = c.getResultados(i);

                            for (CFDI cfdi : resultados) {
                                // trabajar con el CFDI
                                assertTrue(cfdi.getFolio() != null);
                            }
                        }
                    }
                });
    }

    @Test
    @Ignore
    public void getCFDIDirecto() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void getCFDIXML() throws Exception {
        descargaSAT.buscar(consultaFolio,
                new ConsultaTerminadaListener() {

                    @Override
                    public void onTerminada(Consulta consulta) {
                        String xml = consulta.getCFDIXML(cfdiFolio);

                        assertTrue(xml != null);
                        //assertEquals(cfdiXml, xml.trim());
                    }
                });
    }

    private void onStatusChanged(Consulta consulta) {
        if (consulta.isTerminada()) {
            consultaTerminada = true;
        }
    }

    private void esperarConsulta(Consulta consulta) throws InterruptedException {
        while (!consultaTerminada) {
            System.out.println(consulta.getStatus());
            Thread.sleep(1000);
        }

        assertTrue(consulta.isTerminada());
    }

    @AfterClass
    public static void globalClose() throws IOException {
        descargaSAT.close();
    }

    /**
     * Contiene un método que se ejecuta cuando la consulta se encuentra
     * terminada.
     */
    private abstract static class ConsultaTerminadaListener
            implements ProgresoConsultaListener {

        @Override
        public final void onStatusChanged(Consulta consulta) {
            if (consulta.isTerminada()) {
                onTerminada(consulta);
            }
        }

        public abstract void onTerminada(Consulta consulta);

    }
}
