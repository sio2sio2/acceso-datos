.. _orm:

Herramientas |ORM|
==================
|ORM| es el acrónimo de 'Mapeo objeto-relacional' por lo que el propio nombre
define muy descriptivamente cuál es la finalidad de estas herramientas. Si hemos
estudiado la unidad anterior y, sobre todo, hemos intentado :ref:`codificar
estructuradamente una aplicación con conectores <conn-prog>`, habremos notado que
nuestro mayor esfuerzo se concentra en trasladar el modelo relacional de la base
de datos al modelo de objetos del lenguaje de programación.

Las herramientas |ORM| son herramientas pensadas para realizar ellas mismas esta
traducción entre los dos modelos y descargar al programador de esta
responsabilidad. Como ya se enumeraron las :ref:`ventajas e inconvenientes de
una estrategia frente a la otra <conn-vs-orm>`, no abundaremos más en ello.

.. todo:: Investigar el enfoque de MyBatis_.

El |ORM| más utilizado en Java es Hibernate_ y a su uso dedicaremos esta unidad
utilizando el :ref:`ejemplo ilustrativo sobre centros y profesores <conn-er-ec>`.

.. rubric:: Contenidos

.. toctree::
   :maxdepth: 1
   :glob:

   [0-9]*

.. seealso:: Puede consultar también la `pequeña guía oficial
   <https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html>`_.

.. |ORM| replace:: :abbr:`ORM (Object-Relational Mapping)`

.. _MyBatis: https://mybatis.org/mybatis-3/es/
.. _Hibernate: https://hibernate.org/
