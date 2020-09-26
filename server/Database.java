package server;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    int size;
    String[] database;
    private Map<String, String> db = new LinkedHashMap<>();
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

    String set(String pos, String text) {
        db.put(pos, text);
        saveDatabase();
        return processor.getSuccess();
    }

    String get(String pos) {
        if (!db.containsKey(pos)) {
            return processor.getError(ERROR);
        }

        return processor.getValue(db.get(pos));
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