import java.net.*;
import java.util.*;
import java.io.*;

public class PeerClient {
    private static String myName;
    private static int myPort;
    private static DatagramSocket socket;
    private static InetAddress registryAddress;
    private static final int REGISTRY_PORT = 7777;

    // Stores peer info: name -> "address:port"
    private static Map<String, String> knownPeers = new HashMap<>();

    public static void main(String[] args) {
        try {
            myPort = 8000 + new Random().nextInt(1000);
            socket = new DatagramSocket(myPort);
            registryAddress = InetAddress.getByName("localhost");

            Scanner sc = new Scanner(System.in);
            System.out.print("Enter username: ");
            myName = sc.nextLine().trim();

            // Register with registry
            sendToRegistry("REGISTER:" + myName);
            System.out.println("Registered as '" + myName + "' on port " + myPort);

            // Start listener for incoming messages
            new MessageListener().start();

            // Handle user commands
            System.out.println("Commands:\nLIST\nLOOKUP:<peer>\nMSG:<peer>:<message>\nQUIT");
            while (true) {
                String input = sc.nextLine().trim();
                if (input.equalsIgnoreCase("QUIT")) break;

                if (input.equalsIgnoreCase("LIST")) {
                    sendToRegistry("LIST");
                } else if (input.startsWith("LOOKUP:")) {
                    sendToRegistry(input);
                } else if (input.startsWith("MSG:")) {
                    String[] parts = input.split(":", 3);
                    if (parts.length == 3) {
                        String peer = parts[1];
                        String msg = parts[2];
                        sendToPeer(peer, msg);
                    } else {
                        System.out.println("Usage: MSG:<peer>:<message>");
                    }
                } else {
                    System.out.println("Invalid command.");
                }
            }

            sc.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendToRegistry(String message) throws IOException {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, registryAddress, REGISTRY_PORT);
        socket.send(packet);
    }

    private static void sendToPeer(String peerName, String message) throws IOException {
        if (!knownPeers.containsKey(peerName)) {
            System.out.println("Unknown peer. Use LOOKUP " + peerName + " first.");
            return;
        }
        String[] parts = knownPeers.get(peerName).split(":");
        InetAddress addr = InetAddress.getByName(parts[0]);
        int port = Integer.parseInt(parts[1]);

        String msg = "MSG:" + myName + ":" + message;
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
        socket.send(packet);

        System.out.println("[Direct to " + peerName + "] " + message);
    }

    static class MessageListener extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength()).trim();

                    if (message.startsWith("PEER:")) {
                        // Response from registry (LOOKUP)
                        System.out.println("Registry Response: " + message);

                        // Format: PEER:<name>:<ip>:<port>
                        String[] parts = message.split(":");
                        if (parts.length == 4) {
                            String peerName = parts[1];
                            String ip = parts[2];
                            String port = parts[3];
                            knownPeers.put(peerName, ip + ":" + port);
                        }
                    } else if (message.startsWith("MSG:")) {
                        // Direct message from peer
                        String[] parts = message.split(":", 3);
                        if (parts.length == 3) {
                            String fromUser = parts[1];
                            String text = parts[2];
                            System.out.println("[Direct from " + fromUser + "] " + text);
                        }
                    } else {
                        System.out.println("Server/Peer says: " + message);
                    }

                    buffer = new byte[1024]; // reset buffer
                } catch (IOException e) {
                    System.err.println("Listen error: " + e.getMessage());
                }
            }
        }
    }
}
