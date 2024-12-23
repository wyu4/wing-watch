package com.WingWatch.WebScraping;

import com.WingWatch.EventData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

public abstract class WikiUtils {
    private static final String TRAVELLING_SPIRIT = "https://sky-children-of-the-light.fandom.com/wiki/Traveling_Spirits";
    private static final String SEASONAL_EVENTS = "https://sky-children-of-the-light.fandom.com/wiki/Seasonal_Events";
    private static final String MAIN_PAGE = "https://sky-children-of-the-light.fandom.com/wiki/Sky:_Children_of_the_Light_Wiki";

    private static final HashMap<String, Integer> MONTHS = new HashMap<>();
    private static final HashMap<String, String> SOURCES = new HashMap<>();

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
            System.err.println("Error getting source " + wikiUrl + ": " + e.getMessage());
            return SOURCES.get(wikiUrl);
        }
    }

    private static ZonedDateTime[] getPeriod(ZoneId zone, String dateText) throws NullPointerException, IllegalArgumentException {
        ZonedDateTime[] result = {null, null};
        if (dateText == null) {
            throw new NullPointerException("dateText must not be null.");
        }
        String[] dateData = dateText.toUpperCase().replaceAll("\\p{Punct}", "").split(" ");
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

        if (day1 == null || month1 == null || year1 == null) {
            throw new IllegalArgumentException("dateText \"" + dateText + "\" is formatted incorrectly (ie. {month1} {day1} {year1} {month2} {day2} {year2})");
        }
        month2 = (month2==null ? month1 : month2);
        day2 = (day2==null ? day1 : day2);
        year2 = (year2==null ? year1 : year2);

        if (month1 > month2 || (month1.equals(month2) && day1 > day2)) {
            int temp = year1;
            year1 = year2 - 1;
            year2 = temp;
        }

        result[0] = ZonedDateTime.of(year1, month1, day1, 0, 0, 0, 0, zone);
        result[1] = ZonedDateTime.of(year2, month2, day2+1, 0, 0, 0, 0, zone);
        return result;
    }

    public static void refreshSources() {
        SOURCES.put(TRAVELLING_SPIRIT, getSource(TRAVELLING_SPIRIT));
        SOURCES.put(SEASONAL_EVENTS, getSource(SEASONAL_EVENTS));
        SOURCES.put(MAIN_PAGE, getSource(MAIN_PAGE));
    }

    public static EventData getTravellingSpirit(ZoneId zone) {
        String source = SOURCES.get(TRAVELLING_SPIRIT);
        ZonedDateTime[] period = new ZonedDateTime[2];

        try {
            if (source == null) {
                throw new NullPointerException("Source not found!?");
            }
            Document doc = Jsoup.parse(source);

            Element widget = doc.selectFirst("aside.portable-infobox.pi-background.pi-border-color.pi-theme-wikia.pi-layout-default.type-TravelingSpirit");
            if (widget == null) {
                throw new NullPointerException("Widget not found in source.");
            }

            Element nameClass = widget.selectFirst("h2[data-source=title]");
            if (nameClass != null) {
                LocalCache.setValue("TravellingSpiritName", nameClass.text());
            } else {
                throw new NullPointerException("Name not found in source.");
            }

            Element dateClass = widget.selectFirst("div[data-source=date]");
            if (dateClass != null) {
                LocalCache.setValue("TravellingSpiritDate", dateClass.text());
            } else {
                throw new NullPointerException("Date not found in Traveling Spirit source.");
            }
            period = getPeriod(zone, LocalCache.getValue("TravellingSpiritDate"));
        } catch (Exception e) {
            System.err.println("Could not create travelling spirit event: " + e.getMessage());
        }
        return new EventData(LocalCache.getValue("TravellingSpiritName", "Travelling Spirit"), period[0], period[1], 14*24*60*60);
    }

    public static EventData getSeasonEvent(ZoneId zone) {
        String source = SOURCES.get(SEASONAL_EVENTS);
        ZonedDateTime[] period = new ZonedDateTime[2];
        try {
            if (source == null) {
                throw new NullPointerException("Source not found!?");
            }
            Document doc = Jsoup.parse(source);

            Element articleTable = doc.selectFirst("table.article-table");
            if (articleTable == null) {
                throw new NullPointerException("Article table not found in Seasonal Source.");
            }
            Element latestSeasonArticle = articleTable.selectFirst("tr:has(a[title][href])");
            if (latestSeasonArticle == null) {
                throw new NullPointerException("Latest season row in Article table not found in Seasonal Source.");
            }
            Element latestSeasonName = latestSeasonArticle.selectFirst("td > a[title]");
            if (latestSeasonName == null) {
                throw new NullPointerException("Latest season name in row not found in Seasonal Source.");
            } else {
                LocalCache.setValue("SeasonName", latestSeasonName.text());
            }

            Element dateClass = latestSeasonArticle.selectFirst("td:not(:has(*))");
            if (dateClass == null) {
                throw new NullPointerException("Latest season period in row in Article table not found in Seasonal Source.");
            } else {
                LocalCache.setValue("SeasonDate", dateClass.text());
            }
            period = getPeriod(zone, LocalCache.getValue("SeasonDate"));
        }  catch (Exception e) {
            System.err.println("Could not create season event: " + e);
        }
        return new EventData(LocalCache.getValue("SeasonName", "Season of ???"), period[0], period[1], 14*24*60*60);
    }

    public static EventData getDaysEvent(ZoneId zone) {
        // Using the main page because the wiki doesn't have its own isolated page dedicated to current "days of" events. Should be changed once one has been found.
        String source = SOURCES.get(MAIN_PAGE);
        ZonedDateTime[] period = new ZonedDateTime[2];
        try {
            if (source == null) {
                throw new NullPointerException("Source not found!?");
            }
            Document doc = Jsoup.parse(source);
            Element middle = doc.selectFirst("div[class=mainpage-block mainpage-block-middle]");
            if (middle == null) {
                throw new NullPointerException("Could not find middle in Main page source.");
            }
            Element content = middle.selectFirst("center:has(b:has(a))");
            if (content == null) {
                throw new NullPointerException("Content in middle in Main page source.");
            }
            Element title = content.selectFirst("a[title]");
            if (title == null) {
                throw new NullPointerException("Could not find title in content in Main page source.");
            } else {
                LocalCache.setValue("DaysName", title.text());
            }
            Element breakline = content.selectFirst("br");
            if (breakline == null) {
                throw new NullPointerException("Breakline not found in content in Main page source.");
            }
            TextNode dateNode = (TextNode) breakline.nextSibling();
            if (dateNode == null) {
                throw new NullPointerException("Could not get date period from content.");
            } else {
                LocalCache.setValue("DaysDate", dateNode.text());
            }
            period = getPeriod(zone, LocalCache.getValue("DaysDate"));
        } catch (Exception e) {
            System.err.println("Could not create days event: " + e);
        }

        return new EventData(LocalCache.getValue("DaysName", "Days of ???"), period[0], period[1], 365*24*60*60);
    }

//    public static void main(String[] args) throws IOException {
//        File output = new File("test.html");
//        output.createNewFile();
//        System.setOut(new PrintStream(output));
//
//        refreshSources();
//        System.out.println(SOURCES.get(TRAVELLING_SPIRIT));
//    }
}
