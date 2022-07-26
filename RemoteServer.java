package remoteConnect;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class RemoteServer extends JFrame implements Serializable {
	final int server_port = 12561;
	Vector v_client_list;
	PrintWriter requestor;
	boolean connectionOn = false;

	public RemoteServer() {
		// client 수 관리
		v_client_list = new Vector();

		// client가 network 상태인지 확인하기 위해 일정 간격으로 신호를 보내기 위한 timer 생성
		Timer timer = new Timer();

		// 2초마다 Check_client 객체 생성
		timer.schedule(new Check_client(), 0, 2 * 1000);

		try {
			// 포트번호 12561에 SocketServer생성
			ServerSocket server_socket = new ServerSocket(server_port);
			while (true) {
				try {
					// 접속할 client를 관리할 Socket 객체 생성
					Socket server = server_socket.accept();

					// client가 독립적으로 io작업을 할 수 있도록 Connection class 생성
					Connection c = new Connection(server, this);
					// 새로 접속한 client 를 client목록에 추가
					addClient(c);
					// Connection class 가 가지고 있는 run메서드를 실행시켜 client와의 통신을 유지
					c.start();
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {
			System.err.println("sever : " + e);
		}
	}

	public void message(String msg) {
		// client 목록을 한 바퀴 돌면서 msg보냄
		for (int i = 0; i < v_client_list.size(); i++) {
			((Connection) v_client_list.elementAt(i)).sendMessage(msg);
		}
	}

	// addClient 메소드 : 새로운 client 추가
	public void addClient(Connection c) {
		v_client_list.addElement(c);
	}

	// setRequestor 메소드 : client가 보낸 메시지에 대해 바로 그 client에만 보내기 위한 outputSteram
	public synchronized void setRequestor(PrintWriter requestor) {
		// 검색을 요청한 client에게만 검색 결과를 보내기 위해
		this.requestor = requestor;
	}

	// removeClient 메소드 : network가 끊긴 client 삭제
	public void removeClient(Connection c) {
		// Check_client class가 client와 통신을 시도하다 실패하면 해당 socket을 닫는다.
		c.closeSocket();

		// client 목록에서 삭제
		boolean b = v_client_list.remove(c);
	}

	class Connection extends Thread {
		// client와 통신을 위해 만들어진 socket 이것에서 io를 뽑아낸다.
		Socket socket;
		// P2pServer의 class의 메서드를 사용하기 위해
		RemoteServer robot_server;

		// client에 들어온 메시지를 받기위해
		BufferedReader in;

		// client에게 메시지를 보내기위해
		PrintWriter out;

		// Connection 생성자
		public Connection(Socket s, RemoteServer j) {
			socket = s;
			robot_server = j;

			try {

				// inputStream 생성
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// outputStream 생성
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e) {

			}
		}

		public void run() {
			String msg = "";
			// client로부터 메시지가 들어오기를 계속 대기
			while (true) {
				try {
					// client로부터 메시지 한 줄 받기
					msg = in.readLine();
					if (msg != null) { // 메시지가 null이 아닌 경우
						if (msg.startsWith("#click#")) {
							message(msg);
						} else if (msg.startsWith("#press#")) {
							message(msg);
						} else if (msg.startsWith("#release#")) {
							message(msg);
						} else if (msg.startsWith("#drag#")) {
							message(msg);
						} else if (msg.startsWith("#move#")) {
							message(msg);
						} else if (msg.startsWith("#wheel#")) {
							message(msg);
						} else if (msg.startsWith("#keyPressed#")) {
							message(msg);
						} else if (msg.startsWith("#keyReleased#")) {
							message(msg);
						} else if (msg.startsWith("#connect#")) {
							message(msg);
						} else if (msg.startsWith("#shareKey#")) {
							message(msg);
						} else if (msg.startsWith("#share#")) {
							message(msg);
						} else if (msg.startsWith("#connectSuccess#")) {
							message(msg);
						} else if (msg.startsWith("#phone#")) {
							message(msg);
						} else if (msg.startsWith("#p2p#")) {
							message(msg);
						}
					} else {
						break;
					}
				} catch (Exception e) {
					robot_server.removeClient(this);
				} // try-catch
			} // while
		}// run

		public void sendMessage(String msg) {
			try {
				out.println(msg);
				// 자신의 client에게 메시지 보내기
			} catch (Exception e) {

			} // try-catch
		}// sendMessage

		// closeSocket
		public void closeSocket() { // 사용된 io와 socket 닫기
			try {
				in.close();
				out.close();
				socket.close();
			} catch (Exception e) {

			} // try-catch
		}
	} // end Connection Class

	class Check_client extends TimerTask {
		public void run() {
			int client_size = v_client_list.size();

			System.out.println("===============================");
			System.out.println("  현재 접속 클라이언트 수 : " + client_size);
			System.out.println("===============================");

			for (int i = 0; i < client_size; i++) {
				try {
					((Connection) v_client_list.elementAt(i)).sendMessage("#c#");
					// client목를 쭈욱 한 바퀴 돌면서 check 메시지 보낸다(#c#)
				} catch (Exception e) {
					v_client_list.removeElementAt(i);
					// 통신이 되지 않을 경우 목록에서 삭제
				} // try-catch
			} // for
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		} // run
	}

	public static void main(String[] args) {
		new RemoteServer();
	}

}