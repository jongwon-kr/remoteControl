package remoteConnect;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JFrame;

import remoteConnect.RobotServer.Connection;

public class TestServer {

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int SCREEN_WIDTH = screenSize.width; // 화면 가로 너비
	final int SCREEN_HEIGHT = screenSize.height; // 화면 세로 너비
	final int server_port_number = 4444; // 서버 포트 넘버
	Socket socket; // socket
	ServerSocket server_socket; // server socket
	Vector v_client_list; // 클라이언트 관리 리스트
	static Image img = null; // 전송하는 화면

	public TestServer() {
		// 클라이언트 수
		v_client_list = new Vector();

		// client가 network 상태인지 확인하기 위해 일정 간격으로 신호를 보내기 위한 timer 생성
		Timer timer = new Timer();

		// 2초마다 Check_client 객체 생성
		timer.schedule(new Check_client(), 0, 2 * 1000);
		try {
			// 포트번호 12167에 SocketServer생성
			server_socket = new ServerSocket(server_port_number);
			while (true) {
				try {
					socket = server_socket.accept();
					Connection c = new Connection(socket, this);
					addClient(c);
					// Connection class 가 가지고 있는 run메서드를 실행시켜 client와의 통신을 유지
					c.start();
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {

		}
	}

	// add client
	public void addClient(Connection c) {
		v_client_list.addElement(c);
	}

	public void capture() {
		Robot robot;
		try {
			robot = new Robot();
			Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			img = robot.createScreenCapture(area); // Robot 클래스를 이용하여 스크린 캡쳐.
			// this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		TestServer robot_server;

		// client에 들어온 메시지를 받기위해
		BufferedReader in;

		// client에게 메시지를 보내기위해
		ObjectOutputStream oos; // 화면 전송 스트림

		// Connection 생성자
		public Connection(Socket s, TestServer t) {
			this.socket = s;
			this.robot_server = t;

			try {

				// inputStream 생성
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// outputStream 생성
				oos = new ObjectOutputStream(socket.getOutputStream());
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
					if (msg != null) {
						if (msg.startsWith("#share#")) {
							capture();
							if (img != null) {
								oos.writeObject(img);
								System.out.println("보냄");
							} else {
								System.out.println("못보냄");
							}
						}
					} else {
						System.out.println("no msg");
					}
				} catch (Exception e) {

				}
			}
		}// run

		// closeSocket
		public void closeSocket() { // 사용된 io와 socket 닫기
			try {
				in.close();
				oos.close();
				socket.close();
			} catch (Exception e) {

			} // try-catch
		}
	} // end Connection Class

	class Check_client extends TimerTask { // TimerTask에서 상속 받으면 run()메서드 override해야됨
		public void run() {
			Image img = null;
			int client_size = v_client_list.size();

			System.out.println("*****************************");
			System.out.println("**      상태 check         **");
			System.out.println("*****************************");
			System.out.println("**    # of clients : " + client_size + "     **");
			System.out.println("*****************************");

			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			for (int i = 0; i < client_size; i++) {
				try {
					capture();
					img = TestServer.img;
					if (img != null) {
						System.out.println("이미지 있음");
					} else {
						System.out.println("이미지 없음");
					}
					((Connection) v_client_list.elementAt(i)).oos.writeObject(img);
				} catch (Exception e) {
					v_client_list.removeElementAt(i);
					// 통신이 되지 않을 경우 목록에서 삭제
				} // try-catch
			} // for
		} // run

	} // Check_client Class

	// Main
	public static void main(String[] args) {
		new TestServer();
	}
}