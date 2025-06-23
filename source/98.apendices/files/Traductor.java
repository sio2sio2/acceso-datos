public interface Traductor {
   // Posiblemente sea una lista de un tipo determinado, no el genérico Object.
   public List<Object> leer(InputStream st) throws IOException;
   public void escribir(OutputStream st, List<Object> data) throws IOException;
}
