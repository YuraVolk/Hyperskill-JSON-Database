package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static void main(String[] args) {
        new MainClass().main(args);
    }
}

class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "-t", description = "command")
    String command;

    @Parameter(names = "-k", description = "index in DB")
    String index;

    @Parameter(names = "-v", variableArity = true, description = "value")
    List<String> text = new ArrayList<>();
}

class MainClass {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile String content = "";
    private volatile String fileName = "";

    private class Reader implements Runnable {
        @Override
        public void run() {

        }
    }

    private String generateJSON(Args args) {
        Gson gson = new Gson();
        Map<String, String> json = new LinkedHashMap<>();
        json.put("type", args.command);
        if (!args.command.equals("exit")) {
            json.put("key", args.index);
        }
        if (args.command.equals("set")) {
            json.put("value", String.join("", args.text));
        }
        return gson.toJson(json);
    }

    void main(String[] args) {
        String address = "127.0.0.1";
        int port = 12345;
        System.out.println("Client started!");
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            String msg = String.join(" ", args);
            Args arguments = new Args();
            String[] testArguments = args.clone();

            JCommander.newBuilder()
                    .addObject(arguments)
                    .build()
                    .parse(testArguments);

            output.writeUTF(msg);
            System.out.println("Sent: " + generateJSON(arguments));
            String res = input.readUTF();
            System.out.println("Received: " + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}