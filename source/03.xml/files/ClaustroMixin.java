@JsonRootName(value = "claustro")
public class ClaustroMixin {
    @JacksonXmlProperty(isAttribute = true)
    private String centro;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "profesor")
    private Profesor[] plantilla;
}
