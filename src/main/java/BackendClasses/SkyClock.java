package BackendClasses;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkyClock {
    private static final String REGION_URL = "https://raw.githubusercontent.com/cmstead/sky-clock/refs/heads/main/src/date-tools/regional-time.js";

    private static final Pattern CONST_VAR_PATTERN = Pattern.compile("const\\s+(\\w+)\\s*= '\\s*(.+?)';");

    public static final String US_PACIFIC_TIME_ZONE = "US_PACIFIC_TIME_ZONE";

    private static volatile HashMap<String, String> REGION_DATA = new HashMap<>();

    private static HashMap<String, String> getVariables(String url) {
        StringBuilder data = Requests.requestGetString(url);

        Matcher matcher = CONST_VAR_PATTERN.matcher(data);

        HashMap<String, String> variables = new HashMap<>();

        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            variables.put(name, value);
            if (name.equals("error") && !value.equals("None")) {
                System.err.println(value);
                return REGION_DATA;
            }
        }

        return variables;
    }

    public static void refreshData() {
        REGION_DATA = getVariables(REGION_URL);
    }


    public static ZonedDateTime getSkyTime() {
        return ZonedDateTime.now(
                ZoneId.of(REGION_DATA.get(US_PACIFIC_TIME_ZONE))
        );
    }

    static {refreshData();}
}
