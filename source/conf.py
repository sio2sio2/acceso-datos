# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Project information -----------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#project-information
from datetime import date

project = 'Acceso a datos'
copyright = ('CC BY 4.0, 2024-' + str(date.today().year) +
             ', José Miguel Sánchez Alés')
author = 'José Miguel Sánchez Alés'
release = 'rolling'

# -- General configuration ---------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#general-configuration

extensions = [
    'sphinx.ext.extlinks',
    'sphinx.ext.intersphinx',
    'sphinx.ext.todo',
    'sphinx_copybutton',
    'sphinx_togglebutton',
    'sphinx.ext.mathjax'
]

templates_path = ['_templates']
exclude_patterns = []

language = 'es'

# -- Options for HTML output -------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#options-for-html-output

html_theme = 'sphinx_book_theme'
html_static_path = ['_static']
html_logo = '_static/logo.jpg'

html_theme_options = {
    #"collapse_navbar": True
    #"max_navbar_depth": 2,
}

extlinks = {
      'java-util': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/util/%s.html', 'java.util.%s'),
    'java-text': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/text/%s.html', 'java.text.%s'),
    'java-io': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/io/%s.html', 'java.io.%s'),
    'java-nio': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/nio/file/%s.html', 'java.nio.file.%s'),
    'java-lang': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/lang/%s.html', '%s'),
    'java-jaxp': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.xml/org/w3c/dom/%s.html', '%s'),
    'java-function': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/util/function/%s.html', 'java.util.function.%s'),
    'java-stream': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/util/stream/%s.html', 'java.util.stream.%s'),
    'java-sql': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.sql/java/sql/%s.html', 'java.sql.%s'),
    'javax-sql': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.sql/javax/sql/%s.html', 'javax.sql.%s'),
    'java-math': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/math/%s.html', '%s'),
    'java-time': ('https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/time/%s.html', '%s'),
    'hibernate-api': ('https://docs.jboss.org/hibernate/orm/7.0/javadocs/org/hibernate/%s.html', '%s'),
    'deb': ('https://packages.debian.org/stable/%s','%s'),
    'jakarta-persistence': ('https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/%s', '%s'),
    'java-slf4j': ('https://www.slf4j.org/api/org/slf4j/%s.html', '%s'),
    'jackson-databind': ('https://www.javadoc.io/doc/tools.jackson.core/jackson-databind/latest/tools.jackson.databind/tools/jackson/databind/%s.html', '%s'),
    'jackson-core': ('https://javadoc.io/doc/tools.jackson.core/jackson-core/latest/tools.jackson.core/tools/jackson/core/%s.html', '%s')
}

intersphinx_mapping = {
    'linux': ('https://sio2sio2.github.io/doc-linux/', None),
    'lm': ('https://sio2sio2.github.io/LM/', None),
}

#intersphinx_disabled_reftypes = ["std:doc"]


todo_include_todos = True

# Eliminamos los prompts de la copia
copybutton_prompt_text = r"\$ |>>> |jshell> "
copybutton_prompt_is_regexp = True
# Permite :class:no-copybutton
copybutton_selector = "div:not(.no-copybutton) > div.highlight > pre"
