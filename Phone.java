package Test;

import java.awt.FlowLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Phone extends JFrame {
	public JLabel intro = new JLabel("Waiting for connect...");
	public JLabel time = new JLabel("0:00");
	public int time_cnt = 0;

	public Connect con = null;
	public Mic mic = null;
	public Speaker spk = null;

	public AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 11025, 16, 2, 4, 11025, false);

	public Phone() {
		this.setSize(300, 80);
		this.setTitle("Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.setLayout(new FlowLayout());
		this.setVisible(true);
		this.setAlwaysOnTop(true);

		this.add(intro);
		this.add(time);

		this.con = new Connect(this);
		this.mic = new Mic(this);
		this.spk = new Speaker(this);

		startCommunication();
	}

	public void startCommunication() {
		this.mic.start();
		this.spk.start();
	}

	public class Connect extends Thread {
		Socket socket = null;
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		byte[] temp = new byte[10000];

		Phone p = null;

		Connect(Phone p) {
			this.p = p;
			try {
				socket = new Socket("127.0.0.1", 9999);
				System.out.println("Connected!");
				input = new BufferedInputStream(socket.getInputStream());
				output = new BufferedOutputStream(socket.getOutputStream());

				p.intro.setText("Connected!");
				this.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while (true) {
				int t = p.time_cnt++;
				p.time.setText(t / 60 + ":" + t % 60);
				try {
					this.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public class Mic extends Thread {
		TargetDataLine targetLine = null;
		byte[] data = null;
		Phone p = null;

		public Mic(Phone p) {
			this.p = p;
			try {
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, p.format);
				targetLine = (TargetDataLine) AudioSystem.getLine(info);
				targetLine.open();
				// System.out.println(targetLine.getBufferSize());
				data = new byte[(int) (targetLine.getBufferSize() / 5512)];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				targetLine.open();
				targetLine.start();
				while (true) {
					System.out.println(data.length);
					targetLine.read(data, 0, data.length);
					p.con.output.write(data, 0, data.length);
					p.con.output.flush();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	public class Speaker extends Thread {
		SourceDataLine sourceLine = null;
		byte[] data = null;
		Phone p = null;

		Speaker(Phone p) {
			this.p = p;
			try {
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, p.format);
				sourceLine = (SourceDataLine) AudioSystem.getLine(info);
				sourceLine.open();
				data = new byte[(int) (sourceLine.getBufferSize() / 5512)];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				sourceLine.open();
				sourceLine.start();
				while (true) {
					p.con.input.read(data, 0, data.length);
					sourceLine.write(data, 0, data.length);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	public static void main(String[] args) {
		new Phone();
	}
}
