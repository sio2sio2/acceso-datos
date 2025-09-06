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

*Java* presenta la enorme ventaja de que define una especificación muy completa
llamada |JPA| para el uso de |ORM|, de manera que, si usamos |ORM|\ s
compatibles con ella, podremos migrar de uno a otro con un mínimo esfuerzo. El
|ORM| más utilizado en Java es Hibernate_ y es compatible, por lo que esta
unidad explicará cómo usar esta especificación usando como implementación
Hibernate_, pero podríamos usar cualquier otro |ORM| siempre que nos
aseguráramos que es compatible con |JPA|\ [#]_. Los ejemplos los basaremos en
:ref:`nuestro caso sobre centros y profesores <ej-centros-alumnos>`.

.. todo:: Investigar el enfoque de MyBatis_ (quizás para la unidad anterior).

.. rubric:: Contenidos

.. toctree::
   :maxdepth: 1
   :glob:

   [0-9]*

.. seealso:: Dado que usaremos Hibernate_ como implementación de |JPA|, puede consultar
   también su `pequeña guía oficial
   <https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html>`_.

.. rubric:: Notas al pie

.. [#] Por ejemplo, `EclipseLink <https://eclipse.dev/eclipselink/>`_ o `OpenJPA <https://openjpa.apache.org/>`_.

.. |ORM| replace:: :abbr:`ORM (Object-Relational Mapping)`

.. _MyBatis: https://mybatis.org/mybatis-3/es/
.. _Hibernate: https://hibernate.org/
.. |JPA| replace:: :abbr:`JPA (Java Persistent API)`
