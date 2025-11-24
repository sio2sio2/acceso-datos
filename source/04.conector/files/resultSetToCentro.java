public static Centro resultSetToCentro(ResultSet rs) throws SQLException {

   int id = rs.getInt("id");
   String nombre = rs.getString("nombre");
   // Suponemos que hemos definido mejor Titularidad y hemos creado
   // un método estático que obtiene Titularidad.PUBLICA a partir de "pública", etc.
   Titularidad titularidad = Titularidad.fromString(rs.getString("titularidad"));

   return new Centro(id, nombre, titularidad);

}
