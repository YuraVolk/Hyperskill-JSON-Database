package server;

import java.util.LinkedHashMap;
import java.util.Map;

public class Database {
    int size;
    String[] database;
    private Map<String, String> db = new LinkedHashMap<>();
    private JSONProcessor processor = new JSONProcessor();

    final String ERROR = "No such key";

    Database(int size) {
        this.size = size;
        database = new String[size];
        for (int i = 0; i < size; i++) {
            database[i] = "";
        }
    }

    String set(String pos, String text) {
        db.put(pos, text);
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
        return processor.getSuccess();
    }
}