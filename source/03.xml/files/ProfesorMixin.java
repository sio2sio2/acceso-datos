public abstract class ProfesorMixin {
    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private String sustituye;

    @JacksonXmlProperty(isAttribute = true)
    private String casillero;
}
