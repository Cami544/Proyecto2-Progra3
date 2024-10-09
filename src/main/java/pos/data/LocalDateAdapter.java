package pos.data;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return LocalDate.parse(v);
    }

    public int getAno(){
        return LocalDate.now().getYear();
    }
    public String getMes(){
        return LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v.toString();
    }
}
