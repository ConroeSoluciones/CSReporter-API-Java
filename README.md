# CSReporter Java API

Provee una API sencilla para realizar consultas al portal del SAT a través
de nuestro Web Service para CSReporter.

Consta de 2 interfaces principales:

    com.csfacturacion.csreporter.CSReporter
    com.csfacturacion.csreporter.Consulta

Las implementaciones de ambas interfaces se encargan de realizar las peticiones
HTTP a la API REST del WS, presentando una API sencilla para clientes finales.

# Dependencias

* Java SE 5+
* [Apache Maven](http://maven.apache.org/)

# Instalación

La API se encuentra en el repositorio central de Maven, por lo que sólo se 
debe incluir la dependencia en el POM del proyecto donde se quiera utilizar.

    <dependency>
        <groupId>com.csfacturacion.csreporter</groupId>
        <artifactId>csreporter-api-java</artifactId>
        <version>1.0</version>
    </dependency>

# Construcción e instalación del proyecto

Ejecutar el siguiente comando para construir e instalar la API en el repositorio
local de maven:

    mvn install


# Documentación

Para generar la documentación del proyecto, ejecutar el siguiente comando:

    mvn site

Esto generará la documentación en la carpeta "target/site", comenzar a navegar
por el archivo index.html.

# Ejemplos de uso

Algunos ejemplos de uso:

    // credenciales para CSFacturación
    Credenciales csCredenciales = new Credenciales("XXXXXXXXXXXXX", "pass"));

    // inicializar un CSReporter
    CSReporter csReporter = new CSReporterImpl(csCredenciales);
            
    // credenciales para el portal del SAT
    Credenciales satCredenciales = new Credenciales("XXXXXXXXXXXXX", "pass"));

    // obtener todos los CFDIs emitidos en el periodo de 2014-01-01 a las 
    // 00:00:00 horas hasta 2015-12-31 a las 23:59:59
    Consulta consulta = csReporter.consultar(satCredenciales,
            new ParametrosBuilder()
            .tipo(Parametros.Tipo.EMITIDAS)
            .status(Parametros.Status.TODOS)
            .fechaInicio(new DateTime()
                    .withDate(2014, 1, 1)
                    .withTimeAtStartOfDay()
                    .toDate())
            .fechaFin(new DateTime()
                    .withDate(2015, 12, 31)
                    .withTime(23, 59, 59, 0)
                    .toDate())
            .build(),
            new ProgresoConsultaListener() {

             1   @Override
                public void onStatusChanged(Consulta consulta) {
                    // verificar status y hacer algo con los resultados
                    if (consulta.isTerminada()) {
                        for (int i = 1; i <= consulta.getPaginas(); i++) {
                            // obtener todos los CFDIs de la página i
                            List<CFDI> cfdis = consulta.getResultados(i);
                        }
                    }
                }
            });

Para más ejemplos, ver el archivo:

    src/tests/java/com/csfacturacion/csreporter/impl/CSReporterIT.java
