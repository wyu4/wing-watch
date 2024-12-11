package com.WingWatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;

public class TravelingSpirit {
    private static String SOURCE = null;
    private static final String WIKI = "https://sky-children-of-the-light.fandom.com/wiki/Traveling_Spirits";
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

    private static String getSource() {
        if (SOURCE != null) {
            return SOURCE;
        }
        try {
            SOURCE = Requests.requestGetString(WIKI).toString();
            return SOURCE;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDateText() {
        Document doc = Jsoup.parse(getSource());
        Element date = doc.selectFirst(DATE_CLASS);
        if (date != null) {
            return date.text().toUpperCase().replaceAll(",", "");
        } else {
            throw new NullPointerException("Date not found in Traveling Spirit source.");
        }
    }

    public static String getNameText() {
        Document doc = Jsoup.parse(getSource());
        Element date = doc.selectFirst(NAME_CLASS);
        if (date != null) {
            return date.text();
        } else {
            System.err.println("Name not found in Traveling Spirit source.");
            return "Unknown Spirit";
        }
    }

    private static ZonedDateTime[] getDate() {
        ZonedDateTime skyTime = SkyClock.getSkyTime();
        ZonedDateTime[] result = {skyTime, skyTime};
        String[] dateData = getDateText().split(" ");
        if (dateData.length >= 4) {
            String month1 = dateData[0];
            String day1 = dateData[1];
            String month2 = dateData[3];
            String day2 = dateData[4];

            int currentYear = skyTime.getYear();

            if (MONTHS.containsKey(month1)) {
                try {
                    result[0] = ZonedDateTime.of(currentYear, MONTHS.get(month1), Integer.parseInt(day1), 0, 0, 0, 0, skyTime.getZone());
                } catch (NumberFormatException ignore) {};
            }
            if (MONTHS.containsKey(month2)) {
                try {
                    result[1] = ZonedDateTime.of(MONTHS.get(month2) < MONTHS.get(month1) ? currentYear + 1 : currentYear, MONTHS.get(month2), Integer.parseInt(day2)+1, 0, 0, 0, 0, skyTime.getZone());
                } catch (NumberFormatException ignore) {};
            }
        }
        return result;
    }

    public static EventData createData() {
        ZonedDateTime[] dates = getDate();
        return new EventData(getNameText(), dates[0], dates[1]);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        SkyClock.refreshData();
        System.out.println(getNameText());
        System.out.println(Arrays.toString(getDate()));
    }
}
