import java.net.*;
import java.util.*;

public class PeerRegistry {
    // name -> "address:port"
    private static Map<String, String> peers = new HashMap<>();

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(7777);
            byte[] buffer = new byte[1024];
            System.out.println("Peer Registry started on port 7777");
            System.out.println("Listening for REGISTER, LIST, LOOKUP commands...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                String message = new String(packet.getData(), 0, packet.getLength()).trim();

                String response = processMessage(message, clientAddress, clientPort);

                if (response != null) {
                    byte[] data = response.getBytes();
                    DatagramPacket reply = new DatagramPacket(data, data.length, clientAddress, clientPort);
                    socket.send(reply);
                }

                buffer = new byte[1024]; // reset buffer
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static synchronized String processMessage(String message, InetAddress address, int port) {
        if (message.startsWith("REGISTER:")) {
            String name = message.substring(9).trim();
            peers.put(name, address.getHostAddress() + ":" + port);
            System.out.println("Registered peer: " + name + " -> " + address.getHostAddress() + ":" + port);
            return "REGISTERED:" + name;
        }
        else if (message.equals("LIST")) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : peers.entrySet()) {
                String[] parts = entry.getValue().split(":");
                sb.append("PEER:").append(entry.getKey())
                        .append(":").append(parts[0])
                        .append(":").append(parts[1]).append("\n");
            }
            return sb.toString().trim();
        }
        else if (message.startsWith("LOOKUP:")) {
            String name = message.substring(7).trim();
            if (peers.containsKey(name)) {
                String[] parts = peers.get(name).split(":");
                return "PEER:" + name + ":" + parts[0] + ":" + parts[1];
            } else {
                return "NOT_FOUND:" + name;
            }
        }
        return "INVALID_COMMAND";
    }
}
