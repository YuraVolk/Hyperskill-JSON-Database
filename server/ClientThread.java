package server;


import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientThread implements Runnable {
    class Args {
        private String type;
        private String key;
        private String value;
        private String filename;

        Args(String type, String key, String value, String filename) {
            this.type = type;
            this.key = key;
            this.value = value;
            this.filename = filename;
        }

        public String getCommand() {
            return type;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String getFilename() {
            return filename;
        }
    }

    private Socket socket;
    private ServerSocket server;
    private Database database;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientThread(Socket socket, ServerSocket server, Database database) {
        //System.out.println("client created");
        this.socket = socket;
        this.server = server;
        this.database = database;
        try {
            dataInputStream = new DataInputStream(this.socket.getInputStream());
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String msg = dataInputStream.readUTF();

            Gson gson = new Gson();
            Map<String, Object> map = new LinkedHashMap<>();
            map = gson.fromJson(msg, map.getClass());

            switch ((String) map.get("type")) {
                case "set":
                    dataOutputStream.writeUTF(database.set((String) map.get("key"),
                            (Map<String, Object>) map.get("value")));
                    break;
                case "get":
                    dataOutputStream.writeUTF(
                            database.get((List<String>) map.get("key")));
                case "delete":
                    break;
                case "exit":
                    dataOutputStream.writeUTF("{\"response\":\"OK\"}");
                    socket.close();
                    server.close();
                    break;
            }
            /*Args args = gson.fromJson(msg, Args.class);
            System.out.println(args.getCommand());
            switch (args.getCommand()) {
                case "set":
                    dataOutputStream.writeUTF(database.set(args.getKey(), String.join(" ", args.getValue())));
                    break;
                case "get":
                    dataOutputStream.writeUTF(database.get(args.getKey()));
                    break;
                case "delete":
                    dataOutputStream.writeUTF(database.delete(args.getKey()));
                    break;
                default:
                    dataOutputStream.writeUTF("{\"response\":\"OK\"}");
                    socket.close();
                    server.close();
                    break;
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
