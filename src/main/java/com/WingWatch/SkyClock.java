package com.WingWatch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SkyClock {
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

    public static final EventData[] WAX_EVENTS = {
            new EventData(EventData.N_GEYSER, 2*60, 5),
            new EventData(EventData.N_GRANDMA, 2*60, 35),
            new EventData(EventData.N_TURTLE, 2*60, 50),
            new EventData(EventData.N_SKATER, 2*60, 60)
    };

    public static void refreshData() throws IOException, URISyntaxException {
        REGION_DATA = getVariables(REGION_URL);
    }


    public static ZonedDateTime getSkyTime() {
        return ZonedDateTime.now(
                ZoneId.of(REGION_DATA.get(US_PACIFIC_TIME_ZONE))
        );
    }
}
