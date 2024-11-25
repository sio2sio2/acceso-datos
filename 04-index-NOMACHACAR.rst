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

Ambas estrategias tienen sus **ventajas** e **inconvenientes**:

**Ventajas** de los conectores
   a. Mayor control sobre las operaciones, al definirse manualmente, lo que
      permite ajustar más la solución al problema concreto o utilizar
      características avanzadas de |SQL|.
   #. Al haber menos abstracción, suele ser una estrategia de mayor rendimiento.
   #. Es una solución más fácilmente depurable.
   #. Las sentencias |SQL| son independientes del lenguaje de programación en que
      se escriba la aplicación, pese a lo cual habrá que traducir el código al
      nuevo lenguaje.
   #. Son más sencillos de usar que un |ORM|.
   #. Los conectores suelen formar parte de las librerías básicas del lenguaje
      por lo que no necesitaremos usar una librería de terceros (la librería
      |ORM|) ni tendremos que reescribir el código si decidimos cambiar a un
      |ORM| distinto.

**Ventajas** de las herramientas |ORM|
   a. Al proveer un mecanismo para traducir el modelo relacional al modelo de
      datos del lenguaje de programación (modelo de objetos), son herramientas
      más productivas.
   #. El programador trata los datos directamente como objetos lo que hace el
      código más sencillo y manipulable.
   #. Generalmente, cambiar de |SGBD| es trivial, puesto que la herramienta
      nos abstrae de sus particularidades. Su uso, por tanto, nos independencia
      de cuál sea el |SGBD| que gestione los datos frente a los conectores que
      usan sentencias |SQL|, generalmente dependientes de cuál es el |SGBD|. En
      contrapartida, puede resultar muy trabajoso cambiar de |ORM|

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
necesario que tengamos **instalado el motor**. En el caso de :program:`SQLite`:

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

Además, necesitaremos importar en nuestro proyecto la librería propia de :program:`SQLite`: sqlite-jdbc_.

.. rubric:: Ejercicio ilustrativo

Tomemos como base el :ref:`ejercicio ilustrativo con que introducimos XML
<xml>`, aunque con algunos cambios (generalizaciones):

+ Puede haber varios claustros.
+ Un mismo profesor puede trabajar en varios claustro diferentes.
+ Si se da la anterior circunstancia, el profesor se adcribirá a un
  departamento\ [#]_ por centro, pero no tiene que ser para todos los centros el
  mismo.
+ De igual modo, en un centro puede tener asignado uno o varios casilleros
  distintos a los que tiene en otro.

Esto podemos representarlo gráficamente con el siguente diagrama |E/R|:

.. image:: files/ER-problema.png

Podemos traducir el anterior esquema al modelo relacional así:

.. code-block:: none

   Centro(*idCentro*, nombre)
   Profesor(*idProfesor*, casillero, sustituye, apelativo, apellidos, nombre)
   Departamento(*idDepartamento*, denominacion)
   Trabaja(*idProfesor, idCcentro*, departamento)

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
  simplemente para ilustrar cómo obligar a :program:`SQLite` a respetar la integridad
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

.. note:: Hay otro método, ``execute`` que sirve para ambos casos y que devuelve
   ``true``, si devuelve resultados (segundo caso) o ``false``, si no lo hace.
   De hecho, lo podríamos haber usado al crear las tablas, aunque hemos
   preferido ``executeUpdate``.

Simples
-------
Comencemos creado cuatro tablas:

.. literalinclude:: files/01.create.java
   :language: java

El código tiene dos aspectos:

#. Las sentencias |SQL| que son sentencias |SQL| cuya comprensión no
   forma parte de los objetivos de esta unidad\ [#]_, así que no entraremos en
   comentarlas.

   .. hint:: No obstante si conviene precisar que procuremos al escribirlas
      ceñirnos al estándar lo más posible a fin de que sean lo más universales
      posibles y nos sea menos costoso cambiar de |SGBD|. A este respecto:

      + Dejamos de escribir los nombres de elementos de la base de datos
        (tablas, columnas) entre comillas dobles, porque el estándar |SQL| lo
        permite en caso de que los nombres no contengan caracteres 'raros'
        (espacios, etc.) o no se tenga la intención de distinguir nombres
        cambiando entre mayúsculas y minúsculas (p.e. que una tabla se llame
        *Persona* y otra distinta *persona* o *PERSONA*)\ [#]_.

      + Escribimos para los tipos los nombres que define el estándar.

      + Las cadenas se encierran entre comillas simples.

#. La ejecución de esas sentencias usando |JDBC|. Obsérvese que:

  + Reaprovechamos el mismo objeto :java-sql:`Statement <Statement>` para
    ejecutarlas todas.
  + Las ejecutamos usando el método ``executeUpdate``, puesto que su función es
    modificar el contenido de la base de datos, no obtener información.

Tampoco las sentencias de inserción devuelven resultados, por lo que también
podemos hacer:

.. _ej-insert-sin-par:

.. code-block:: java

      // Agregamos algunos departamentos.
      stmt.executeUpdate("INSERT INTO Departamento (denominacion) VALUES ('Informática')");
      for(String denominacion: new String[] {"Inglés", "Francés", "Tecnología"}) {
          stmt.executeUpdate(String.format("INSERT INTO Departamento (denominacion) VALUES ('%s');", denominacion));
      }

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

.. seealso:: Se proporcionan adicionalmente dos guiones para su uso directo con
   :program:`SQLite`: :download:`sustituye.sql <files/sustituye.sql>`, que
   implementa la solución desarrollada aquí y :download:`sustituto.sql
   <files/sustituto.sql>` en que el campo *sustituye* se cambia por *sustituto*
   para que su significado sea quién es el sustituto del profesor en vez de a
   quién sustituye el profesor. En ambas soluciones, al código Java mostrado se
   añaden algunos ``INSERT`` para agregar registros a la base de datos y se
   define una |CTE| recursiva (totalmente compatible con el estándar |SQL|) para
   que la propia base de datos nos cree una vista (*PlantillaFuncionamiento*)
   que muestra cada profesor, a qué titular en último caso sustituye (o él mismo
   si es titular) y si está activo o no. Esta vista es de enorme utilidad para
   saber en dónde trabaja cada profesor, ya que la tabla *Trabaja* sólo contiene
   profesores titulares.

.. rubric:: Consultas

Las consultas, en cambio, sí devuelven resultados, que deberán procesarse luego:

.. code-block:: java

   ResultSet rs = stmt.executeQuery("SELECT * FROM Departamento;");

   while(rs.next()) {
        int id = rs.getInt("id_departamento");
        String denominacion = rs.getString("denominacion");
        System.out.println(String.format("ID: %d  -- nombre: %s", id, denominacion));
   }

.. borrar esto)

Como se ilustra arriba, el resultado de las consultas se obtiene a través de un
objeto |ResultSet| que se va consumiendo a medida que obtenemos registros de él.
En el ejemplo, nos hemos limitado a imprimirlos, pero si nuestro programa
pretendiera hacer algo útil, tendríamos que trasladar esta información al modelo
de objetos de Java, Por ejemplo, suponiendo que hubiéramos definido una clase
``Departamento`` así:

.. literalinclude:: files/Departamento.java
   :language: java

podríamos hacer esto:

.. code-block:: java

   ResultSet rs = stmt.executeQuery("SELECT * FROM Departamento");

   List<Departamento> departamentos = new ArrayList<>(); 

   while(rs.next()) {
        int id = rs.getInt("idDepartamento");
        String denominacion = rs.getString("denominacion");
        departamentos.add(new Departamento().cargarDatos(id, denominacion));
        System.out.println(String.format("ID: %d  -- nombre: %s", id, denominacion));
   }

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
|PreparedStatement|:

.. code-block:: java
   :emphasize-lines: 2-4, 7, 8

   try(
      PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Departamento (denominacion) VALUES (?)")
   ) {
       for(String denominacion: new String[] {"Informática", "Inglés", "Tecnología"}) {
           pstmt.setString(1, denominacion);  // 1: Valor para el primer "?".
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

Obsérvese que al definir los valores de los parámetros de una consulta,
necesitamos cambiar de método según sea el tipo del campo:

.. code-block:: java

   try(
      PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Profesor (apelativo, nombre, apellidos, sustituye) VALUES (?, ?, ?, ?)");
   ) {
         pstmt.setString(1, "Manolo");
         pstmt.setString(2, "Manuel");
         pstmt.setString(3, "Peñalba Pinzón");
         pstmt.setNull(4, Types.INTEGER);
         pstmt.executeUpdate();
   }

|JDBC| permite usar un método genérico (``setObject``) que se encargará de
inferir el tipo |SQL| correcto a partir del tipo de *Java*:

.. code-block:: java

   try(
      PreparedStatement pstmt = conn.prepareStatement("""
         INSERT INTO "Profesor" ("apelativo", "nombre", "apellidos", "sustituye") VALUES
            (?, ?, ?, ?);
      """)
   ) {
         pstmt.setObject(1, "Manolo");
         pstmt.setObject(2, "Manuel");
         pstmt.setObject(3, "Peñalba Pinzón");
         pstmt.setObject(4, null, Types.INTEGER);  // No se puede inferir entero a partir de null
         pstmt.executeUpdate();
   }

El problema de ``setObject`` es que delegar la elección del tipo en el conector penaliza el
rendimiento.

.. _conn-data:

Datos
=====
Hasta ahora no nos hemos detenido en las particularidades de los tipos de datos,
ya que los que muestran nuestro ejemplo son bastante simples. Sin embargo, la
correspondencia entre tipos de datos de Java y de |SQL| no es exacta, por lo que
|JDBC| implementa los métodos `setXXXX` de |PreparedStatement| para guardar
datos en la base de datos y los métodos `getXXXX` de |ResultSet| para
recuperarlos.

En principio (aunque hay excepciones), podemos usar estos métodos y abstraernos
de cómo implementa el tipo el |SGBD| el estándar |SQL|.

.. _conn-data-simple:

Datos simples
-------------
Por datos simples entendemos los que representan caracteres, enteros, números
reales(flotantes o de precisión fija) o booleanos y tienen, por lo general, un
equivalente propio de Java.

.. table::
   :class: tipos-sql

   +-----------------+-------------------------+--------------------------------------+
   | Tipo |SQL|      | Tipo Java\ [#]_         | Método |JDBC| de |PreparedStatement| |
   +=================+=========================+======================================+
   | | CHAR(n)       | :java-lang:`String`     | setString(int idx, String v)         |
   | | VARCHAR(n)    |                         |                                      |
   +-----------------+-------------------------+--------------------------------------+
   | | SMALLINT      | | :java-lang:`Short`    | | setShort(int idx, short v)         |
   | | INTEGER       | | :java-lang:`Integer`  | | setInt(int idx, int v)             |
   | | BIGINT        | | :java-lang:`Long`     | | setLong(int idx, long v)           |
   +-----------------+-------------------------+--------------------------------------+
   | BOOLEAN         | :java-lang:`Boolean`    | | setBoolean(int idx, boolean v)     |
   +-----------------+-------------------------+--------------------------------------+
   | NUMERIC/DECIMAL | :java-lang:`BigDecimal` | | setBigDecimal(int idx, BigDecimal  |
   +-----------------+-------------------------+--------------------------------------+
   | | FLOAT         | | :java-lang:`Float`    | | setFloat(int idx, float v)         |
   | | DOUBLE        | | :java-lang:`Double`   | | setDouble(int idx, double v)       |
   +-----------------+-------------------------+--------------------------------------+

.. note:: No hay más que sustituir `set` por `get` para obtener los métodos de
   |PreparedStatement| necesarios para guardar datos.

Ejemplos de cómo obtener o escribir datos de este tipo ya se han dejado
escritos en los ejemplos de apartados anteriores. Sólo :java-math:`BigDecimal`
no es un tipo primitivo en Java, pero su uso es trivial:

.. code-block:: java

   BigDecimal comaFija = new BigDecimal("13.456");

.. rubric:: SQLite

Dado que hemos escogido este |SGBD| para desarrollar la unidad, nos conviene
centrarnos en sus particularidades. La característica fundamental de :program:`SQLite`
es que tiene **tipado dinámico** y cuál sea el tipo que declaremos al crear la tabla
es irrelevante, porque el sistema gestor aceptará el dato con independencia de
su tipo. Por ejemplo:

.. code-block:: sql

   CREATE TABLE Persona (
      nombre    VARCHAR(255);
   );

   INSERT INTO Persona VALUES
      ("Manolo"),   // Consecuente con la definición: no da problemas.
      (4356);       // Inconsecuente, pero da igual: el dato se almacena como entero.

De hecho, :program:`SQLite` ni siquiera atiende a qué palabra usamos para
definir el tipo y creará la tabla, incluso aunque nos inventemos el nombre del
tipo:

.. code-block:: sql

   CREATE TABLE Persona (
      nombre    TIPOINVENTADO   // No da error.
   );

Internamente, :program:`SQLite` sólo dispone de datos de tipo texto, entero (de
diverso tamaño), doble y BLOB; y dependiendo del valor que se suministre usará
un tipo u otro para el dato. Así, ``CHAR`` y ``VARCHAR`` se asimilan a texto,
``INTEGER``, ``BIGINT``, ``SMALLINT`` y ``BOOLEAN`` a enteros, ``FLOAT``,
``DOBLE`` y ``NUMERIC``/``DECIMAL`` a dobles (por tanto, se perderá la precisión
de este último tipo).

.. _conn-data-complex:

Datos complejos
---------------
Los datos complejos se caracterizan porque el paquete ``java.sql`` tiene
definidos tipos específicos que se corresponden con los definidos en el estándar
|SQL|.

.. table::
   :class: tipos-sql

   +-----------------+--------------------------+-----------------------------------------------+
   | Tipo |SQL|      | Tipo Java                | Método |JDBC| de |PreparedStatement|          |
   +=================+==========================+===============================================+
   | | DATE          | | :java-sql:`Date`       | | setDate(int idx, java.sql.Date v)           |
   | | TIME          | | :java-sql:`Time`       | | setTime(int idx, java.sql.Time v)           |
   | | TIMESTAMP     | | :java-sql:`Timestamp`  | | setTimestamp(int idx, java.sql.Timestamp v) |
   +-----------------+--------------------------+-----------------------------------------------+
   | BLOB            | | :java-sql:`Blob`       | | setBlob(int idx, java.sql.Blob v)           |
   |                 | | |InputStream|          | | setBinaryStream(int idx, InputStream v)     |
   +-----------------+--------------------------+-----------------------------------------------+
   | CLOB            | | :java-sql:`Clob`       | | setClob(int idx, java.sql.Clob v)           |
   |                 | | |Reader|               | | setCharacterStream(int idx, Reader v)       |
   +-----------------+--------------------------+-----------------------------------------------+
   | JSON            | :java-lang:`String`      | setString(int idx, String v)                  |
   +-----------------+--------------------------+-----------------------------------------------+
   | | ARRAY         | | :java-sql:`Array`      | | setArray(int idx, java.sql.Array v)         |
   | | STRUCT        | | :java-sql:`Struct`     | | setStruct(int idx, java.sql.Struct v)       |
   +-----------------+--------------------------+-----------------------------------------------+

.. _conn-date:

Fechas y tiempos
''''''''''''''''
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

A través de |JDBC| sólo se soportan directamente los tres primeros tipos y,
además, se requiere saber cómo convertir entre :java-sql:`Date`,
:java-sql:`Time`, :java-sql:`Timestamp` y los tipos con los que frecuentemente
se trabaja en *Java*.

.. code-block:: java

   import java.time.LocalDate;
   import java.time.LocalTime;
   import java.time.LocalDateTime;
   import java.sql.Date;
   import java.sql.Time;
   import java.sql.Timestamp;

   java.util.Date udate = new java.util.Date(); // Almacena fecha y hora.

   // Date --> Date
   Date date = new Date(udate.getTime());

   // Date --> Date
   udate = new Date(date.getTime());

   // Date --> Time
   Time time = new Time(date.getTime());

   // Time --> Date
   date = new Date(time.getTime());

   // Date --> Timestamp
   Timestamp timestamp = new Timestamp(date.getTime());

   // Timestamp --> Date
   date = new Date(timestamp.getTime());

   LocalDate localDate = LocalDate.now();

   // LocalDate --> Date
   date = Date.valueOf(localDate);

   // Date --> LocalDate
   localDate = date.toLocalDate();

   LocalTime localTime = LocalTime.now();

   // LocalTime --> Time
   sqltime = Time.valueOf(localTime);

   // Time --> Localtime
   localTime = Time.toLocalTime();

   LocalDateTime localDateTime = LocalDateTime.now();

   // LocalDateTime --> Timestamp
   timestamp = Timestamp.valueOf(localDateTime);

   // Timestamp --> LocalDateTime
   localDateTime = timestamp.toLocalDateTime();

BLOB y CLOB
'''''''''''
Ambos tipos representan datos de tamaño considerable, ``BLOB`` un datos binarios
(p.e. una imagen) y ``CLOB`` un conjunto de caracteres, o sea, un texto grande
que el que se podrían almacenar con ``VARCHAR`` (cuyo límite depende del
|SGBD|). Al margen de esa diferencia, explicado uno, explicado el otro.

Por ejemplo, si tuviéramos un archivo con una foto que quisiéramos guardar en
una base de datos podríamos hacer:

.. code-block:: java
   :emphasize-lines: 10, 12

   try (
      Connection conn = DriverManager.getConnection(dbUrl);
   ) {
      try(
         PreparedStatement pstmt = conn.preparedStatement("INSERT INTO Persona (nombre, avatar) VALUES (?, ?)")
      ) {
         Path archivo = Path.of("ruta", "al", "archivo", "jpg");
         try(InputStream st = Files.newInputStream(archivo)) {
            pstmt.setString(1, "Manolito");
            pstmt.setBinaryStream(2, st);
            pstmt.executeUpdate();
         }
      }
   }

También podríamos querer guardar un archivo binaro ya cargado en memoria:

.. code-block:: java

   byte[] archivo = new byte[] {10, 20, 5, 50, 12, 221, 13}
   Blob blob = conn.createBlob();
   blob.setBytes(1, archivo); // Agregamos la secuencia de bytes al principio del Blob.

   try (
      Connection conn = DriverManager.getConnection(dbUrl);
   ) {
      try(
         PreparedStatement pstmt = conn.preparedStatement("INSERT INTO Persona (nombre, avatar) VALUES (?, ?)")
      ) {
         pstmt.setString(1, "Manolito");
         pstmt.setBlob(2, blob);
         pstmt.executeUpdate();
      }
      finally {
         blob.free();  // Vaciamos el blob para liberar la memoria.
      }
   }

JSON
''''
Desde |SQL|\ :2023 el estándar soporta de forma nativa el tipo |JSON|. Sin
embargo, |JDBC| aún no tiene soporte alguno para ello, así que el único modo de
tratarlo es a través de :java-lang:`String`.

ARRAY y STRUCT
''''''''''''''
El tipo de dato ``ARRAY`` es, simplemente, una secuencia de datos de un mismo
tipo, o sea, lo que entenderíamos como *array* en cualquier lenguaje de
programación:

.. code-block:: sql
   :emphasize-lines: 6

   CREATE TABLE Trabaja (
      profesor        INTEGER,
      claustro        INTEGER,
      departamento    INTEGER,
      -- Para poder asignar varios casilleros a un mismo profesor
      casillero       INTEGER ARRAY   NOT NULL,

      /* Restricciones */
   );

``STRUCT``, en cambio, es un tipo de dato que permite incluir
como valor de un campo una estructura de datos al modo de las estructuras *C* o
los mapas de *Python* o *Java*:

.. code-block:: sql

   // No pueden definirse restricciones en la definición, así que estas
   // (p.e. tipo_via debería incluir un CHECK con varios valores)
   // debe definirse en la tabla en la que se incluya este tipo struct.
   CREATE TYPE domicilio AS (
      tipo_via       VARCHAR(40),
      nombre_via     VARCHAR(150),
      numero         INTEGER,
      bloque         CHAR(1),
      escalera       CHAR(1),
      piso           INTEGER,
      letra          CHAR(2)
   );

.. rubric:: SQLite

:program:`SQLite` no soporta de forma nativa los datos complejos (ya explicamos
al tratar los datos simples cómo funcionan en realidad los tipos en él). En
particular:

``DATE``\ /\ ``TIME``\ /\ ``TIMESTAMP``
   Puede almacenarlos como una cadena ('2024-12-12'), un entero(el tiempo *UNIX*
   1733961600) o un flotante (`fecha juliana
   <https://es.wikipedia.org/wiki/Fecha_juliana>`_ usada en Astronomía). Para
   darles soporte añade funciones específicas.

   En el caso particular de |JDBC|, ``setDate`` almacenará la fecha como un
   entero, lo cual nos es indiferente si leemos los campos con ``getDate``, pero
   quizás no nos guste tanto, si la lectura la hacemos por otros medios (p.e.
   usando directamente el cliente de :program:`SQLite` sin echar mano de funciones
   específicas).
   
``BLOB``
   Es el único dato complejo que realmente soporta :program:`SQLite`, así que no
   tendremos problemas con él.

``CLOB``
   :program:`SQLite` no le da un tratamiento especial y se trata como cualquier
   otra cadena, ya que internamente :program:`SQLite` sólo tiene un tipo para
   datos que son cadenas. Sin embargo, ``setClob`` **no está implementado** para
   él, por lo que tendremos que usar ``setString``.

``JSON``
   No tiene soporte nativo sino a través de funciones específicas. En cualquier
   caso, |JDBC| tampoco lo tiene con lo que tendrá que usarse ``setString``
   igual que para el resto de |SGBD|.

``ARRAY``\ /\ ``STRUCT``
   No tienen soporte en :program:`SQLite` y, además, los métodos ``setArray`` y
   ``setStruct`` no están implementados para el *driver*.

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
   :emphasize-lines: 1, 10, 14, 17

   conn.setAutoCommit(false);  // Evitamos que cada sentencia implique una transacción

   try(
      PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Departamento (denominacion) VALUES (?)")
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

.. borrar esto)

.. important:: En el ejemplo, las sentencias son una misma sentencia con
   distinto parámetros.  Evidentemente, las transacciones pueden estar
   constituidas por cualesquiera sentencias.

.. _conn-batch:

Operaciones masivas
-------------------
En el caso de que tengamos que llevar a cabos muchas operaciones que comparten
la misma sentencia y distintos parámetros (como en el ejemplo anterior
precisamente), el modo más eficiente para llevarlas a cabo es el siguiente:

.. code-block:: java
   :emphasize-lines: 8, 11

   conn.setAutoCommit(false);  // Todas las inserciones forman parte de una transacción

   try(
      PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Departamento (denominacion) VALUES (?)")
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

.. _conn-extra:

Extras
======
|ResultSet| permite ir obteniendo fila a fila los resultados de una consulta.
Sin embargo, no proporciona una interfaz funcional que nos permita utilizar las
:ref:`operaciones funcionales habituales <java-stream-operaciones>`. Para
paliarlo podemos definir una clase que haga la conversión (véase el :download:`codigo fuente <files/JdbcUtils.java>`):

.. literalinclude:: files/JdbcUtils.java
   :language: java
   :start-at: public class

Si incluimos este archivo en nuestro proyecto podremos hacer consultas de este
modo:

.. code-block:: java
   :emphasize-lines: 3

   ResultSet rs = stmt.executeQuery("SELECT * FROM Departamento");

   Stream<Departamento> departamentos = JdbcUtils.resultSetToStream(rs, fila -> {
      try {
         int id = fila.getInt("id_departamento");
         String denominacion = fila.getString("denominacion");
         return new Departamento().cargarDatos(id, denominacion);
      }
      catch(SQLException err) {
         err.printStackTrace();
         return null;
      }
   });

   //Tratamos el flujo como mejor estimemos oportuno.
   for(Departamento d: (Iterable<Departamento>) departamentos::iterator) {
      System.out.println(String.format("ID: %d -- Denominación: %s", d.getId(), d.getDenominacion()));
   }

.. tip:: El método ``resultSetToStreamp`` permite no definir la función que
   transforma la fila (el propio ``ResultSet``) en un objeto. En ese caso, se
   obtendrá con cada elemento del flujo la propia fila:

   .. code-block:: java

      Stream<ResultSet> result = JdbcUtils.resultSetToStream(rs);

.. _conn-transaction-manager:

Gestor de transacciones
-----------------------
.. todo:: Añadir en transacciones una referencia a este apartado.

Para facilitar la forma en que se gestionan las :ref:`transacciones ya
estudiadas <conn-transactions>` podemos crear una clase que explote las
posibilidades del bloque `try-with-resources
<https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html>`_:

.. literalinclude:: files/TransactionManager.java
   :language: java

La clase podría usarse del siguiente modo::

.. code-block:: java

   // Se supone que conn es una conexión ya abierta.
   try (TransaccionManager tm = new TransactionManager(conn)) {
      // Ejecutamos las sentencias de esta transacción.
      tm.commit(); // Confirmamos en la base de datos.
   }
   catch(SQLException err) {
      // No hay que hacer rollback, ya que se encarga el close().
      err.printStackTrace();
   }

.. tip:: Podemos incluir esta clase como una clase estática dentro de
   ``JdbcUtils`` para tener todos estos añadidos juntos.

Cargar esquema desde archivo
----------------------------
Es muy común que la primera vez que se ejecuta la aplicación, ésta cree la base
de datos y defina el esquema y los datos iniciales necesarios. Lo cómodo es que
las sentencias necesarias se encuentren en un guión |SQL| y el programa las lea
de él, en vez de encontrarse incrustadas en el código.

Sin embargo, |JBDC| no tiene definido un método que nos permita ejecutar un
guión |SQL| completo, así que la única forma de poner ejecutar sus sentencias,
es descomponerlas primero. Para ello podemos optar por dos estrategias:

+ `JSQLParser
  <https://mvnrepository.com/artifact/com.github.jsqlparser/jsqlparser>` que es
  capaz de procesar el código |SQL| y, por tanto, reconocer los elementos de que
  se compone.

+ Si el guión no es complejo y seguimos algunas premisas:

  a. Los ';' que completan sentencias deben encontrarse a final de línea.
  #. No pueden usarse las palabras ``begin`` o ``end`` en comentarios, nombres,
     valores, etc.
  #. No puede usarse ``IF`` en aquellos |SGBD| que lo implementen (pero sí el
     estándar ``CASE``).

  podemos escribir una solución artesanal, que es la que proponemos.

.. literalinclude:: files/splitSQL.java
   :language: java

.. hint:: Podemos incluir el método dentro de ``JdbcUtils``.

¿Cómo podemos usar este método? Por ejemplo, así:

.. code-block:: java

   final String tablaExistente = "Profesor";
   final Path guion = Path.of(System.getProperty("user.dir"), "loquesea.sql");

   // Ya hay una conexión "conn" abierta.
   try(Statement stmt = conn.createStatement()) {
      stmt.executeUpdate("SELECT 1 FROM " + tablaExistente);
   }
   catch(SQLException err) { // Si la tabla no existe, no hay esquema aún.
      try (
         InputStream st = Files.newInputStream(guion);
         TransactionManager tm = new TransactionManager(conn)
      ) {
         List<String> sentencias = slitSQL(st);
         for(String sentencia: sentencias) {
            stmt.executeUpdate(sentencia);
         }
         tm.commit();
      }
      catch(SQLException err) {
         err.printStackTrace(); // O como se decida tratar el error.
      }
   }

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
.. |JSON| replace:: :abbr:`JSON (JavaScript Object Notation)`
.. |CTE| replace:: :abbr:`CTE (Common Table Expression)`
.. |InputStream| replace:: :java-io:`InputStream <InputStream>`
.. |Reader| replace:: :java-io:`Reader <Reader>`
.. |PreparedStatement| replace:: :java-sql:`PreparedStatement <PreparedStatement>`
.. |ResultSet| replace:: :java-sql:`ResultSet <ResultSet>`

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

.. [#] En puridad, los profesores desempeñan puestos que se adscriben a
   departamentos y a cada departamento pueden estar adscritos uno o varios
   puestos, pero obviaremos esto para simplificar y nos limitaremos a afirmar
   que trabajan para departamentos.

.. [#] Según el estandar un nombre que no se encierra entre comillas dobles, se
   sobreentiende escrito todo en mayúsculas. En particular, :program:`SQLite` se
   salta el estándar en este aspecto y no hace esa distinción, incluso aunque
   los nombres si se hayan entrecomillado. Otra razón que avala el que no
   entrecomillemos es que :program:`MariaDB`/:program:`MySQL` exige que
   cambiemos la configuración predeterminada para soportar el entrecomillado con
   comillas dobles, ya que este |SGBD| entrecomilla con el acento grave.

.. [#] Se han supuesto (excepto para :program:`SQLite` obviamente) conexiones
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
   curioso: cuando se usa ``INTEGER`` para definir la clave primaria asume el
   comportamento de :code:`GENERATED BY DEFAULT AS IDENTITY` y cuando se usa
   ``INT`` la clave no es autoincremental.

.. [#] Se han referido clases, pero también equivalen a tipos primitivos (``short`` en vez de :java-lang:`Short`.
