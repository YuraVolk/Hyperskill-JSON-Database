package server;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    int size;
    String[] database;
    private Map<String, Object> db = new LinkedHashMap<>();
    private JSONProcessor processor = new JSONProcessor();
    private final String filePath = "C:\\Users\\Yuriy Volkovskiy\\Desktop\\JSON Database\\JSON Database\\task\\src\\server\\data\\db.json";
    final String ERROR = "No such key";
    private ReadWriteLock lock;
    private Lock readLock;
    private Lock writeLock;

    Database() {
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();

        try {
            writeLock.lock();
            FileWriter writer = new FileWriter(filePath);
            writer.write("{}");
            writer.close();
            writeLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDatabase() {
        try {
            writeLock.lock();
            FileWriter writer = new FileWriter(filePath);
            writer.write(new Gson().toJson(db));
            writer.close();
            writeLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String set(String pos, Map<String, Object> value) {
        db.put(pos, value);
        saveDatabase();
        return processor.getSuccess();
    }

    String get(List<String> pos) {
        Map<String, Object> finalResult = db;
        String finalStringResult = null;
        for (String element : pos) {
            if (!finalResult.containsKey(element)) {
                return processor.getError(ERROR);
            } else {
                System.out.println("element: " + finalResult.get(element));
                if (finalResult.get(element) instanceof String) {
                    System.out.println("string: " + finalResult.get(element));
                    finalStringResult = (String) finalResult.get(element);
                } else {
                    System.out.println("result: " + finalResult.get(element));
                    finalResult = (Map<String, Object>) finalResult.get(element);
                }
            }
        }

        if (finalStringResult == null) {
            return processor.getValue(new Gson().toJson(finalResult));
        } else {
            return processor.getValue(finalStringResult);
        }


    }

    String delete(String pos) {
        if (!db.containsKey(pos))  {
            return processor.getError(ERROR);
        }

        db.remove(pos);
        saveDatabase();
        return processor.getSuccess();
    }
}