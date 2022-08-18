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
			bufImage = robot.createScreenCapture(area); // Robot 클래스를 이용하여 스크린 캡쳐.

			// this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufImage;
	}

	public static void main(String[] args) {

		String serverIp = "127.0.0.1";
		Socket socket = null;

		try {
			// 서버 연결
			socket = new Socket(serverIp, 1313);
			System.out.println("서버에 연결되었습니다.");

			// 파일전송

			String filePath = "C:/Users/samsung010/Desktop/qweqweqwe/";
			String fileNm = "capture.png";
			File file = new File(filePath + fileNm);
			BufferedImage img = null;
			int cnt = 0;
			FileSender fs = new FileSender(socket, filePath, fileNm);
			while (cnt < 100) {
				try {
					Thread.sleep(100);
					img = capture();
					if (img != null) {
						System.out.println(img.getWidth());
					}
					ImageIO.write(img, "png", file);

					System.out.println("이미지 저장 완료");
					// String fileNm = "정보보호기사.hwp";
					fs.run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cnt++;
			}
			// 메세지 전송
			/*
			 * String msg = "미안해 화내서! 사랑해"; MsgSender ms = new MsgSender(socket, msg);
			 * ms.start();
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//파일 전송용 클래스
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
			// 데이터 전송용 스트림 생성
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// @Override
	public void run() {

		try {

			// 파일전송을 서버에 알린다.('file' 구분자 전송)
			dos.writeUTF("file");
			dos.flush();

			// 전송할 파일을 읽어서 Socket Server에 전송
			String result = fileRead(dos);
			System.out.println("result : " + result);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		try {
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	}

	private String fileRead(DataOutputStream dos) {

		String result;

		try {
			System.out.println("파일 전송 작업을 시작합니다.");

			dos.writeUTF(fileNm);
			System.out.println("파일 이름(" + fileNm + ")을 전송하였습니다.");

			// 파일을 읽어서 서버에 전송
			File file = new File(filePath + "/" + fileNm);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);

			int len;
			int size = 4096;
			byte[] data = new byte[size];
			while ((len = bis.read(data)) != -1) {
				dos.write(data, 0, len);
			}

			// 서버에 전송
			dos.flush();

			/*
			 * -- 먹통된다. DataInputStream dis = new DataInputStream(socket.getInputStream());
			 * result = dis.readUTF(); if( result.equals("SUCCESS") ){
			 * System.out.println("파일 전송 작업을 완료하였습니다."); System.out.println("보낸 파일의 사이즈 : "
			 * + file.length()); }else{ System.out.println("파일 전송 실패!."); }
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

//메세지 전송용 클래스
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
			// 데이터 전송용 스트림 생성
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// @Override
	public void run() {

		try {
			// 파일전송 구분자 전송('msg' 전송)
			dos.writeUTF("msg");
			dos.flush();

			dos.writeUTF(msg);
			dos.flush();

			System.out.println("[" + msg + "] 전송");

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