package aa.billing.util;

import java.time.Instant;

public class TimeUtil {

    public static Instant floorNowTo(long intervalSeconds) {
        var now = Instant.now();
        var millis = now.toEpochMilli();
        return Instant.ofEpochMilli(millis - (millis % (intervalSeconds * 1000)));
    }

}
