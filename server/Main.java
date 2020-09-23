package server;

import com.beust.jcommander.JCommander;
import jdk.jfr.DataAmount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class Main {
    public static void main(String[] args) {
        new MainClass().main();
    }
}

class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "-t", description = "command name")
    String command;

    @Parameter(names = "-k", description = "index in database (from 1 to 1000)")
    String index;

    @Parameter(names = "-v", variableArity = true, description = "text")
    List<String> text = new ArrayList<>();
}


class MainClass {
    void main() {
        String address = "127.0.0.1";
        int port = 12345;
        Database database = new Database(1000);
        System.out.println("Server started!");
        try (ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            while (true) {
                try (Socket socket = server.accept();
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                ) {
                    String msg = input.readUTF();
                    Args args = new Args();
                    JCommander.newBuilder()
                            .addObject(args)
                            .build()
                            .parse(msg.split(" "));

                    if (args.command.equals("exit")) {
                        output.writeUTF("{\"response\":\"OK\"}");
                        break;
                    }
                    switch (args.command) {
                        case "set":
                            output.writeUTF(database.set(args.index, String.join(" ", args.text)));
                            break;
                        case "get":
                            output.writeUTF(database.get(args.index));
                            break;
                        case "delete":
                            output.writeUTF(database.delete(args.index));
                            break;
                        default:
                            output.writeUTF("ERROR");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
