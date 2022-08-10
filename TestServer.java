package remoteConnect;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer extends Thread {
	final int server_port = 7777;
	ServerSocket ssock;
	Socket csock = null;

	ObjectOutputStream oos;

	public TestServer() {
		try {
			ssock = new ServerSocket(server_port);
			csock = ssock.accept();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				csock.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void run() {
		try {
			Image img = null;
			while ((csock = ssock.accept()) != null) {
				img = capture();
				oos = new ObjectOutputStream(csock.getOutputStream());
				oos.writeObject(img);
				oos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Image capture() {
		Robot robot;
		Image img = null;
		try {
			robot = new Robot();
			Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			img = robot.createScreenCapture(area); // Robot 클래스를 이용하여 스크린 캡쳐.
			// this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

	// Main
	public static void main(String[] args) {
		TestServer ts = new TestServer();
		ts.start();
	}
}