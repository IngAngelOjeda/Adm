package py.gov.mitic.adminpy.util;

import javax.ws.rs.BadRequestException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Util {
    
    public static Boolean NonNullNonEmpty(String data) {
    	boolean result = false;
        if(Objects.nonNull(data) && !data.isEmpty()) {
            result = true;
        }
        return result;
    }

    /**
     * getContentType header
     * @param format
     * @return
     */
    public static String getContentType(String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
        } else if ("excel".equalsIgnoreCase(format)) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if ("csv".equalsIgnoreCase(format)) {
            return "text/csv";
        } else {
            throw new IllegalArgumentException("Formato de informe no admitido");
        }
    }

}
