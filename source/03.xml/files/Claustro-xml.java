@JacksonXmlRootElement(localName = "claustro")
public class Claustro {

    @JacksonXmlProperty(isAttribute = true)
    private String centro;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "profesor")
    private Profesor[] plantilla;

    public Claustro() {}

    public Claustro(String centro, Profesor[] plantilla) {
        setCentro(centro);
        setPlantilla(plantilla);
    }

    public String getCentro() { return centro; }
    public void setCentro(String centro) { this.centro = centro; }
    public Profesor[] getPlantilla() { return plantilla; }
    public void setPlantilla(Profesor[] plantilla) { this.plantilla = plantilla; }

    public String toString() {
        return String.format("%s -- %s", centro, Arrays.toString(plantilla));
    }
}
