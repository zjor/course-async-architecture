package aa.billing.util;

import java.time.Instant;

public class TimeUtil {

    private static final long FIVE_MINUTES_SECONDS = 5 * 60L;

    public static Instant floorTo(Instant i, long intervalSeconds) {
        var millis = i.toEpochMilli();
        return Instant.ofEpochMilli(millis - (millis % (intervalSeconds * 1000)));
    }


    public static Instant floorNowTo(long intervalSeconds) {
        return floorTo(Instant.now(), intervalSeconds);
    }

    public static Instant getCycleStartFromNow() {
        return floorNowTo(FIVE_MINUTES_SECONDS);
    }

    public static Instant getCycleEnd(Instant start) {
        var i = Instant.ofEpochMilli(start.toEpochMilli() + FIVE_MINUTES_SECONDS * 1000L + 1);
        return floorTo(i, FIVE_MINUTES_SECONDS);
    }
}
