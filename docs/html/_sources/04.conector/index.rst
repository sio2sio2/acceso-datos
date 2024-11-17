.. _conn:

Conectores a bases de datos relacionales
****************************************
Para el acceso a bases de datos relacionales hay dos estrategias:

#. El uso de conectores, en que el acceso a la base de datos se realiza mediante
   sentencias |SQL| y la traducción del modelo relacional al modelo de objetos
   corre a cargo del programador.

#. El uso de herramientas |ORM| en que la traducción entre ambos modelos corre a
   cargo de la propia herramienta siguiendo las orientaciones del programador.

Podemos entender mejor estas dos estrategias, si las comparamos con estrategias
ya vistas anteriormente para acceder a otros soportes de datos:

- La primera estrategia es la misma que sigue la librería commons-csv_ para leer
  y escribir |CSV|.
- La segunda estrategia es la que sigue la `librería Jackson`_ para leer y
  escribir distintos formatos.

.. todo:: Ventajas e inconvenientes de cada cual.

En esta unidad abordaremos la primera estrategia y dejaremos la segunda para la
:ref:`unidad siguiente <orm>`.

Java proporciona para el acceso a bases de datos relacionales una |API| en su
|JDK| llamada |JDBC|, lo que posibilita que el acceso sea idéntico sea cual sea
el |SGBD| que se decida utilizar. Esto no significa que el código sea
independiente del motor subyacente, puesto que cada motor tiene sus
particularidades que extienden el |SQL| estándar y esta estrategia, al estar
basada en la construcción de sentencias |SQL|, nos obliga a utilizarlas. Lo que
en realidad es común son los métodos que proporciona Java para ordenar la
ejecución de las las sentencias al motor de la base de datos. Además de la
|API|, necesitaremos para cada |SGBD| un *driver* basado en la |API| que
posibilite la conexión. Este *driver* sí que será una librería de terceros que
deberemos incluir en nuestro proyecto.

Aperitivo
=========
Para aprender y practicar el acceso con conectores, podemos escoger cualquier
|SGBD|. Utilizaremos en el caso de estos apuntes, SQLite_ por varias razones:

+ Comodidad, ya que a diferencia de otros (MariaDB_, Oracle_, etc) no requiere
  un servidor: la base de datos es un archivo.
+ Es una base de datos empotrada y se usa mucha en aplicaciones que necesitan
  organizar sus datos en una base de datos exclusiva en la que no
  concurrirán otros procesos.

Con la excepción de las propias sentencias |SQL| que pueden variar de |SGBD| o
|SGBD|, las explicaciones serán totalmente válidas. Antes, no obstante, es
necesario que tengamos **instalado el motor**. En el caso de **SQLite**:

.. _rst-simple:

* Las distribuciones de *Linux* suelen incluir un paquete por lo que su
  instalación es trivial.
* Para sistemas *Windows* la página oficial ofrece `binarios precompilados
  <https://sqlite.org/download.html#win32>`_, pero no un instalador automático.
  La instalación, no obstante, es sencilla:

  a. Guardar los archivos suministrados dentro del *zip* en un lugar adecuado
  (p.e. :file:`C:\\Program Files\\SQLite`).
  #. Añadir el directorio al `PATH` del sistema.

.. note:: Procuramos escribir sentencias |SQL| que cumplan el estándar para que
   el código sea lo más portable posible.

Además, necesitaremos importar en nuestro proyecto la librería propia de SQLite: sqlite-jdbc_.

.. rubric:: Ejercicio ilustrativo

Tomemos como base el :ref:`ejercicio ilustrativo con que introducimos XML
<xml>`, aunque con algunos cambios:

+ Puede haber varios claustros.
+ Un mismo profesor puede trabajar en varios claustro diferentes.
+ Si se da la anterior circunstancia, el profesor se adcribirá a un departamento
  por centro, pero no tiene que ser para todos los centros el mismo.

Esto podemos representarlo gráficamente con el siguente diagrama |E/R|:

.. image:: files/ER-problema.png

Podemos traducir el anterior esquema al modelo relacional así:

.. code-block:: none

   Claustro(*idClaustro*, nombre)
   Profesor(*idProfesor*, casillero, sustituye, apelativo, apellidos, nombre)
   Departamento(*idDepartamento*, denominacion)
   Trabaja(*idProfesor, idClaustro*, departamento)

.. _conn-conexion:

Conexión
========
Lo primero que debemos aprender es cómo abrir una conexión a la base de datos:

.. code-block:: java
   :emphasize-lines: 1, 5, 11

   final String protocol = "jdbc:sqlite:";

   // Las bases de datos de SQLite son archivos.
   Path dbPath = Path.of(System.getProperty("java.io.tmpdir"), "test.db");
   String dbUrl = String.format("%s%s", dbProtocol, dbPath);

   // Alternativa particular de SQLite: base de datos en memoria.
   //String dbUrl = String.format("%s%s", dbProtocol, ":memory:");

   try (
      Connection conn = DriverManager.getConnection(dbUrl);
   ) {
      System.out.println("Hemos logrado conectar a la base de datos");
   }
   catch(SQLException err) {
      System.err.println("Error de conexión. " + err.getMessage());
   }

En el código de ejemplo, hay tres claves fundamentales:

* La |URL| de conexión que se compone, a su vez de:

  - El protocolo que identifica al |SGBD|
  - La indicación de la base de datos a la que queremos conectar.

  Esta |URL| depende del |SGBD|, así que tendremos que tener presente cuál
  estamos utilizando al construirla:

  .. table::
     :class: url-sql

     ============ ======================== ==============================================================
      |SGBD|       Librería |JDBC|          |URL|\ [#]_
     ============ ======================== ==============================================================
      SQLite       `sqlite-jdbc`_           `jdbc:sqlite:<ruta>`
      MariaDB      `mariadb-java-client`_   `jdbc:mariadb://[<host>[:<port>]]/<base_datos>`
      MySQL        `mysql-connector-j`_     `jdbc:mysql://[<host>[:<port>]]/[base_datos]`
      PostgreSQL   postgresql_              `jdbc:postgresql://[<host>[:<port>]]/<base_datos>`
      Oracle       ojdbc11_                 `jdbc:oracle:thin:@//<host>[:<port>]/<SID/ServerName>`
      SQL Server   `mssql-jdbc`_            `jdbc:sqlserver://<host>[:<port>];databaseName=<base_datos>`
     ============ ======================== ==============================================================

* El objeto de conexión, construido de esta manera para que se cierre automáticamente.
* Un objeto para generar sentencias |SQL| del que hablaremos bajo el próximo
  epígrafe. También es necesario cerrarlo y, en este ejemplo, lo hemos creado
  simplemente para ilustrar cómo obligar a *SQLite* a respetar la integridad
  referencial (por defecto, no lo hace).

Por supuesto, el código es *completamente inútil*: nos hemos conectado a la base
de datos para no hacer absolutamente nada. En los siguientes apartados veremos
como leer y escribir datos.

.. _conn-statement:

Ejecución de sentencias
=======================
El acceso mediante conectores se basa en la ejecución de sentencias |SQL| que
pueden ser de dos tipos:

+ Sentencias que modifican la base de datos.
+ Sentencias que obtienen datos sin llevar a cabo modificación alguna.

A partir de un objeto :java-sql:`Statement <Statement>`, para las primeras se
usa el método ``executeUpdate``, mientras que para las segundas el método
``executeQuery``.

.. todo:: Hablar de ``execute``.

Simples
-------
Comencemos creado cuatro tablas:

.. literalinclude:: files/01.create.java
   :language: java

El código tiene dos aspectos:

#. Las sentencias |SQL| que son sentencias |SQL| cuya comprensión no
   forma parte de los objetivos de esta unidad\ [#]_, así que no entraremos en
   comentarlas.
#. La ejecución de esas sentencias usando |JDBC|. Obsérvese que:

  + Usamos un único objeto :java-sql:`Statement <Statement>` para todas ellas,
    porque es lícito.
  + Las ejecutamos usando el método ``executeUpdate`` puesto que su función es
    modificar el contenido de la base de datos, no obtener información.

Tampoco las sentencias de inserción devuelven resultados, por lo que también
podemos hacer:

.. _ej-insert-sin-par:

.. code-block:: java

      // Agregamos algunos departamentos.
      stmt.executeUpdate("""
          INSERT INTO "Departamento" ("denominacion") VALUES ('Informática');
      """);
      for(String denominacion: new String[] {"Inglés", "Francés", "Tecnología"}) {
          stmt.executeUpdate(String.format("""
              INSERT INTO "Departamento" ("denominacion") VALUES ('%s');
          """, denominacion));
      }")

.. borrar)))

.. caution:: Aún estamos empezando y sabemos poco así que estas sentencias son
   torpes por dos razones:

   * Estamos ejecutando repetidamente (cuatro veces) la misma sentencia y no es
     eficiente. Ya aprenderemos cómo hacer :ref:`operaciones masivas <conn-batch>`.
   * Las sentencias usan valores almacenados en variables y hay que construirlas
     incluyendo esos valores dentro de las sentencias. Para ello nosotros hemos
     usado ``String.format``. Sin embargo, |JDBC| ya viene con un mecanismo
     específico para realizar esta labor: las :ref:`sentencias parametrizadas
     <conn-preparedstatement>`.

   Por tanto, no tome estos ejemplos muy en serio. Ya aprenderá a escribirlos
   mejor.

.. rubric:: Consultas

Las consultas, en cambio, sí devuelven resultados, que deberán procesarse luego:

.. code-block:: java

   ResultSet rs = stmt.executeQuery("""
        SELECT * FROM "Departamento";
   """);

   while(rs.next()) {
        int id = rs.getInt("idDepartamento");
        String denominacion = rs.getString("denominacion");
        System.out.println(String.format("ID: %d  -- nombre: %s", id, denominacion));
   }

.. borrar esto)

Como se ilustra arriba, el resultado de las consultas se obtiene a través de un
objeto :java-sql:`ResultSet <ResultSet>` que se va consumiendo a medida que
obtenemos registros de él. En el ejemplo, nos hemos limitado a imprimirlos, pero
si nuestro programa pretendiera hacer algo útil, tendríamos que trasladar esta
información al modelo de objetos de Java, Por ejemplo, suponiendo que hubiéramos
definido una clase ``Departamento`` así:

.. literalinclude:: files/Departamento.java
   :language: java

podríamos hacer esto:

.. code-block:: java

   ResultSet rs = stmt.executeQuery("""
        SELECT * FROM "Departamento";
   """);

   List<Departamento> departamentos = new ArrayList<>(); 

   while(rs.next()) {
        departamentos.add(new Departamento().cargarDatos(
            rs.getInt("idDepartamento"),
            rs.getString("denominacion")
        ));
        System.out.println(String.format("ID: %d  -- nombre: %s", id, denominacion));
   }

.. borrar esto)

.. important:: Obsérvese que tiene que ser el programador el que traduzca el modelo
   relacional al modelo de objetos, tal como adelantamos en la introducción.

.. _conn-preparedstatement:

Parametrizadas
--------------
Las sentencias, tanto las de manipulación como las de consultas, requieren en
muchos casos que se incluyan valores de variables en las propias sentencias
(véase, sin ir más lejos, el :ref:`ejemplo de inserción bajo el epígrafe
anterior <ej-insert-sin-par>`.). Por este motivo los conectores incluyen un
mecanismo para parametrizar sentencias; y, en el caso de |JDBC|, se hace uso de
:java-sql:`PreparedStatement <PreparedStatement>`:

.. code-block:: java
   :emphasize-lines: 2-4, 7, 8

   try(
      PreparedStatement pstmt = conn.prepareStatement("""
         INSERT INTO "Departamento" ("denominacion") VALUES (?);
      """)
   ) {
       for(String denominacion: new String[] {"Informática", "Inglés", "Tecnología"}) {
           pstmt.setString(1, denominacion);  // Valor para el primer "?".
           pstmt.executeUpdate();
       }
   }

Por tanto, cuando se ejecutan sentencias parametrizadas, hay que definir el
valor para todos los parámetros (el primero, el segundo, etc.) y cuando se han
establecidos todos sus valores, ejecutar la sentencia.

.. caution:: Pero no hemos dado aún con la forma eficiente, ya que estamos
   repitiendo la ejecución de la misma sentencia con distintos valores y lo que
   hemos hecho no es en absoluto eficiente. Necesitaremos :ref:`más adelante
   <conn-batch>`, darle al menos una vuelta más.

.. _conn-transactions:

Transacciones
=============
Hasta ahora hemos obviado el concepto de transacción. Una :dfn:`transacción` es
una operación sobre la base datos, no necesariamente atómica, que debe
completarse o no hacerse en absoluto, es decir, si una *transacción* se
compone de dos operaciones (sentencias |SQL|), ambas operaciones deben
realizarse.

Por ejemplo, en una tienda la venta de un bolígrafo implica dos cosas:

+ Ingresar el importe del bolígrafo.
+ Eliminar el bolígrafo del almacén.

Ambas operaciones son indisolubles y hemos de hacerlas para que se complete la
venta, o no hacerlas en absoluto para que quede la venta pendiente. En cambio,
si se hiciera una y no la otra, la base de datos quedaría en un estado
inconsistente.

Manejo de transacciones
-----------------------
En los ejemplos con que hemos ilustrado los distintos casos, cada sentencia
|SQL| constituye una transacción diferente. Si queremos que varias sentencias
pertenezcan a una misma transacción debemos hacer lo siguiente:

.. code-block:: java
   :emphasize-lines: 1, 12, 16, 19

   conn.setAutoCommit(false);  // Evitamos que cada sentencia implique una transacción

   try(
      PreparedStatement pstmt = conn.prepareStatement("""
         INSERT INTO "Departamento" ("denominacion") VALUES (?);
      """)
   ) {
       for(String denominacion: new String[] {"Informática", "Inglés", "Tecnología"}) {
           pstmt.setString(1, denominacion);
           pstmt.executeUpdate();
       }
       conn.commit(); // Después de ejecutar todas las sentencias, las confirmamos.
   }
   catch(SQLException err) {
      err.printStackTrace();
      conn.rollback();  // Hubo un problema, no ejecutamos ninguna inserción.
   }
   finally {
      conn.setAutoCommit(true);
   }

.. important:: En el ejemplo, las sentencias son una misma sentencia con distinto parámetros.
   Evidentemente, las transacciones pueden estar constituidas por cualesquiera
   sentencias.

.. _conn-batch:

Operaciones masivas
-------------------
En el caso de que tengamos que llevar a cabos muchas operaciones que comparten
la misma sentencia y distintos parámetros (como en el ejemplo anterior
precisamente), el modo más eficiente para llevarlas a cabo es el siguiente:

.. code-block:: java
   :emphasize-lines: 10, 13

   conn.setAutoCommit(false);  // Todas las inserciones forman parte de una transacción

   try(
      PreparedStatement pstmt = conn.prepareStatement("""
         INSERT INTO "Departamento" ("denominacion") VALUES (?);
      """)
   ) {
       for(String denominacion: new String[] {"Informática", "Inglés", "Tecnología"}) {
           pstmt.setString(1, denominacion);
           pstmt.addBatch();  // Añadimos la inserción al procedimiento.
       }

       pstmt.executeBatch(); // Ejecutamos todas las inserciones pendientes.
       conn.commit(); // Grabamos la transacción.
   }
   catch(SQLException err) {
      err.printStackTrace();
      conn.rollback();  // Hubo un problema, no ejecutamos ninguna inserción.
   }
   finally {
      conn.setAutoCommit(true);
   }

.. _conn-data:

Datos
=====
Hasta ahora no hemos profundizados en los tipos de datos concretos ya que los
que muestran nuestro ejemplos son bastante simples. Sin embargo, la
correspondencia entre tipos de datos de Java y de |SQL| no es exacta, por lo que
|JDBC| implementa los métodos `setXXXX` de :java-sql:`PreparedStatement
<PreparedStatement>`.

Datos simples
-------------

.. _conn-date:

Fechas y tiempos
----------------
El estándar |SQL| define cinco tipos de datos para la expresión de tiempos:

1. ``DATE`` que sirve para definir fechas (p.e. \'2014-01-08\').
#. ``TIME`` que sirve para definir horas con precisión de segundos (p.e.
   \'08:30:21\'), aunque también podría incluirse precisión de hasta el
   microsegundo, añadiendo decimales al segundo.
#. ``TIMESTAMP`` que combina en un mismo tipo fecha y hora (p.e. \'2014-01-08
   08:30:21\').
#. ``TIMESTAMP WITH TIME ZONE`` que permite almacenar, además, el huso horario
   (p.e. \'2014-01-08 08:30:21+01:00\').
#. ``INTERVAL`` para almacenar periodos de tiempo (p.e. \'INTERVAL 2 DAYS\' o
   \'INTERVAL 2 DAYS 10 HOURS\').

De los |SGBD| más utilizados el que implementa más fielmente el estándar es
PostgreSQL_. SQLite_, que es el que hemos propuesto utilizando, es el que tiene un
soporte más precario. De hecho, no implementa ninguno. Sin embargo:

* En las definiciones de las tablas (``CREATE TABLE``), podremos usar todas las
  las palabras reservadas vistas. Eso se debe a que **SQLite** no impone ninguna
  restricción a los tipos, ya que usa tipado dinámico, y admite que se escriba
  como tipo cualquier palabra, incluso una que inventemos. Él simplemente
  almacenará el dato como ``TEXT``, ``INTEGER``, ``DOUBLE`` o ``BLOB``
  dependiendo del valor que se le proporcione.
* Para operar con los datos tendremos que usar funciones específicas.

.. _conn-extra:

Extras
======

.. + setRegister
   + Java funcional (Stream y for-each a partir de ResultSet).

.. |SQL| replace:: :abbr:`SQL (Structured Query Language)`
.. |CSV| replace:: :abbr:`CSV (Comma-Separated Values)`
.. |ORM| replace:: :abbr:`ORM (Object-Relational Mapping)`
.. |API| replace:: :abbr:`API (Application Programming Interface)`
.. |JDK| replace:: :abbr:`JDK (Java Development Kit)`
.. |JDBC| replace:: :abbr:`JDBC (Java DataBase Connectivity)`
.. |SGBD| replace:: :abbr:`SGBD (Sistema Gestor de Bases de Datos)`
.. |URL| replace:: :abbr:`URL (Uniform Resource Locator)`
.. |TCP| replace:: :abbr:`TCP (Transmission Control Protocol)`
.. |IP| replace:: :abbr:`IP (Internet Protocol)`
.. |E/R| replace:: :abbr:`E/R (Entidad/Relación)`

.. _commons-csv: https://commons.apache.org/proper/commons-csv/project-info.html
.. _librería Jackson: https://github.com/FasterXML/jackson
.. _SQLite: https://sqlite.org/
.. _MariaDB: https://mariadb.org/
.. _Oracle: https://www.oracle.com/es/database/
.. _sqlite-jdbc: https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
.. _mariadb-java-client: https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
.. _mysql-connector-j: https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
.. _postgresql: https://mvnrepository.com/artifact/org.postgresql/postgresql
.. _ojdbc11: https://mvnrepository.com/artifact/com.oracle.database.jdbc/ojdbc11
.. _mssql-jdbc: https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc

.. rubric:: Notas al pie

.. [#] Se han supuesto (excepto para **SQLite** obviamente) conexiones
   |TCP|/|IP|. Sin embargo, en sistemas *UNIX* el motor podría permitir
   conexiones a un *socket* local. Por ejemplo, una |URL| a socket local para
   MariaDB podría ser
   ``jdbc:mariadb:///mibase?socket=/var/run/mysqld/mysqld.sock`` o
   algo parecido.

.. [#] Aunque haremos una excepción aclarando cómo están definidas las claves
   primarias en las tablas ``Claustro``, ``Departamento`` y ``Profesor``.
   Nuestra intención es que sus identificadores se generen automáticamente sin
   necesidad de que especifiquemos un valor. Eso se hace en SQLite_ con
   *AUTOINCREMENT*, pero en otros motores se expresa de otro modo, ya que no
   forma parte del estándar. Desde |SQL|\ :2003, existe un modo de expresarlo:
   :code:`GENERATED [BY DEFAULT|ALWAYS] AS IDENTITY`. Sin embargo, SQLite_ no lo
   soporta y por eso lo hemos dejado comentado. En cambio, tiene un comportamento
   curioso cuando se usa ``INTEGER`` para definir la clave primaria asume el
   comportamento de :code:`GENERATED BY DEFAULT AS IDENTITY` y cuando se usa
   ``INT`` la clave no es autoincremental.
