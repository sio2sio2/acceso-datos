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
  (p.e. :file:`C:\Program Files\SQLite`).
  #. Añadir el directorio al `PATH` del sistema.

.. note:: Procuramos escribir sentencias |SQL| que cumplan el estándar para que
   el código sea lo más portable posible.

Además, necesitaremos importar en nuestro proyecto la librería propia de SQLite: sqlite-jdbc_.

.. rubric:: Ejercicio ilustrativo

.. todo:: Exponer el ejercicio que deseamos resolver.

.. _conn-conexion:

Conexión
========
Lo primero que debemos aprender es cómo abrir una conexión a la base de datos:

.. code-block:: java

   final String protocol = "jdbc:sqlite:";

   // Las bases de datos de SQLite son archivos.
   Path dbPath = Path.of(System.getProperty("java.io.tmpdir"), "test.db");
   String dbUrl = String.format("%s%s", dbProtocol, dbPath);

   // Alternativa particular de SQLite: base de datos en memoria.
   //String dbUrl = String.format("%s%s", dbProtocol, ":memory:");

   try (
      Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
   ) {

      // Particular de SQLite: respetar la integredad referencial (opcional).
      stmt.execute("PRAGMA foreign_keys = ON");

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
* Un objeto para generar sentencias |SQL| del que hablaremos bajo el próximo epíepígrafe. También es necesario cerrarlo y, en este ejemplo, lo hemos creado simplemente para ilustrar cómo obligar a *SQLite* a respetar la integridad referencial (por defecto, no lo hace).

Por supuesto, el código es *completamente inútil*: nos hemos conectado a la base de datos para no hacer absolutamente nada. En los siguientes apartados veremos como leer y escribir datos.

.. _conn-statement:

Ejecución de sentencias
=======================
El acceso mediante conectores se basa en la ejecución de sentencias |SQL| que
pueden ser de dos tipos:

+ Sentencias que modifican la base de datos.
+ Sentencias que obtienen datos sin llevar a cabo modificación alguna.

Para las primeras se usa el método ``executeUpdate``, mientras que para las
segundas el método ``executeQuery``.

.. todo:: Hablar de ``execute``.

Simples
-------
Comencemos creado cuatro tablas:

.. literalinclude:: files/01.create.java
   :language: java


.. _conn-prestatement:

Parametrizadas
--------------

.. _conn-transactions:

Transacciones
=============

.. Concepto.

Manejo de transacciones
-----------------------

.. _conn-batch:

Operaciones masivas
-------------------

.. executeBatch.

.. _conn-date:

Fechas y horas
==============

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
