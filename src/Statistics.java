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
    private HashMap<String, Integer> hashmapOperSys;
    public Statistics() {
        totalTraffic = 0;
        hashsetSite = new HashSet<>();
        hashmapOperSys = new HashMap<>();
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
        if (logEntry.getResponseCode() == 200) {
            hashsetSite.add(logEntry.getPath());
        }
        // статистику операционных систем
//        if (logEntry.getUserAgent().getPlatform() != null) {
        if (logEntry.getUserAgent().getPlatform() != null && !logEntry.getUserAgent().getPlatform().isEmpty()) {
            if (hashmapOperSys.containsKey(logEntry.getUserAgent().getPlatform()))
                hashmapOperSys.put(logEntry.getUserAgent().getPlatform(), hashmapOperSys.get(logEntry.getUserAgent().getPlatform())+1);
            else
                hashmapOperSys.put(logEntry.getUserAgent().getPlatform(), 1);
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
}
