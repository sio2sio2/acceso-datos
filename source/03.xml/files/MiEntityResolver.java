import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class MiEntityResolver implements EntityResolver2 {
    @Override
    public InputSource getExternalSubset(String name, String baseURI)
            throws SAXException, IOException {
        return null;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        return resolveEntity(null, publicId, null, systemId);
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId)
            throws SAXException, IOException {

        if (systemId == null) return null;

        try {
            systemId = resolvePath(baseURI, systemId);
        } catch(URISyntaxException e) {
            return null;
        }
        
        return new InputSource(systemId);
    }

    private String resolvePath(String base, String path) throws URISyntaxException {
        if(new URI(path).isAbsolute()) return path;
        if(base == null) throw new IllegalArgumentException("No puede calcularse una ruta relativa si la base es nula.");

        if(base.contains("!")) {
            // Nos quedamos con la parte del path dentro del jar
            Path basePath = Path.of(base.substring(base.indexOf("!") + 1));
            // Resolvemos el path relativo y devolvemos el recurso.
            URL resource = getClass().getResource(basePath.resolveSibling(path).toString());
            return resource != null ? resource.toString() : null;
        }

        return new URI(base).resolve(path).toString();
    }
}
