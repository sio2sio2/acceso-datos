import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

abstract class AbstractDateTypeAdapter extends TypeAdapter<Date> {

    protected abstract DateFormat getDateFormat();

    @Override
    @SuppressWarnings("resource")
    public final void write(final JsonWriter out, final Date value) throws IOException {
        out.value(getDateFormat().format(value));  // Traduce Date a una cadena "yyyy-MM-dd"
    }

    @Override
    public final Date read(final JsonReader in) {
        try {
            return getDateFormat().parse(in.nextString());
        }
        catch (Exception err) {
            return null;
        }
    }
}
