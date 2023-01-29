import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private Set<String> hashsetSite;
    private Set<String> hashsetSite404;
    private HashMap<String, Integer> hashmapOperSys;
    private HashMap<String, Integer> haыshmapBrowser;
    public Statistics() {
        totalTraffic = 0;
        hashsetSite = new HashSet<>();
        hashsetSite404 = new HashSet<>();
        hashmapOperSys = new HashMap<>();
        haыshmapBrowser = new HashMap<>();
    }
    public long getTotalTraffic() { return totalTraffic; }
    void addEntry(LogEntry logEntry) {
        totalTraffic    += (long)logEntry.getResponseSize();
        if (logEntry.getTime() == null)
            return;
        if ((minTime == null) || (logEntry.getTime().isBefore(minTime))) {
            minTime = logEntry.getTime();
        }
        if ((maxTime == null) || (logEntry.getTime().isAfter(maxTime))) {
            maxTime = logEntry.getTime();
        }
        // если 200 - добавляем страницу в Statistics.hashsetSite
        if (logEntry.getResponseCode() == 200)
            hashsetSite.add(logEntry.getPath());
        // если 404 - добавляем страницу в Statistics.hashsetSite404 (несуществующих страниц сайта)
        if (logEntry.getResponseCode() == 404)
            hashsetSite404.add(logEntry.getPath());
        // статистика операционных систем
        if (logEntry.getUserAgent().getPlatform() != null && !logEntry.getUserAgent().getPlatform().isEmpty()) {
            if (hashmapOperSys.containsKey(logEntry.getUserAgent().getPlatform()))
                hashmapOperSys.put(logEntry.getUserAgent().getPlatform(), hashmapOperSys.get(logEntry.getUserAgent().getPlatform())+1);
            else
                hashmapOperSys.put(logEntry.getUserAgent().getPlatform(), 1);
        }
        // статистика бразузеров
        if (logEntry.getUserAgent().getBrowser() != null && !logEntry.getUserAgent().getBrowser().isEmpty()) {
            if (haыshmapBrowser.containsKey(logEntry.getUserAgent().getBrowser()))
                haыshmapBrowser.put(logEntry.getUserAgent().getBrowser(), haыshmapBrowser.get(logEntry.getUserAgent().getBrowser())+1);
            else
                haыshmapBrowser.put(logEntry.getUserAgent().getBrowser(), 1);
        }
    }
    long getTrafficRate() {
        if (ChronoUnit.HOURS.between(minTime, maxTime) <= 0)
            return 0;
       // System.out.println("totalTraffic = " + totalTraffic + "; minTime = " + minTime + "; maxTime = " + maxTime + "; hours = " + ChronoUnit.HOURS.between(minTime, maxTime));
        return totalTraffic/ChronoUnit.HOURS.between(minTime, maxTime);
    }

    // статистика по сайтам
    public Set<String> getSiteStat() {
        HashSet<String> set = new HashSet<String>();
        for (String s : hashsetSite) {
            set.add(s);
        }
        return set;
    }
    // статистика по несуществующим сайтам (ответ 404)
    public Set<String> getSiteStat404() {
        HashSet<String> set = new HashSet<String>();
        for (String s : hashsetSite404) {
            set.add(s);
        }
        return set;
    }
    // статистика операционных систем
    public HashMap<String, Double> getOperSys() {
        HashMap<String, Double> map = new HashMap<>();
        double sumOperSys = 0.0d;
        for (double d : hashmapOperSys.values()) {
            sumOperSys += d;
        }
        for(Map.Entry<String, Integer> entry : hashmapOperSys.entrySet()) {
            map.put(entry.getKey(), (double)entry.getValue()/sumOperSys);
        }
        return map;
    }
    // статистика браузеров
    public HashMap<String, Double> getOperBrowser() {
        HashMap<String, Double> map = new HashMap<>();
        double sumBrowser = 0.0d;
        for (double d : haыshmapBrowser.values()) {
            sumBrowser += d;
        }
        for(Map.Entry<String, Integer> entry : haыshmapBrowser.entrySet()) {
            map.put(entry.getKey(), (double)entry.getValue()/sumBrowser);
        }
        return map;
    }
}
