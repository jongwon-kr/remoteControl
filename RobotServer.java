package remoteConnect;

import java.awt.Image;
import java.io.BufferedReader;
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

import javax.swing.JFrame;

public class RobotServer extends JFrame implements Serializable {
	final int server_port = 2222;
	Socket client;
	Vector v_client_list;
	PrintWriter requestor;
	boolean connectionOn = false;

	public RobotServer() {
		// client 수 관리 vector
		v_client_list = new Vector();

		// client가 network 상태인지 확인하기 위해 일정 간격으로 신호를 보내기 위한 timer 생성
		Timer timer = new Timer();

		// 2초마다 Check_client 객체 생성
		timer.schedule(new Check_client(), 0, 2 * 1000);

		try {
			// 포트번호 12167에 SocketServer생성
			ServerSocket server_socket = new ServerSocket(server_port);
			while (true) {
				try {
					// 접속할 client를 관리할 Socket 객체 생성
					Socket server = server_socket.accept();

					ReceiveScreen rs = new ReceiveScreen(server);
					rs.start();

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
		RobotServer robot_server;

		// client에 들어온 메시지를 받기위해
		BufferedReader in;

		// client에게 메시지를 보내기위해
		PrintWriter out;

		// Connection 생성자
		public Connection(Socket s, RobotServer j) {
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
					System.out.println(msg);
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

	class ReceiveScreen extends Thread {
		// client와 통신을 위해 만들어진 socket 이것에서 io를 뽑아낸다.
		Socket socket;

		// 화면 전송을 위한 bufferedInputStream, outputStream
		ObjectInputStream ois;
		ObjectOutputStream oos;

		public ReceiveScreen(Socket socket) {
			socket = socket;
			try {
				// inputStream 생성
				ois = new ObjectInputStream(socket.getInputStream());

				// outputStream 생성
				oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (Exception e) {

			}
		}

		public void run() {
			Image image = null;
			while (true) {
				try {
					image = (Image) ois.readObject();
					if (image != null) { // 메시지가 null이 아닌 경우
						oos.writeObject(image);
					} else {
						break;
					}
				} catch (Exception e) {
				} // try-catch
			} // while
		}
	}

	class Check_client extends TimerTask { // TimerTask에서 상속 받으면 run()메서드 override해야됨
		public void run() {
			int client_size = v_client_list.size();

			System.out.println("*****************************");
			System.out.println("**      상태 check         **");
			System.out.println("*****************************");
			System.out.println("**    # of clients : " + client_size + "     **");
			System.out.println("*****************************");

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
	} // Check_client Class

	public static void main(String[] args) {
		new RobotServer();
	}

}