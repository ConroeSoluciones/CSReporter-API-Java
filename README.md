# CSReporter Java API

Provee una API sencilla para realizar consultas al portal del SAT a través
de nuestro Web Service para CSReporter.

Consta de 2 interfaces principales:

    com.csfacturacion.CSReporter
    com.csfacturacion.Consulta

Las implementaciones de ambas interfaces se encargan de realizar las peticiones
HTTP a la API REST del WS, presentando una API sencilla para clientes finales.

Para ver ejemplos de uso, ver el archivo:

    src/tests/java/com/csfacturacion/csreporter/impl/CSReporterIT.java
