package battleship;

import java.io.*;
import java.net.*;


public class Udp {
    private static DatagramSocket socket = null;
    private static byte[] buffer = new byte[256];
    private static InetAddress address;
	public static String playerIP = "192.168.1.255";
	public static int udpPort = 6464;
	
	
	
	public String receive() throws SocketException, UnknownHostException {
		socket = new DatagramSocket(udpPort);
		DatagramPacket packet= new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(packet);
		}catch(IOException e) {
			e.printStackTrace();
		}
		//address = packet.getAddress();
		String received = new String(packet.getData(),0,packet.getLength());
		socket.close();
		return received;
	}
	public void send(String broadcastMessage) throws IOException {
		InetAddress address1 = InetAddress.getByName(playerIP);
		socket = new DatagramSocket();
	    socket.setBroadcast(true);
	    byte[] buffer = broadcastMessage.getBytes();
	    DatagramPacket packet 
		   = new DatagramPacket(buffer, buffer.length, address1, udpPort);
	    socket.send(packet);
	    socket.close();
			    
	}
}
