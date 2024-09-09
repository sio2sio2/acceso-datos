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
    'sphinx.ext.todo'
]

templates_path = ['_templates']
exclude_patterns = []

language = 'es'

# -- Options for HTML output -------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#options-for-html-output

html_theme = 'sphinx_book_theme'
html_static_path = ['_static']
html_logo = '_static/logo.jpg'

extlinks = {
    'java-util': ('https://docs.oracle.com/javase/8/docs/api/java/util/%s.html', 'java.util.%s'),
    'java-text': ('https://docs.oracle.com/javase/8/docs/api/java/text/%s.html', 'java.text.%s'),
    'java-io': ('https://docs.oracle.com/javase/8/docs/api/java/io/%s.html', 'java.io.%s'),
    'java-nio': ('https://docs.oracle.com/javase/8/docs/api/java/nio/file/%s.html', 'java.nio.file.%s'),
    'java-lang': ('https://docs.oracle.com/javase/8/docs/api/java/lang/%s.html', 'java.lang.%s'),
    'java-function': ('https://docs.oracle.com/javase/8/docs/api/java/util/function/%s.html', 'java.util.function.%s'),
    'java-stream': ('https://docs.oracle.com/javase/8/docs/api/java/util/stream/%s.html', 'java.util.stream.%s'),
    'deb': ('https://packages.debian.org/stable/%s','%s'),
}

intersphinx_mapping = {
    'linux': ('https://sio2sio2.github.io/doc-linux/', None),
    'lm': ('https://sio2sio2.github.io/LM/', None),
}

#intersphinx_disabled_reftypes = ["std:doc"]


todo_include_todos = True
