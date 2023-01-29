import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("Укажите путь к файлу:");
        String path = new Scanner(System.in).nextLine();
        File file = new File(path);
        boolean fileExists = file.exists();
        boolean isDirectory = file.isDirectory();

        if (fileExists && !isDirectory) {
            System.out.println("Путь указан верно");
        } else if (!fileExists) {
            System.out.println("файл не существует");
            return;
        } else if (isDirectory) {
            System.out.println("указанный путь является путём к папке");
            return;
        }

        Statistics statistics = new Statistics();
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader reader =
                    new BufferedReader(fileReader);
            String line;
            int ln = 0;
            while ((line = reader.readLine()) != null) {
                ln++;
                if (1024 < line.length())
                    throw new IllegalLength("Неверная длина строки (строка номер " + ln);
                LogEntry logEntry = new LogEntry(line);
                //System.out.println("Строка " + ln + "; " + logEntry.toString());
                // статистика
                statistics.addEntry(logEntry);  // фиксируем объем трафика + мин и макс значение времени
            }
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        } catch (IllegalLength e) {
            System.out.println(e.getMessage());
            return;
        }
//        System.out.println("totalTraffic = " + statistics.getTotalTraffic() + "; statistics.minTime = " + statistics.minTime + "; statistics.maxTime = " + statistics.maxTime);
        System.out.println("Обьем часового трафика: " + new DecimalFormat("###,###,###,###").format(statistics.getTrafficRate()));
        // статистикaа всех существующих страниц сайта
        System.out.println("Существующие страницы сайта:");
        HashSet<String> set = (HashSet<String>) statistics.getSiteStat();
        for (String s : set) {
            System.out.println(s);
        }
        // статистикaа всех несуществующих страниц сайта
        System.out.println("Несуществующие страницы сайта:");
        HashSet<String> set404 = (HashSet<String>) statistics.getSiteStat404();
        for (String s : set404) {
            System.out.println(s);
        }
        // статистика операционных систем в виде Map
        System.out.println("Статистика операционных систем:");
        HashMap<String, Double> map =  statistics.getOperSys();
        for(Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        // статистика браузеров в виде Map
        System.out.println("Статистика браузеров:");
        DecimalFormat df = new DecimalFormat("0.00000");
        HashMap<String, Double> mapb =  statistics.getOperBrowser();
        for(Map.Entry<String, Double> entry : mapb.entrySet()) {
            System.out.println(entry.getKey() + " = " + df.format(entry.getValue()));
        }
    }
 }
