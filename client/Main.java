package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static void main(String[] args) {
        new MainClass().main(args);
    }
}

class Args {
    @Parameter(names = {"--type", "-t"})
    String command;
    @Parameter(names = {"--key", "-k"})
    String key;
    @Parameter(names = {"--value", "-v"})
    String value;
    @Parameter(names = {"--input", "-in"})
    String filename;
}

class MainClass {
    void main(String[] args) {
        String address = "127.0.0.1";
        int port = 12345;
        System.out.println("Client started!");
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            String[] testArguments;
            if (args[0].equals("Main")) {
                testArguments = Arrays.copyOfRange(args, 1, args.length);
            } else {
                testArguments = args.clone();
            }

            Args arguments = new Args();
            JCommander.newBuilder()
                    .addObject(arguments)
                    .build()
                    .parse(testArguments);

            String result = "";
            if (arguments.filename == null) {
                Map<String, String> json = new LinkedHashMap<>();
                json.put("type", arguments.command);
                if (arguments.key != null) {
                    json.put("key", arguments.key);
                }
                if (arguments.value != null) {
                    json.put("value", arguments.value);
                }
                result = new Gson().toJson(json);
            } else {
                Path filepath = Paths.get("C:\\Users\\Yuriy Volkovskiy\\Desktop\\JSON Database\\" +
                        "JSON Database\\task\\src\\client\\data\\" + arguments.filename);
                result = String.join("\n", Files.readAllLines(filepath));

            }

            System.out.println("Sent: " + result);
            output.writeUTF(result);

            String res = input.readUTF();
            System.out.println("Received: " + res);
        } catch (IOException e) {
            System.out.println("Error while reading file");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}