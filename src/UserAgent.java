import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgent {
    final private String platform;
    final private String browser;
    final private Pattern platformPattern = Pattern.compile("Windows[\s;]|Mac[\s;]|Linux[\s;]");
    // из описания: https://developer.mozilla.org/en-US/docs/Web/HTTP/Browser_detection_using_the_user_agent
    // Firefox, Seamonkey, Chrome, Chromium, Safari, Opera 15+, Opera 12-, Internet Explorer 10-, Internet Explorer 11
    final private Pattern browserPattern = Pattern.compile("Firefox/|Seamonkey/|Chrome/|Chromium/|Safari/|OPR/|Opera/|MSIE\s|Trident/");
    public UserAgent(String strUserAgent) {

        try {
            strUserAgent = strUserAgent.substring(strUserAgent.indexOf("("));
        }
        catch (Exception ex) {
            this.browser = "";
            this.platform = "";
            return;
        }

        // тип операционной системы
        String tmpPlatform = "";
        Matcher matcher = platformPattern.matcher(strUserAgent);
        while(matcher.find()) {
           tmpPlatform = matcher.group().replaceAll("\s", "").replaceAll(";", "");
        };
        this.platform = tmpPlatform.equals("Mac") ? "macOS" : tmpPlatform;
        // тип браузера
        String tmpBrowser = "";
        matcher = browserPattern.matcher(strUserAgent);
        while(matcher.find()) {
            switch (matcher.group()) {
                case "Firefox/": tmpBrowser = tmpBrowser + "Firefox;"; break;
                case "Seamonkey/": tmpBrowser = tmpBrowser + "Seamonkey;"; break;
                case "Chrome/": tmpBrowser = tmpBrowser + "Chrome;"; break;
                case "Chromium/": tmpBrowser = tmpBrowser + "Chromium;"; break;
                case "Safari/": tmpBrowser = tmpBrowser + "Safari;"; break;
                case "OPR/": tmpBrowser = tmpBrowser + "Opera 15+;"; break;
                case "Opera/": tmpBrowser = tmpBrowser + "Opera 12-;"; break;
                case "MSIE\s": tmpBrowser = tmpBrowser + "Internet Explorer 10-;"; break;
                case "Trident/": tmpBrowser = tmpBrowser + "Internet Explorer 11;"; break;
            }
        };
        this.browser = tmpBrowser.replaceAll(";+$", "");
    }
    public String getPlatform() {
        return platform;
    }
    public String getBrowser() {
        return browser;
    }
}
