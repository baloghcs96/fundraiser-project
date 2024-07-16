package hu.progmasters.fundraiser.domain.entity.token;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class TokenCreateFunction {
    protected static Date calculateExpiryDate(int EXPIRATION_MINUTE) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EXPIRATION_MINUTE);
        return new Date(cal.getTime().getTime());
    }
}
