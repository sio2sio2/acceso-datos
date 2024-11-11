.. _conectores:

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
unidad siguiente.

Java proporciona para el acceso a bases de datos relacionales una |API| en su
|JDK| llamada |JDBC|, lo que posibilita que el acceso sea idéntico sea cual sea
el |SGBD| que se decida utilizar. Esto no significa que el código sea
independiente del motor subyacente, puesto que cada motor tiene sus
particularidades que extienden el |SQL| estándar y esta estrategia nos obliga a
utilizarlo. Lo que en realidad es común son los métodos que proporciona Java
para ejecutar sentencias en la base de datos.

Previo
======

.. sqlite: instalación.

Conexión
========

Ejecución de sentencias
=======================

Simples
-------

Parametrizadas
--------------

Transacciones
=============

.. incluir executeBatch.

Fechas y horas
==============

Extras
======

.. + setMultiple
   + Java funcional.

.. |SQL| replace:: :abbr:`SQL (Structured Query Language)`
.. |CSV| replace:: :abbr:`CSV (Comma-Separated Values)`
.. |ORM| replace:: :abbr:`ORM (Object-Relational Mapping)`
.. |API| replace:: :abbr:`API (Application Programming Interface)`
.. |JDK| replace:: :abbr:`JDK (Java Development Kit)`
.. |JDBC| replace:: :abbr:`JDBC (Java DataBase Connectivity)`
.. |SGBD| replace:: :abbr:`SGBD (Sistema Gestor de Bases de Datos)`

.. _commons-csv: https://commons.apache.org/proper/commons-csv/project-info.html
.. _librería Jackson: https://github.com/FasterXML/jackson
