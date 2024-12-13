package com.WingWatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;

public class WikiUtils {
    private static final String TRAVELLING_SPIRIT = "https://sky-children-of-the-light.fandom.com/wiki/Traveling_Spirits";
    private static final String DATE_CLASS = ".pi-data-value.pi-font";
    private static final String NAME_CLASS = "h2[data-source=title]";

    private static final HashMap<String, Integer> MONTHS = new HashMap<>();

    static {
        MONTHS.put("JANUARY", 1);
        MONTHS.put("FEBRUARY", 2);
        MONTHS.put("MARCH", 3);
        MONTHS.put("APRIL", 4);
        MONTHS.put("MAY", 5);
        MONTHS.put("JUNE", 6);
        MONTHS.put("JULY", 7);
        MONTHS.put("AUGUST", 8);
        MONTHS.put("SEPTEMBER", 9);
        MONTHS.put("OCTOBER", 10);
        MONTHS.put("NOVEMBER", 11);
        MONTHS.put("DECEMBER", 12);
    }

    private static String getSource(String wikiUrl) {
        try {
            return Requests.requestGetString(wikiUrl).toString();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDateText(String source) {
        Document doc = Jsoup.parse(source);
        Element date = doc.selectFirst(DATE_CLASS);
        if (date != null) {
            return date.text().toUpperCase().replaceAll(",", "");
        } else {
            throw new NullPointerException("Date not found in Traveling Spirit source.");
        }
    }

    public static String getNameText(String source) {
        Document doc = Jsoup.parse(source);
        Element date = doc.selectFirst(NAME_CLASS);
        if (date != null) {
            return date.text();
        } else {
            System.err.println("Name not found in source.");
            return "Unknown Spirit";
        }
    }

    private static ZonedDateTime[] getPeriod(ZonedDateTime defaultTime, String dateText) {
        ZonedDateTime[] result = {defaultTime, defaultTime};
        try {
            if (dateText == null) {
                throw new NullPointerException("dateText must not be null.");
            }

            String[] dateData = dateText.split(" ");

            Integer month1 = null;
            Integer day1 = null;
            Integer month2 = null;
            Integer day2 = null;
            Integer year1 = null;
            Integer year2 = null;

            for (int i = 0; i < dateData.length; i++) {
                String item = dateData[i];
                try {
                    int number = Integer.parseInt(item);
                    if (number <= 31) {
                        if (day1 == null) {
                            day1 = number;
                        } else {
                            day2 = number;
                        }
                    } else {
                        if (year1 == null) {
                            year1 = number;
                        } else {
                            year2 = number;
                        }
                    }
                } catch(NumberFormatException e) {
                    if (MONTHS.containsKey(item)) {
                        if (month1 == null) {
                            month1 = MONTHS.get(item);
                        } else {
                            month2 = MONTHS.get(item);
                        }
                    }
                }
            }

            month1 = (month1==null ? defaultTime.getMonthValue() : month1);
            day1 = (day1==null ? defaultTime.getDayOfMonth() : day1);
            year1 = (year1==null ? defaultTime.getYear() : year1);
            month2 = (month2==null ? month1 : month2);
            day2 = (day2==null ? day1 : day2);
            year2 = (year2==null ? year1 : year2);

            result[0] = ZonedDateTime.of(year1, month1, day1, 0, 0, 0, 0, defaultTime.getZone());
            result[1] = ZonedDateTime.of(year2, month2, day2+1, 0, 0, 0, 0, defaultTime.getZone());
        } catch (Exception e) {
            System.err.println("Could not get the time range from text \"" + dateText + "\": " + e.getMessage());
        }

        return result;
    }

    public static EventData getTravellingSpirit(ZonedDateTime currentTime) {
        String source = getSource(TRAVELLING_SPIRIT);
        ZonedDateTime[] period = getPeriod(currentTime, getDateText(source));
        return new EventData(getNameText(source), period[0], period[1], 14*24*60*60);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        SkyClockUtils.refreshData();

    }
}
