public class TraductorFactory {

   public static Traductor crearTraductor(String formato)
         throws IllegalArgumentException, UnsupportedOperationException {

      return switch (formato.toLowerCase()) {
         // Formatos implementados
         case "txt" -> new TTxt();
         case "csv" -> new TCsv();
         case "json" -> new TJson();
         case "yaml" -> new TYaml();
         // Formatos conocidos que no se han implementado
         case "xml" -> throw new UnsupportedOperationException(String.format("'%s': Formato no soportado.", formato));
         // Cualquier otra cosa, no es un formato que reconozcamos
         default -> throw new IllegalArgumentException(String.format("'%s': Formato desconocido.", formato));
      };
   }
}
