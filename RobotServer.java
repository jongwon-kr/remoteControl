package remoteConnect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class RobotServer {
	final int server_port = 12156;
	Socket client; // client
	Vector v_client_list; // client 목록
	PrintWriter requestor; // 요청

	public RobotServer() {
		v_client_list = new Vector();
		Timer timer = new Timer(); // timer
		timer.schedule(new Check_client(), 0, 2 * 1000);
		try {
			ServerSocket serverSocket = new ServerSocket(server_port);
			while (true) {
				try {
					// client를 관리할 ServerSocket 객체 생성
					Socket server = serverSocket.accept();

					// client의 io작업을 위한 Connection 객체 생성
					Connection c = new Connection(server, this);

					// client목록에 추가
					addClient(c);

					c.start();
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {
			System.err.println("sever : " + e);
		}
	}

	// message 전송
	public void messageAll(String msg) {
		// client목록의 모든 client에게 메시지 전송
		for (int i = 0; i < v_client_list.size(); i++) {
			((Connection) v_client_list.elementAt(i)).sendMessage(msg);
		}
	}

	// client목록에 추가
	public void addClient(Connection c) {
		v_client_list.addElement(c);
	}

	// setRequestor 메소드 : client가 보낸 메시지에 대해 바로 그 client에만 보내기 위한 outputSteram
	public synchronized void setRequestor(PrintWriter requestor) {
		// 검색을 요청한 client에게만 검색 결과를 보내기 위해
		this.requestor = requestor;
	}

	// client제거
	public void removeClient(Connection c) {
		c.closeSocket();
		boolean b = v_client_list.remove(c);
	}

	class Connection extends Thread {
		Socket socket;
		RobotServer robotServer;
		BufferedReader in;
		PrintWriter out;

		public Connection(Socket s, RobotServer j) {
			socket = s;
			robotServer = j;

			try {
				// inputStream
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// outputStream
				out = new PrintWriter(socket.getOutputStream());
			} catch (Exception e) {

			}
		}

		public void run() {
			String msg = "";
			while (true) {
				try {
					msg = in.readLine();
					if (msg != null) {
						// client 메시지 관련
					} else {
						break;
					}
				} catch (Exception e) {

				}
			}
		}

		// sendMessage
		public void sendMessage(String msg) {
			try {
				out.println(msg);
			} catch (Exception e) {

			}
		}

		// closeSocket
		public void closeSocket() { // 사용된 io와 socket 닫기
			try {
				in.close();
				out.close();
				socket.close();
			} catch (Exception e) {

			} // try-catch
		}
	}

	class Check_client extends TimerTask { // TimerTask에서 상속 받으면 run()메서드 override해야됨
		public void run() {
			int client_size = v_client_list.size();
			
			System.out.println("*****************************");
			System.out.println("*          상태 check        *");
			System.out.println("*           clients : " + client_size + "     *");
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
