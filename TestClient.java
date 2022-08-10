package remoteConnect;

import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TestClient {
	final int port_number = 7777;
	String host_address = "127.0.0.1";
	Socket socket = null;

	ObjectInputStream ois;

	public void connecCreate() {
		try {
			socket = new Socket(host_address, port_number);

			ois = new ObjectInputStream(socket.getInputStream());

		} catch (Exception e) {

		}
	}

	class receiveScreen extends Thread {
		Image img;
		ObjectInputStream ois;

		public receiveScreen(ObjectInputStream ois) {
			this.ois = ois;

		}

		public void run() {
			try {
				img = null;
				while (true) {
					img = (Image) ois.readObject(this);

					ois.close();
				}
			} catch (IOException e) {

			}
		}
	}

	public static void main(String[] args) {
	}
}