import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class YyyyMMddDateTypeAdapter extends AbstractDateTypeAdapter {
    private YyyyMMddDateTypeAdapter() {}

    @Override
    protected DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}