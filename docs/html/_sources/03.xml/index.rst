|XML|
*****
Para la manipulación del formato :ref:`XML <lm:xml>` tenemos varias estrategias
y, dentro de ellas varias alternativas. Nos centraremos en tres:

#. La carga en memoria del documento completo para la creación de su |DOM| y
   el acceso a sus elementos mediante técnicas ya analizadas en el módulo de
   :external+lm:doc:`Lenguajes de Marcas <index>` (:ref:`XPath <lm:xpath>`,
   :ref:`XQuery <lm:xquery>` o :ref:`XSLT <lm:xslt>`). Usan esta estrategia
   |JAXP| (que está incluida en |JDK|) o `Saxon
   <https://www.saxonica.com/welcome/welcome.xml>`_, que es más completa y tiene
   versión comercial.

#. La traducción entre el |XML| y un modelo de objetos, tal como se hizo al
   tratar |JSON|. También hay varias alternativas: nosotros trataremos
   *Jackson*, que :ref:`ya usamos para tratar JSON <json-jackson>`.

#. El accesso a bases de datos |XML| como :ref:`BaseX <lm:basex>`.

Vayamos con ellas:

.. toctree::
   :glob:
   :maxdepth: 2

   [0-9]*

.. |XML| replace:: :abbr:`XML (eXtensible Markup Language)`
.. |DOM| replace:: :abbr:`DOM (Document Object Model)`
.. |JAXP| replace:: :abbr:`JAXP (Java API for XML Processing)`
.. |JDK| replace:: :abbr:`JDK (Java Development Kit)`
.. |JSON| replace:: :abbr:`JSON (JavaScript Object Notation)`
