package edu.seg2105.edu.server.backend;

import java.io.*;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

public class ServerConsole implements ChatIF {

    final public static int DEFAULT_PORT = 5555;

    private EchoServer server;
    private BufferedReader fromConsole;

    public ServerConsole(int port) {
        server = new EchoServer(port, this);
        fromConsole = new BufferedReader(new InputStreamReader(System.in));
    }

    public void accept() {
        try {
            String message;
            while (true) {
                message = fromConsole.readLine();
                handleCommand(message);
            }
        } catch (Exception e) {
            System.out.println("Unexpected error while reading from console!");
        }
    }

    private void handleCommand(String command) {
        if (command == null || command.trim().isEmpty())
            return;

        if (!command.startsWith("#")) {
            server.sendToAllClients("SERVER MSG> " + command);
            display("SERVER MSG> " + command);
            return;
        }

        String[] tokens = command.split(" ");
        String cmd = tokens[0].toLowerCase();

        try {
            switch (cmd) {
                case "#start":
                    if (server.isListening()) {
                        display("Server is already running.");
                    } else {
                        server.listen();
                        display("Server started listening for clients.");
                    }
                    break;

                case "#stop":
                    server.stopListening();
                    display("Server stopped listening for new clients.");
                    break;

                case "#close":
                    server.close();
                    display("Server closed and all clients disconnected.");
                    break;

                case "#setport":
                    if (server.isListening()) {
                        display("Cannot change port while server is running.");
                    } else if (tokens.length > 1) {
                        int newPort = Integer.parseInt(tokens[1]);
                        server.setPort(newPort);
                        display("Port set to " + newPort);
                    } else {
                        display("Usage: #setport <port>");
                    }
                    break;

                case "#getport":
                    display("Current port: " + server.getPort());
                    break;

                case "#quit":
                    display("Server shutting down...");
                    System.exit(0);
                    break;

                default:
                    display("Unknown command: " + cmd);
            }
        } catch (Exception e) {
            display("Error processing command: " + e.getMessage());
        }
    }

    public void display(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        try {
            port = Integer.parseInt(args[0]);
        } catch (Throwable t) {
        }

        ServerConsole console = new ServerConsole(port);
        console.display("Server console started on port " + port);
        console.accept();
    }
}
