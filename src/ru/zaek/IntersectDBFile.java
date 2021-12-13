package ru.zaek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IntersectDBFile extends IntersectDB {
    Path path;

    // 2 для размера и зарезервированные 6 байтов
    final static int HEADER_SIZE = 8;

    /**
     * Запуск программы:
     * IntersectDBFile - создание файла, программа запросит параметры в терминале
     * IntersectDBFile /path/to/db cmd x y [value]
     *
     * где cmd - название команды (set, get, inc, add)
     *
     */
    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            do {
                System.out.print("Укажите путь к файлу: ");
                Path path = Path.of(reader.readLine());
                if(!Files.exists(path)) {
                    System.out.print("Будет создан файл " + path + ". Продолжить? [Y/n]");
                    String response = reader.readLine();

                    if (response.matches("[YyДд]") || response.length() == 0) {
                        createFile(path, inputSize(reader));
                        return;
                    }
                } else {
                    System.out.print("Будет перезаписан существующий файл " + path + ". Продолжить? [N/y]");
                    String response = reader.readLine();
                    if (response.matches("[YyДд]")) {
                        createFile(path, inputSize(reader));
                        return;
                    }
                }
            } while (true);
        }

        IntersectDBFile intersectDBFile = new IntersectDBFile(Paths.get(args[0]));

        String cmd = args[1];

        short n1, n2, val;
        n1 = (short) Integer.parseInt(args[2]);
        n2 = (short)Integer.parseInt(args[3]);

        switch(cmd) {
            // intersectdb.jar "filename" set 3 5 10
            case "set":
                val = (short)Integer.parseInt(args[4]);
                intersectDBFile.set(n1, n2, val);
                break;
            // intersectdb.jar "filename" get 3 5
            case "get":
                System.out.println(intersectDBFile.get(n1, n2));
                return;
            // intersectdb.jar "filename" inc 3 5
            case "inc":
                intersectDBFile.inc(n1, n2);
                break;
            // intersectdb.jar "filename" add 3 5 12
            case "add":
                val = (short)Integer.parseInt(args[4]);
                intersectDBFile.set(n1, n2, (short) (intersectDBFile.get(n1, n2) + val));
                break;
            default:
                throw new Exception("Unknown command " + cmd);
        }

        intersectDBFile.save();
    }

    IntersectDBFile(Path _path) throws IOException {
        super();
        path = _path;
        this.load();
    }

    /**
     * Создаёт файл пустой базы данных
     */
    private static void createFile(Path path, short size) throws IOException {
        byte[] byteArray = new byte[((size + 1) * size) + 1];
        writeShort(byteArray, 0, size);
        Files.write(path, byteArray);
    }

    /**
     * Записывает значение типа short побайтно в массив arr на позиции 'pos' и 'pos+1'
     */
    private static void writeShort(byte[] arr, int pos, short size) {
        arr[pos] = (byte) ((size >> 8) & 0xff);
        arr[pos+1] = (byte)(size & 0xff);
    }

    /**
     * Считывает число типа short из массива байтов arr с позиции pos
     */
    private static short readShort(byte[] arr, int pos) {
        return (short) (((arr[pos] & 0xff) << 8) | (arr[pos + 1] & 0xff));
    }

    /**
     * Сохраняет текущую версию базы данных
     */
    public void save() throws IOException {
        byte[] byteArray = new byte[data.length * 2 + HEADER_SIZE];
        // Длина
        writeShort(byteArray, 0, this.size);

        for(int i = 0; i < data.length; i ++) {
            writeShort(byteArray, HEADER_SIZE + 2*i, data[i]);
        }

        Files.write(path, byteArray);
    }

    /**
     * Загружает данные из текущего файла
     */
    private void load() throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        this.initData(readShort(bytes, 0));

        for (int i = HEADER_SIZE, j = 0; i < bytes.length - 1; i += 2, j++) {
            data[j] = readShort(bytes, i);
        }
    }


    private static short inputSize(BufferedReader reader) throws IOException {
        int result = 0;
        while(result < 2 || result > 32767) {
            System.out.print("Введите размер базы данных (число от 2 до 32 767): ");
            String response = reader.readLine();
            try {
                result = Integer.parseInt(response.replace(" ", ""));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                result = 0;
            }
        }

        return (short)result;
    }
}
