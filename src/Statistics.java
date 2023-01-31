import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private Set<String> hashsetSite;
    private Set<String> hashsetSite404;
    private HashMap<String, Integer> hashmapOperSys;
    private HashMap<String, Integer> haыshmapBrowser;
    private Set<String> hashsetIpAdr;   // набор IP адресов
    private long countUserRequest;
    private long countErrorRequest;
    private HashMap<LocalDateTime, Long> hashmapPickSec;
    private HashSet<String> hashsetDomain;
    private HashMap<String, Long> hashmapIpAdrCount;
    public Statistics() {
        totalTraffic = 0;
        hashsetSite = new HashSet<>();
        hashsetSite404 = new HashSet<>();
        hashmapOperSys = new HashMap<>();
        haыshmapBrowser = new HashMap<>();
        hashsetIpAdr    = new HashSet<>();
        hashmapPickSec   = new HashMap<>();
        hashsetDomain   = new HashSet<>();
        hashmapIpAdrCount = new HashMap<>();
        countUserRequest = 0;
        countErrorRequest = 0;
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
        // в расчёте должны участвовать только обращения к сайту через обычные браузеры (не боты).
        if (logEntry.getUserAgent().isBot() == false) {
            countUserRequest++; // количество посещений пользователем (не ботом)
            hashsetIpAdr.add(logEntry.getIpAdr());  // уникальный IP-адрес пользователя
            if (logEntry.getTime() != null) {
                // в качестве ключей должны быть отдельные секунды, а в качестве значений — количества посещений в эту секунду
                if (hashmapPickSec.containsKey(logEntry.getTime()))
                    hashmapPickSec.put(logEntry.getTime(), hashmapPickSec.get(logEntry.getTime()) + 1);
                else
                    hashmapPickSec.put(logEntry.getTime(), 1l);
            }
            // пользователем считается пользователь с одним и тем же IP-адресом, не являющийся ботом
            if (hashmapIpAdrCount.containsKey(logEntry.getIpAdr()))
                hashmapIpAdrCount.put(logEntry.getIpAdr(), hashmapIpAdrCount.get(logEntry.getIpAdr()) + 1);
            else
                hashmapIpAdrCount.put(logEntry.getIpAdr(), 1l);
        }
        // ошибочный код ответа (4xx или 5xx)
        if ((String.valueOf(logEntry.getResponseCode()).charAt(0) == '4') || (String.valueOf(logEntry.getResponseCode()).charAt(0) == '5'))
            countErrorRequest++;    // передана строка с информацией о запросе с ошибочным кодом ответа
        // собирайте домены для всех referer-ов в HashSet<String>
        if ((logEntry.getReferer() != "-") && (logEntry.getReferer() != null)) {
            try {
                String str = getDomainName(logEntry.getReferer());
                if (str != null)
                    hashsetDomain.add(str);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
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
    // Метод подсчёта среднего количества посещений сайта за час
    public long getCountUserRequestInHour() {
        if ((ChronoUnit.HOURS.between(minTime, maxTime)) == 0)
            return 0;
         return countUserRequest/ChronoUnit.HOURS.between(minTime, maxTime);
    }
    // Метод подсчёта среднего количества ошибочных запросов в час
    public long getCountErrorRequestInHour() {
        if ((ChronoUnit.HOURS.between(minTime, maxTime)) == 0)
            return 0;
        return countErrorRequest/ChronoUnit.HOURS.between(minTime, maxTime);
    }
    // Метод расчёта средней посещаемости одним пользователем
    public long getAverageVisit() {
        if (hashsetIpAdr.size() == 0)
            return 0;
         return countUserRequest/hashsetIpAdr.size();
    }
    // Метод расчёта пиковой посещаемости сайта (в секунду)
    public long getPickInSecond() {
        Optional<Map.Entry<LocalDateTime, Long>> maxEntry = hashmapPickSec.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue));
        if (maxEntry.isEmpty())
            return 0;
        else
            return maxEntry.get().getValue();
    }
    // получение доменного имени
    private static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        if (domain == null)
            return null;
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
    // список сайтов, со страниц которых есть ссылки на текущий сайт
    public Set<String> getSetDomainName() {
        HashSet<String> set = new HashSet<String>();
        hashsetDomain.forEach((String damainName) -> {
            set.add(damainName);
        });
        return set;
    }
    // взять максимальное количество из всех пользователей
    public long getMaxIpAdrCount() {
        return hashmapIpAdrCount.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue();
    }
}
