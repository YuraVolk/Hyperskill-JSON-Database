package client;

public class Database {
    String[] array;

    public Database() {
        this.array = new String[100];
    }

    public void set(int index, String data) {
        if (index > 100) {
            System.out.println("ERROR");
        } else {
            this.array[index - 1] = data;
            System.out.println("OK");
        }
    }

    public String get(int index) {
        if (index < 0 || index > 100 || this.array[index - 1] == null) {
            return "ERROR";
        } else {
            return this.array[index - 1];
        }
    }

    public void delete(int index) {
        if (index < 0 || index > 100) {
            System.out.println("ERROR");
        } else {
            this.array[index - 1] = null;
            System.out.println("OK");
        }
    }
}