# PadelGest

Projecte final del cicle formatiu de Desenvolupament d'Aplicacions Multiplataforma (DAM).

Aplicació per gestionar les pistes, els usuaris i les reserves d'un club de pàdel.

## Com arrencar el servidor

1. Crea la base de dades `padelgest` a PostgreSQL i executa
   `base_de_dades/001_crear_taules.sql`.
2. Copia `servidor/src/main/resources/application.properties.example`
   a `application.properties` (a la mateixa carpeta) i posa-hi
   la contrasenya de PostgreSQL i una clau JWT de més de 32 caràcters.
3. Arrenca `ServidorApplication` (o `./mvnw spring-boot:run` des de `servidor/`).

Els tests (`./mvnw test`) no necessiten PostgreSQL: fan servir una
base de dades H2 en memòria.