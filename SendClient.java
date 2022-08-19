package remoteConnect;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import javax.imageio.ImageIO;

public class SendClient {
	public static BufferedImage capture() {
		Robot robot;
		BufferedImage bufImage = null;
		try {
			robot = new Robot();
			Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			bufImage = robot.createScreenCapture(area); // Robot Ŭ������ �̿��Ͽ� ��ũ�� ĸ��.

			// this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufImage;
	}

	public static void main(String[] args) throws InterruptedException {

		String serverIp = "127.0.0.1";
		Socket socket = null;

		try {
			// ���� ����
			socket = new Socket(serverIp, 1313);
			System.out.println("������ ����Ǿ����ϴ�.");

			// ��������

			String filePath = "C:/Users/samsung010/Desktop/receive/";
			String fileNm = "capture.png";
			for (int i = 0; i < 1000; i++) {
				sendScreen(socket, filePath, fileNm);
			}
			// String fileNm = "������ȣ���.hwp";
			// �޼��� ����

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendScreen(Socket socket, String filePath, String fileNm) {
		BufferedImage img = null;
		File file = new File(filePath + fileNm);
		int cnt = 0;
		FileSender fs = new FileSender(socket, filePath, fileNm);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		img = capture();
		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("�̹��� ���� �Ϸ�");
		fs.run();
	}
}

//���� ���ۿ� Ŭ����
class FileSender extends Thread {

	String filePath;
	String fileNm;
	Socket socket;
	DataOutputStream dos;
	FileInputStream fis;
	BufferedInputStream bis;

	public FileSender(Socket socket, String filePath, String fileNm) {

		this.socket = socket;
		this.fileNm = fileNm;
		this.filePath = filePath;

		try {
			// ������ ���ۿ� ��Ʈ�� ����
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// @Override
	public void run() {

		try {

			// ���������� ������ �˸���.('file' ������ ����)
			dos.writeUTF("file");
			dos.flush();

			// ������ ������ �о Socket Server�� ����
			String result = fileRead(dos);
			System.out.println("result : " + result);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String fileRead(DataOutputStream dos) {

		String result;

		try {
			System.out.println("���� ���� �۾��� �����մϴ�.");

			dos.writeUTF(fileNm);
			System.out.println("���� �̸�(" + fileNm + ")�� �����Ͽ����ϴ�.");

			// ������ �о ������ ����
			File file = new File(filePath + "/" + fileNm);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);

			int len;
			int size = 4096;
			byte[] data = new byte[size];
			while ((len = bis.read(data)) != -1) {
				dos.write(data, 0, len);
			}

			// ������ ����
			dos.flush();

			/*
			 * -- ����ȴ�. DataInputStream dis = new DataInputStream(socket.getInputStream());
			 * result = dis.readUTF(); if( result.equals("SUCCESS") ){
			 * System.out.println("���� ���� �۾��� �Ϸ��Ͽ����ϴ�."); System.out.println("���� ������ ������ : "
			 * + file.length()); }else{ System.out.println("���� ���� ����!."); }
			 */

			result = "SUCCESS";
		} catch (IOException e) {
			e.printStackTrace();
			result = "ERROR";
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
}

//�޼��� ���ۿ� Ŭ����
class MsgSender extends Thread {

	Socket socket;
	String msg;
	DataOutputStream dos;
	FileInputStream fis;
	BufferedInputStream bis;

	public MsgSender(Socket socket, String msg) {

		this.socket = socket;
		this.msg = msg;

		try {
			// ������ ���ۿ� ��Ʈ�� ����
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// @Override
	public void run() {

		try {
			// �������� ������ ����('msg' ����)
			dos.writeUTF("msg");
			dos.flush();

			dos.writeUTF(msg);
			dos.flush();

			System.out.println("[" + msg + "] ����");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}