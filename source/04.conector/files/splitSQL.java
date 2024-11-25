 /**
  * Descompone un guión SQL en las sentencias de que se compone.
  * @param st Entrada de la que se lee el guión
  * @return  Una lista con las sentencias separadadas.
  * @throws IOException
  */
 private List<String> splitSQL(InputStream st) throws IOException {
     Pattern beginPattern = Pattern.compile("\\b(BEGIN|CASE)\\b", Pattern.CASE_INSENSITIVE);
     Pattern endPattern = Pattern.compile("\\bEND\\b", Pattern.CASE_INSENSITIVE);

     try (
         InputStreamReader sr = new InputStreamReader(st, StandardCharsets.UTF_8);
         BufferedReader br = new BufferedReader(sr);
     ) {
         List<String> sentencias = new ArrayList<>();
         String linea;
         String sentencia = "";
         int contador = 0;
         while((linea = br.readLine()) != null) {
             linea = linea.trim();
             if(linea.isEmpty()) continue;

             Matcher beginMatcher = beginPattern.matcher(linea);
             Matcher endMatcher = endPattern.matcher(linea);

             while(beginMatcher.find()) contador++;
             while(endMatcher.find()) contador--;

             sentencia += "\n" + linea;

             if(contador == 0 && linea.endsWith(";")) {
                 sentencias.add(sentencia);
                 sentencia = "";
             }
         }
         return sentencias;
     }
 }
