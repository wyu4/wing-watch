package com.WingWatch.WebScraping;

import com.WingWatch.EventData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class WikiUtils {
    private static final String TRAVELLING_SPIRIT = "https://sky-children-of-the-light.fandom.com/wiki/Traveling_Spirits";
    private static final String SEASONAL_EVENTS = "https://sky-children-of-the-light.fandom.com/wiki/Seasonal_Events";

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

    private static ZonedDateTime[] getPeriod(ZonedDateTime defaultTime, String dateText) {
        ZonedDateTime[] result = {defaultTime, defaultTime};
        try {
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

    public static void refreshSources() {
        SOURCES.put(TRAVELLING_SPIRIT, getSource(TRAVELLING_SPIRIT));
        SOURCES.put(SEASONAL_EVENTS, getSource(SEASONAL_EVENTS));
    }

    public static EventData getTravellingSpirit(ZonedDateTime currentTime) {
        String source = SOURCES.get(TRAVELLING_SPIRIT);
        String nameText = "Unknown Spirit";
        String dateText = null;

        try {
            Document doc = Jsoup.parse(source);
            Element nameClass = doc.selectFirst("h2[data-source=title]");
            if (nameClass != null) {
                nameText = nameClass.text();
            } else {
                throw new NullPointerException("Name not found in source.");
            }

            Element dateClass = doc.selectFirst(".pi-data-value.pi-font");
            if (dateClass != null) {
                dateText = dateClass.text();
            } else {
                throw new NullPointerException("Date not found in Traveling Spirit source.");
            }

            ZonedDateTime[] period = getPeriod(currentTime, dateText);
            return new EventData(nameText, period[0], period[1], 14*24*60*60);
        } catch (Exception e) {
            System.err.println("Could not create travelling spirit event: " + e.getMessage());
            return new EventData(nameText);
        }
    }

    public static EventData getSeasonEvent(ZonedDateTime currentTime) {
        String source = SOURCES.get(SEASONAL_EVENTS);
        String nameText = "Season of ???";
        String dateText = null;

        try {
            Document doc = Jsoup.parse(source);
//            Element nameSection = doc.selectFirst("li:has(span:matchesOwn(Seasons & Events))");
//            if (nameSection == null) {
//                throw new NullPointerException("Seasons & Events section not found in Seasonal Source.");
//            }
//            Element latestSeasonName = nameSection.selectFirst(".wds-dropdown__content ul.wds-list li span");
//            if (latestSeasonName != null) {
//                nameText = latestSeasonName.text();
//            } else {
//                throw new NullPointerException("Latest name not found in Seasonal Source's section.");
//            }

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
                nameText = latestSeasonName.text();
            }

            Element dateClass = latestSeasonArticle.selectFirst("td:not(:has(*))");
            if (dateClass == null) {
                throw new NullPointerException("Latest season period in row in Article table not found in Seasonal Source.");
            } else {
                dateText = dateClass.text();
            }

            ZonedDateTime[] period = getPeriod(currentTime, dateText);
            return new EventData(nameText, period[0], period[1], 14*24*60*60);
        }  catch (Exception e) {
            System.err.println("Could not create season event: " + e.getMessage());
            return new EventData(nameText);
        }
    }

    public static void main(String[] args) throws IOException {
        SkyClockUtils.refreshData();
        refreshSources();
        System.out.println(getSeasonEvent(SkyClockUtils.getSkyTime()));

//        File output = new File("output.html");
//        output.createNewFile();
//        System.setOut(new PrintStream(output));
//        System.out.println(SOURCES.get(SEASONAL_EVENTS));
    }
}
