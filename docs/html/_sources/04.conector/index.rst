.. _conn:

Conectores
**********
Las bases de datos relacionales son unos de los soportes más utilizados para
el almacenamiento organizado de información. Su problema, al ser accedidas
mediante aplicaciones, es que basan su estructrura en el modelo relacional, que
no es el modelo que usan los lenguajes de programación para manejar datos. En
concreto, los más habituales, los lenguajes de |POO| manejan los datos haciendo
uso del modelo de objetos, por lo que existe una discrepación entre el modo en
que tratan los datos los |SGBD| relacionales y el modo en que los tratan los
lenguajes. Para afrontarlo, los lenguajes utilizan dos estrategias distintas:

#. El uso de conectores, en que el acceso a la base de datos se realiza mediante
   sentencias |SQL| y la traducción del modelo relacional al modelo de objetos
   corre a cargo del programador.

#. El uso de herramientas |ORM| en que la traducción entre ambos modelos corre a
   cargo de la propia herramienta siguiendo las orientaciones del programador.

Podemos entender mejor estas dos estrategias si las comparamos con estrategias
ya vistas anteriormente para acceder a otros soportes de datos:

- La primera estrategia es la misma que sigue la librería commons-csv_ para leer
  y escribir |CSV|.
- La segunda estrategia es la que sigue la `librería Jackson`_ para leer y
  escribir distintos formatos.

.. _conn-vs-orm:

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
   #. Los conectores suelen formar parte de las librerías básicas del lenguaje,
      por lo que no necesitaremos usar una librería de terceros (la librería
      |ORM|) ni tendremos que reescribir el código si decidimos cambiar a un
      |ORM| distinto.

**Ventajas** de las herramientas |ORM|
   a. Al proveer un mecanismo para traducir el modelo relacional al modelo de
      datos del lenguaje de programación (modelo de objetos), son herramientas
      más productivas.
   #. El programador trata los datos directamente como objetos, lo que hace el
      código más sencillo y manipulable.
   #. Generalmente, cambiar de |SGBD| es trivial, puesto que la herramienta
      nos abstrae de sus particularidades. Su uso, por tanto, nos independiza
      de cuál sea el |SGBD| que gestione los datos frente a los conectores que
      usan sentencias |SQL|, generalmente dependientes de cuál es el |SGBD|. En
      contrapartida, puede resultar muy trabajoso cambiar de |ORM|.

En esta unidad abordaremos la primera estrategia y dejaremos la segunda para la
:ref:`unidad siguiente <orm>`.

Java proporciona para el acceso a bases de datos relacionales una |API| en su
|JDK| llamada |JDBC|, lo que posibilita que el acceso sea idéntico sea cual sea
el |SGBD| que se decida utilizar. Esto no significa que el código sea
independiente del motor subyacente, puesto que cada motor tiene sus
particularidades que extienden el |SQL| estándar y esta estrategia, al estar
basada en la construcción de sentencias |SQL|, nos obliga a utilizarlas. Lo que
en realidad es común son los métodos que proporciona Java para ordenar la
ejecución de las sentencias al motor de la base de datos. Además de la
|API|, necesitaremos para cada |SGBD| un *driver* basado en la |API| que
posibilite la conexión. Este *driver* sí que será una librería de terceros que
deberemos incluir en nuestro proyecto.

.. rubric:: SQLite

Para aprender y practicar el acceso con conectores, podemos escoger cualquier
|SGBD|. Utilizaremos en el caso de estos apuntes, SQLite_ por varias razones:

+ Comodidad, ya que a diferencia de otros (MariaDB_, Oracle_, etc) no requiere
  un servidor: la base de datos es un archivo.
+ Es una base de datos empotrada y se usa mucho en aplicaciones que necesitan
  organizar sus datos en una base de datos exclusiva en la que no
  concurrirán otros procesos.

Con la excepción de las propias sentencias |SQL| que pueden variar de |SGBD| o
|SGBD|, las explicaciones serán totalmente válidas. Antes, no obstante, es
necesario que tengamos **instalado el motor**. En el caso de :program:`SQLite`:

.. _rst-simple:

* Las distribuciones de *Linux* suelen incluir un paquete, por lo que su
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

Tomaremos como base un problema muy sencillo en el que simplemente existen
centros y estudiantes matriculados en ellos. Podemos representarlo
gráficamente con el siguente diagrama |E/R|:

.. image:: files/er-ec.png

Podemos traducir el anterior esquema al modelo relacional así:

.. code-block:: none

   Centro(*id*, nombre, titularidad)
   Estudiante(*id*, nombre, nacimiento, _centro_)

El cual en |SQL| se define así:

.. literalinclude:: files/ec.sql
   :name: ec-esquema
   :language: sql
   :end-before: -- Datos

.. rubric:: Contenidos

.. toctree:: 
   :maxdepth: 1
   :glob:

   [0-9]*

.. |POO| replace:: :abbr:`POO (Programación Orientada a Objetos)`
.. |SQL| replace:: :abbr:`SQL (Structured Query Language)`
.. |CSV| replace:: :abbr:`CSV (Comma-Separated Values)`
.. |ORM| replace:: :abbr:`ORM (Object-Relational Mapping)`
.. |API| replace:: :abbr:`API (Application Programming Interface)`
.. |JDK| replace:: :abbr:`JDK (Java Development Kit)`
.. |JDBC| replace:: :abbr:`JDBC (Java DataBase Connectivity)`
.. |SGBD| replace:: :abbr:`SGBD (Sistema Gestor de Bases de Datos)`
.. |E/R| replace:: :abbr:`E/R (Entidad/Relación)`

.. _commons-csv: https://commons.apache.org/proper/commons-csv/project-info.html
.. _librería Jackson: https://github.com/FasterXML/jackson
.. _MariaDB: https://mariadb.org/
.. _Oracle: https://www.oracle.com/es/database/
.. _SQLite: https://sqlite.org/
.. _sqlite-jdbc: https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
.. _mariadb-java-client: https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
