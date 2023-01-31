import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// import org.apache.commons.httpclient.methods.*;
enum HttpMethod { GET, POST }

public class LogEntry {
    final private String ipAdr;
    final private LocalDateTime time;
    final private HttpMethod method;
    final private String path;
    final private int responseCode;
    final private int responseSize;
    final private String referer;
    final private UserAgent userAgent;
    private final String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private Pattern ip_pattern = Pattern.compile(IPADDRESS_PATTERN);
    private Pattern patternGP = Pattern.compile((char)34 + "GET|POST"); // метод GET|POST
    static final private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z", Locale.ENGLISH);

    public LogEntry(String strLine) {
        int begIndex = 0;
        int endIndex = 0;
        // IP
        Matcher matcher = ip_pattern.matcher(strLine);
        if (matcher.find()) {
            this.ipAdr = matcher.group();
        } else {
            this.ipAdr = "0.0.0.0";
        }
        // time
        LocalDateTime tmpTime;
        try {
            tmpTime = DATE_FORMAT.parse(strLine.substring(strLine.indexOf('[')+1, strLine.indexOf(']'))).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        catch (Exception ex) {
            tmpTime = null;
            System.out.println("Ошибка в определении даты " + ex.getMessage());
        }
        time = tmpTime;
        // methode
        Matcher matcherHttpMethod = patternGP.matcher(strLine);
        HttpMethod tmpMethod = null;
        while(matcherHttpMethod.find()) {
            tmpMethod = (matcherHttpMethod.group().substring(1) == "GET") ? HttpMethod.GET : HttpMethod.POST;
            endIndex = matcherHttpMethod.end();
        }
        this.method = tmpMethod;
        // path
        begIndex = strLine.indexOf((char)47, endIndex);
        endIndex = strLine.indexOf((char)34, begIndex);
        this.path = strLine.substring(begIndex, endIndex);
        // response
        begIndex = strLine.indexOf((char)32, endIndex);
        endIndex = strLine.indexOf((char)32, (begIndex+1));
        this.responseCode = Integer.parseInt(strLine.substring(begIndex, endIndex).trim());
        begIndex = strLine.indexOf((char)32, endIndex);
        endIndex = strLine.indexOf((char)32, (begIndex+1));
        // date size
        int tmpDateSize;
        try {
            tmpDateSize = Integer.parseInt(strLine.substring(begIndex, strLine.indexOf((char)32, endIndex)).trim());
        }
        catch (NumberFormatException e) {
            tmpDateSize = 0;
        }
        this.responseSize = tmpDateSize;
        // referer
        begIndex = strLine.indexOf((char)34, endIndex)+1;
        endIndex = strLine.indexOf((char)34, begIndex);
        this.referer = strLine.substring(begIndex, endIndex);
        // userAgent
        begIndex = strLine.indexOf((char)34, endIndex + 2);
        if (begIndex < 0){
            userAgent = new UserAgent("");
        } else {
            userAgent = new UserAgent(strLine.substring(begIndex));
        }
    }
    public LocalDateTime getTime() { return time; }
    public String getIpAdr() { return ipAdr; }
    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public int getResponseSize() { return responseSize; }
    public UserAgent getUserAgent() { return userAgent; }
    public String getReferer() { return referer; }
    @Override
    public String toString() {
        return "LogEntry{" +
                "ipAdr='" + ipAdr + '\'' +
                ", time=" + time +
                ", method=" + method +
                ", path='" + path + '\'' +
                ", responseCode=" + responseCode +
                ", responseSize=" + responseSize +
                ", referer='" + referer + '\'' +
                ", userAgent.Platform=" + userAgent.getPlatform() +
                ", userAgent.Browser=" + userAgent.getBrowser() +
                '}';
    }
}
