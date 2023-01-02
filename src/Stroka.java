import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stroka {

    String strLine;
    String ipAdress;
    String dummy;
    String strDate;
    String method;
    String UserAgent;

    final String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

    public Stroka(String strLine) {
        this.strLine = strLine;
    }

    public boolean parse() {
        // IP-адрес
        Matcher matcher = pattern.matcher(this.strLine);
        if (matcher.find()) {
          //  System.out.println(matcher.group());
            this.ipAdress = matcher.group();
        } else{
          //  System.out.println("0.0.0.0");
            this.ipAdress = "0.0.0.0";
        }
        // Два пропущенных свойства
        this.dummy = strLine.substring(matcher.end(), strLine.indexOf('['));
        this.strDate = strLine.substring(strLine.indexOf('[')+1, strLine.indexOf(']'));
        // метод GET|POST
        Pattern patternGP = Pattern.compile((char)34 + "GET|POST");
        Matcher matcher1 = patternGP.matcher(strLine);
        while(matcher1.find()) {
            this.method = matcher1.group().substring(1);
        }
        this.UserAgent = (0 < strLine.indexOf((char)34 + "Mozilla")) ? strLine.substring(strLine.indexOf((char)34 + "Mozilla")+1) : "";
        return true;
    }
/*
    SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date date = formatter.parse("22-09-2018 08:23:43 PM");
*/
}
