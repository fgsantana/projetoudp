package UDPSRL;

import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
	static Timer timer;
	static int tam;
	static int min = 0;
	static int[] janela;
	static int porcentagem;
	static DatagramSocket clientsocket;
	static InetAddress IPServer;
	static String x;
	static int count;


	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		IPServer = InetAddress.getByName("localhost");
		System.out.println("Insira o tamanho da janela: ");
		tam = in.nextInt();
		janela = new int[tam];
		System.out.println("Insira a probabilidade de descarte em forma de inteiro: ");
		porcentagem = in.nextInt();
		clientsocket = new DatagramSocket();
		count = 0;
		try {
			while (in.hasNext()) {
				x = in.next();
				switch (x) {
				case " ":
					in.next();
				case "close":
					clientsocket.close();
				default:
					if (decide(porcentagem) == 1) {
						Enviar(x);
						janela[count] = 2;
						count++;

					} else {
						new Client(5);
						System.out.println("enviei pela segunda vez o pkt de posicao: " + count);
					}
					if (count == tam) { // esse contador ainda n eh certo, tem que testar
						count = 0;
						for (int i = tam; i <= count; i++) {
							janela[i] = 0;
						}
					}
					int retorno = Receber(x);
					if (retorno > 0) {
						janela[retorno] = 2;
					}
					ajeitarJanela(retorno);

				}

			}
		} catch (ConnectException e) {

		}

	}

	public static void ajeitarJanela(int retorno) {
		if (retorno == min) {
			for (int i = min; i < min + tam; i++) {
				if (janela[i] == 2) {
					min++;
				} else {
					break;
				}
			}
		}
	}

	public static void Enviar(String x) throws IOException {
		try {
			byte[] snddata;
			if (count < 10) {
				x = x + " 0" + count;
			} else {
				x = x + " " + count;
			}
			snddata = x.getBytes();
			DatagramPacket sndpacket = new DatagramPacket(snddata, snddata.length, IPServer, 1111);
			clientsocket.send(sndpacket);
			janela[count] = 1;
		} catch (IOException e) {

		}

	}

	public static int Receber(String x) {
		try {
			byte[] rcvdata = new byte[count];
			DatagramPacket rcvpacket = new DatagramPacket(rcvdata, rcvdata.length);
			clientsocket.receive(rcvpacket);
			String retorno = new String(rcvpacket.getData());
			int ret = Integer.parseInt(retorno);
			ret -= 1;
			return ret;
		} catch (IOException e) {
			return -1;
		}
	}

	public Client(int ini) {
		timer = new Timer();
		timer.schedule(new RemindTask(), ini);
	}

	class RemindTask extends TimerTask {
		public void run() {
			if (janela[count] != 2) {
				try {
					Enviar(x);
				} catch (IOException e) {

				}
				System.out.println("PKT reenviado");
			}
			timer.cancel();
		}
	}

	private static int decide(int porcentagem) {
		Random gerador = new Random();
		if (gerador.nextInt(99) <= porcentagem - 1) {
			return 0;
		} else {
			return 1;
		}
	}

}
