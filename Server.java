package Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

	private ServerSocket server = null;
	private Manager manager = new Manager();

	public void start() {
		try {
			server = new ServerSocket(1234);
			System.out.println("서버가 활성화 되었습니다.");
			while (true) {
				Socket socket = server.accept();
				new Chat(socket).start();
				manager.add(socket);
				manager.sendClientInformation();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

	public class Chat extends Thread {
		private Socket socket;
		private BufferedReader reader;

		public Chat(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message;
				while ((message = reader.readLine()) != null) {
					System.out.println(message);
					manager.sendToAll(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					manager.remove(socket);
					if (reader != null)
						reader.close();
					if (socket != null)
						socket.close();
					System.out.println("클라이언트가 나갔습니다.");
					manager.sendClientInformation();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	class Manager extends Vector {
		void add(Socket socket) {
			super.add(socket);
		}

		void remove(Socket socket) {
			super.remove(socket);
		}

		synchronized void sendToAll(String message) {
			PrintWriter writer = null;
			Socket socket;

			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);
				try {
					writer = new PrintWriter(socket.getOutputStream(), true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (writer != null)
					writer.println(message);
			}
		}

		synchronized void sendClientInformation() {
			String information = "현재 채팅 인원: " + size();
			System.out.println(information);
			sendToAll(information);
		}
	}
}