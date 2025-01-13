package com.WingWatch.WebScraping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SkyClockUtils {
    private static final String REGION_URL = "https://raw.githubusercontent.com/cmstead/sky-clock/refs/heads/main/src/date-tools/regional-time.js";

    private static final Pattern CONST_VAR_PATTERN = Pattern.compile("const\\s+(\\w+)\\s*= '\\s*(.+?)';");

    public static final String US_PACIFIC_TIME_ZONE = "US_PACIFIC_TIME_ZONE";

    private static volatile HashMap<String, String> REGION_DATA = new HashMap<>();

    private static HashMap<String, String> getVariables(String url) throws IOException, URISyntaxException {
        StringBuilder data = Requests.requestGetString(url);

        Matcher matcher = CONST_VAR_PATTERN.matcher(data);

        HashMap<String, String> variables = new HashMap<>();

        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            variables.put(name, value);
        }

        return variables;
    }

    public static void refreshData() {
        try {
            REGION_DATA = getVariables(REGION_URL);
            LocalCache.setValue(US_PACIFIC_TIME_ZONE, REGION_DATA.get(US_PACIFIC_TIME_ZONE));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Could not refresh SkyClock data: " + e);
        }
    }

    public static String formatTimeLeft(Long remainingTime) {
        if (remainingTime == null || remainingTime == Long.MIN_VALUE) {
            return "???";
        }
        remainingTime = Math.abs(remainingTime);
        return String.format("%02d : %02d : %02d : %02d",
                remainingTime/(60*60*24),
                remainingTime/(60*60) % 24,
                remainingTime/(60) % 60,
                remainingTime % 60
        );
    }

    private static ZoneId getSkyZone(boolean tryCacheOnFail) {
        String zoneName = REGION_DATA.get(US_PACIFIC_TIME_ZONE);
        if (zoneName == null) {
            if (tryCacheOnFail) {
                REGION_DATA.put(US_PACIFIC_TIME_ZONE, LocalCache.getValue(US_PACIFIC_TIME_ZONE));
                return getSkyZone(false);
            } else {
                return null;
            }
        }
        return ZoneId.of(zoneName);
    }

    public static ZonedDateTime getSkyTime() {
        ZoneId zone = getSkyZone(true);
        if (zone == null) {
            return null;
        }
        return ZonedDateTime.now(zone).truncatedTo(ChronoUnit.SECONDS);
    }
}
