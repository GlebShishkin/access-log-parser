import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        List<Stroka> list = new ArrayList<Stroka>();

        System.out.println("Укажите путь к файлу:");
        String path = new Scanner(System.in).nextLine();
        File file = new File(path);
        boolean fileExists = file.exists();
        boolean isDirectory = file.isDirectory();
        int cntYandexBot = 0;
        int cntGoogleBot = 0;

        if (fileExists && !isDirectory) {
            System.out.println("Путь указан верно");
        } else if (!fileExists) {
            System.out.println("файл не существует");
            return;
        } else if (isDirectory) {
            System.out.println("указанный путь является путём к папке");
            return;
        }

        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader reader =
                    new BufferedReader(fileReader);
            String line;
            int ln = 0;
            while ((line = reader.readLine()) != null) {
                ln++;
                if (1024 < line.length())
                    throw new IllegalLength("Неверная длина строки (строка " + ln);
                Stroka stroka = new Stroka(line);
                stroka.parse();
                list.add(stroka);
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

        for (Stroka s: list) {
            String[] parts = s.UserAgent.split(";");
            if (parts.length >= 2) {
                String fragment = parts[1].replaceAll("\\s","");
                //
                Pattern pattern = Pattern.compile("YANDEXBOT|GOOGLEBOT");
                Matcher matcher1 = pattern.matcher(fragment.toUpperCase());
                while(matcher1.find()) {
                    if (matcher1.group().equals("YANDEXBOT")) {
                        cntYandexBot++;
                    } else if (matcher1.group().equals("GOOGLEBOT")) {
                        cntGoogleBot++;
                    }
                }
            }
        }
        System.out.println("количество строк в файле = " + list.size() + "; GoogleBot = " + cntGoogleBot + "; YandexBot = " + cntYandexBot);
        System.out.println("Доля GoogleBot = " + (double)cntGoogleBot/(double)list.size() + "; доля YandexBot = " + (double)cntYandexBot/(double)list.size());
    }
}
