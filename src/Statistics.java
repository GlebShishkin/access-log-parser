import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
public class Statistics {
    private long totalTraffic;
    LocalDateTime minTime;
    LocalDateTime maxTime;
    public Statistics() {
        totalTraffic = 0;
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
    }
    long getTrafficRate() {
        if (ChronoUnit.HOURS.between(minTime, maxTime) <= 0)
            return 0;
       // System.out.println("totalTraffic = " + totalTraffic + "; minTime = " + minTime + "; maxTime = " + maxTime + "; hours = " + ChronoUnit.HOURS.between(minTime, maxTime));
        return totalTraffic/ChronoUnit.HOURS.between(minTime, maxTime);
    }
}
