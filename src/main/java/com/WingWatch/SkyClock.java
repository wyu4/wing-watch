package com.WingWatch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            new EventData("Dream Skater", new Integer[] {5,6,7}, 2*60*60, 60*60, 10*60, EventData.TimeType.SKY)
    };

    public static final EventData[] DAY_CYCLE = {
//            new EventData("Nighttime", 24*60*60, 0, 5*60*60, EventData.TimeType.LOCAL),
            new EventData("Nest Sunset", 60*60, 40*60, (10*60) + 20, EventData.TimeType.SKY),
            new EventData("Home Sunrise", 24*60*60, 5*60*60, 4*60*60, EventData.TimeType.LOCAL),
            new EventData("Home Cloudy", 24*60*60, 9*60*60, 60*60, EventData.TimeType.LOCAL),
            new EventData("Home Daytime", 24*60*60, 10*60*60, 6*60*60, EventData.TimeType.LOCAL),
            new EventData("Home Cloudy", 24*60*60, 16*60*60, 60*60, EventData.TimeType.LOCAL),
            new EventData("Home Sunset", 24*60*60, 17*60*60, 4*60*60, EventData.TimeType.LOCAL),
            new EventData("Home Nighttime", 24*60*60, 21*60*60, 8*60*60, EventData.TimeType.LOCAL)
    };

    public static final EventData[] QUESTS = {
            new EventData("Daily Quests", 24*60*60, 0, 24*60*60, EventData.TimeType.SKY),
    };

    public static final EventData[] CONCERTS_SHOWS = {
            new EventData("Aurora", 4*60*60, (2*60*60)+(10*60), 50*60, EventData.TimeType.SKY)
    };

    public static final EventData[] RESETS = {
            new EventData("Daily Reset", 24*60*60, 0, 0, EventData.TimeType.SKY),
            new EventData("Weekly Reset", new Integer[] {7}, 24*60*60, 0, 0, EventData.TimeType.SKY),
            new EventData("Passage Quest Reset", 15*60, 0, 0, EventData.TimeType.SKY),
    };

    public static void refreshData() throws IOException, URISyntaxException {
        REGION_DATA = getVariables(REGION_URL);
    }

    public static String formatTimeLeft(Long remainingTime) {
        if (remainingTime == null || remainingTime < 0) {
            return "???";
        }
        return String.format("%02d : %02d : %02d : %02d",
                remainingTime/(60*60*24),
                remainingTime/(60*60) % 24,
                remainingTime/(60) % 60,
                remainingTime % 60
        );
    }

    private static ZoneId getSkyZone() {
        return ZoneId.of(REGION_DATA.get(US_PACIFIC_TIME_ZONE));
    }

    public static ZonedDateTime getSkyTime() {
        return ZonedDateTime.now(getSkyZone());
    }
}
