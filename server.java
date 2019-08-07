package UDPSRL;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Server {
	static DatagramSocket socket;

	public static void main(String[] args) throws IOException {
		int[] janela = new int[10];
		InetAddress clientip;
		byte[] rcvdata = new byte[1024];
		byte[] snddata;
		int port;
		socket = new DatagramSocket(1111);
		DatagramPacket rvpacket = new DatagramPacket(rcvdata, rcvdata.length);
		while (true) {
			String x;
			socket.receive(rvpacket);
			x = new String(rvpacket.getData()).trim();
			String posi = x.substring(x.length() - 2, x.length());
			System.out.println(x + " recebido na posição: " + posi);
			clientip = rvpacket.getAddress();
			port = rvpacket.getPort();
			snddata = posi.getBytes();
			DatagramPacket sndpacket = new DatagramPacket(snddata, snddata.length, clientip, port);
			socket.send(sndpacket);
		}

	}

}
