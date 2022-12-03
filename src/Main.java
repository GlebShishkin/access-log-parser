import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
            int N = 0;

            while(true) {
                System.out.println("Укажите путь к файлу:");
                String path = new Scanner(System.in).nextLine();
                File file = new File(path);
                boolean fileExists = file.exists();
                boolean isDirectory = file.isDirectory();

                if (fileExists && !isDirectory) {
                    System.out.println("Путь указан верно");
                } else if (!fileExists) {
                    System.out.println("файл не существует");
                    continue;
                } else if (isDirectory) {
                    System.out.println("указанный путь является путём к папке");
                    continue;
                }
                System.out.println("Это файл номер " + ++N);
            }
         }
}
