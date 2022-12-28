import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        int ln = 0;
        int maxLength = 0;
        int minLength = 0;

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

        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader reader =
                    new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                if (1024 < line.length())
                    throw new IllegalLength("Неверная длина строки");
                int length = line.length();
                if (maxLength < length)
                    maxLength = length;
                if ((minLength == 0) || (length < minLength))
                    minLength = length;
                ln++;
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

        System.out.println("общее количество строк в файле: " + ln);
        System.out.println("длина самой длинной строки в файле: " + maxLength);
        System.out.println("длина самой короткой строки в файле: " + minLength);
    }
}
