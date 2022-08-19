package remoteConnect;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ReceiveServer extends JFrame {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int SCREEN_WIDTH = screenSize.width; // 화면 가로 너비
	final int SCREEN_HEIGHT = screenSize.height; // 화면 세로 너비
	File file = new File("C:/Users/Jongwon.JONG-PC/Desktop/receive/capture.png");
	static Image img = null;

	public ReceiveServer() {
		super("receiveServer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setVisible(true);
	}

	public void capture() {
		BufferedImage bufImage = null;
		try {
			System.out.println("캡쳐");
			int w = this.getWidth();
			int h = this.getHeight();
			System.out.println(w + ", " + h);
			this.img = (BufferedImage) ImageIO.read(file).getScaledInstance(w, h - 20, Image.SCALE_DEFAULT);
			// this.repaint();
			this.drawImage(img, w, h);
			System.out.println(img.getHeight(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawImage(Image img, int x, int y) {
		Graphics g = this.getGraphics();
		g.drawImage(img, 0, 0, x, y, this);
		this.paint(g);
		this.repaint();
	}

	public void paint(Graphics g) {
		if (RobotClient.img != null) {
			g.drawImage(RobotClient.img, 0, 0, RobotClient.img.getWidth(this), RobotClient.img.getHeight(this), this);
		}
	}

	public static void main(String[] args) {
		ReceiveServer rs = new ReceiveServer();
		ServerSocket serverSocket = null;
		Socket socket = null;

		try {

			serverSocket = new ServerSocket(1313);
			System.out.println("서버가 시작되었습니다.");

			// 클라이언트와의 연결 대기 루프
			while (true) {

				System.out.println("새로운 Client의 연결요청을 기다립니다.");

				// 연결되면 통신용 소켓 생성
				socket = serverSocket.accept();
				System.out.println("클라이언트와 연결되었습니다.");

				// 파일 수신 작업 시작
				Receiver receiver = new Receiver(socket);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		UpdateScreen us = new UpdateScreen();
		us.run();
	}

	static class UpdateScreen extends Thread {
		ReceiveServer rs = new ReceiveServer();

		public void run() {
			while (true) {
				try {
					if (img != null) {
						System.out.println("이미지 없음");
					}
					Thread.sleep(30);
					rs.capture();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

class Receiver extends Thread {
	ReceiveServer rs;
	Socket socket;
	DataInputStream dis = null;
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	BufferedImage bufimg = null;
	int w, h;

	public Receiver(Socket socket) {
		this.socket = socket;
	}

	// @Override
	public void run() {

		try {

			dis = new DataInputStream(socket.getInputStream());
			String type = dis.readUTF();

			/* type값('file'또는 'msg')을 기준으로 파일이 전송됐는지 문자열이 전송됐는지 구분한다. */
			if (type.equals("file")) {

				// 전송된 파일 쓰기!
				String result = fileWrite(dis);
				System.out.println("result : " + result);

			} else if (type.equals("msg")) {

				// 수신된 메세지 쓰기
				String result = getMsg(dis);
				System.out.println("result : " + result);
			}

			// 클라이언트에 결과 전송 - 먹통이된다.
			// DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			// dos.writeUTF(result);

		} catch (IOException e) {
			System.out.println("run() Fail!");
			e.printStackTrace();
		}
	}

	public String fileWrite(DataInputStream dis) {
		Image img = null;
		String result;
		String filePath = "C:/Users/Jongwon.JONG-PC/Desktop/receive/";

		try {
			System.out.println("파일 수신 작업을 시작합니다.");

			// 파일명을 전송 받고 파일명 수정
			String fileNm = dis.readUTF();
			System.out.println("파일명 " + fileNm + "을 전송받았습니다.");

			// 파일을 생성하고 파일에 대한 출력 스트림 생성
			File file = new File(filePath + "/" + fileNm);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			System.out.println(fileNm + "파일을 생성하였습니다.");

			// 바이트 데이터를 전송받으면서 기록
			int len;
			int size = 4096;
			byte[] data = new byte[size];
			while ((len = dis.read(data)) != -1) {
				bos.write(data, 0, len);
			}

			// bos.flush();
			result = "SUCCESS";

			System.out.println("파일 수신 작업을 완료하였습니다.");
			System.out.println("받은 파일의 사이즈 : " + file.length());
		} catch (IOException e) {
			e.printStackTrace();
			result = "ERROR";
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private String getMsg(DataInputStream dis) {

		String result;

		try {
			System.out.println("파일 수신 작업을 시작합니다.");

			// 파일명을 전송 받고 파일명 수정
			String msg = dis.readUTF();
			System.out.println("msg : " + msg);

			result = "SUCCESS";

			System.out.println("메세지 수신 작업을 완료하였습니다.");
		} catch (IOException e) {
			e.printStackTrace();
			result = "ERROR";
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}