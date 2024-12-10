package com.WingWatch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
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
            new EventData("Polluted Geyser", 2*60*60, (5*60) + 11, 10*60, EventData.TimeType.SKY),
            new EventData("Grandma's Dinner Event", 2*60*60, 35*60 + 20, 10*60, EventData.TimeType.SKY),
            new EventData("Sunset Sanctuary Turtle", 2*60*60, 50*60 + 17, 10*60, EventData.TimeType.SKY),
            new EventData("Dream Skater", 2*60*60, 60*60, 10*60, EventData.TimeType.SKY)
    };

    public static final EventData[] DAY_CYCLE = {
//            new EventData("Nighttime", 24*60*60, 0, 5*60*60, EventData.TimeType.LOCAL),
            new EventData("Sunrise", 24*60*60, 5*60*60, 4*60*60, EventData.TimeType.LOCAL),
            new EventData("Cloudy (morning)", 24*60*60, 9*60*60, 60*60, EventData.TimeType.LOCAL),
            new EventData("Daytime", 24*60*60, 10*60*60, 6*60*60, EventData.TimeType.LOCAL),
            new EventData("Cloudy (noon)", 24*60*60, 16*60*60, 60*60, EventData.TimeType.LOCAL),
            new EventData("Sunset", 24*60*60, 17*60*60, 4*60*60, EventData.TimeType.LOCAL),
            new EventData("Nighttime", 24*60*60, 21*60*60, 8*60*60, EventData.TimeType.LOCAL)
    };

    public static final EventData[] QUESTS = {
            new EventData("Passage Quests", 15*60, 0, 0, EventData.TimeType.SKY),
            new EventData("Shop Quests", 2*60*60, 35*60 + 20, 10*60, EventData.TimeType.SKY)
    };

    public static final EventData[] RESETS = {
            new EventData("Daily Reset", 24*60*60, 0, 1, EventData.TimeType.SKY),
            new EventData("Weekly Reset", DayOfWeek.SUNDAY, 0, 1, EventData.TimeType.SKY)
    };

    public static void refreshData() throws IOException, URISyntaxException {
        REGION_DATA = getVariables(REGION_URL);
    }

    public static String formatTimeLeft(long remainingTime) {
        return String.format("%02d : %02d : %02d : %02d",
                remainingTime/(60*60*24),
                remainingTime/(60*60) % 24,
                remainingTime/(60) % 60,
                remainingTime % 60
        );
    }

    public static ZonedDateTime getSkyTime() {
        return ZonedDateTime.now(
                ZoneId.of(REGION_DATA.get(US_PACIFIC_TIME_ZONE))
        );
    }
}
