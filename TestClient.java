package remoteConnect;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TestClient extends JFrame implements ActionListener, Runnable {
	Robot r;
	ServerSocket server_socket;
	Socket socket_to_host;
	String host_address = "127.0.0.1";
	String s_local_address;
	// server�� ����� port��ȣ
	int port_to_host_number = 11332;

	ObjectInputStream ois = null;
	static PrintWriter out = null;
	String connectKey;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int SCREEN_WIDTH = screenSize.width; // ȭ�� ���� �ʺ�
	final int SCREEN_HEIGHT = screenSize.height; // ȭ�� ���� �ʺ�
	static Image img = null;

	JPanel top_panel; // ��� �г�
	JButton connect; // ���� ��ư
	JButton makeShareKey; // ����Ű ����
	JTextField conTf; // ���� textfield

	JPanel centerLeft_panel; // �߾� �г�
	JPanel centerRight_panel;
	JSplitPane centerSplitPane; // �߾� splitpane
	JSplitPane mainSplitPane; // ����ȭ�� splitpane
	JDialog dialog;

	JMenuBar menubar;
	JMenu menu;
	JMenuItem server_ip;

	boolean connectCheck = false;

	public TestClient() {
		super("���� ����");
		ConnectCreation();
		getContentPane().add(setUI()).setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(484, 363);
		setVisible(true);
	}

	public int ConnectCreation() {
		try {
			socket_to_host = new Socket(host_address, port_to_host_number);

		} catch (UnknownHostException e) {
			Alert("���", "�˼����� ȣ��Ʈ�Դϴ�.");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			Alert("���", "���ῡ �����Ͽ����ϴ�.");
			return 0;

		} // try-catch
		connectCheck = true;
		System.out.println("����");
		return 1;
	}

	public void Alert(String alert_title, String alert_message) {
		// alert �޼ҵ�
		dialog = new JDialog(this, alert_title, true);
		JLabel lll = new JLabel(alert_message);
		lll.setVerticalTextPosition(SwingConstants.CENTER);
		lll.setHorizontalTextPosition(SwingConstants.CENTER);
		JPanel ttt = new JPanel();
		ttt.add(lll);
		dialog.setLocation(180, 80);
		dialog.setSize(320, 100);
		dialog.setContentPane(ttt);
		dialog.show();
	}

	// Mainȭ�� UI����
	public Component setUI() {
		top_panel = new JPanel(new FlowLayout());
		centerLeft_panel = new JPanel();
		centerRight_panel = new JPanel();

		conTf = new JTextField();
		conTf.setPreferredSize(new Dimension(100, 30));

		// ���� ��ư
		connect = new JButton("����");
		connect.setBackground(Color.white);
		connect.setFocusable(false);
		connect.setFont(new Font("Dialog", Font.BOLD, 12));
		connect.setPreferredSize(new Dimension(60, 30));
		connect.setActionCommand("connect");
		connect.addActionListener(this);

		// ����Ű ���� ��ư
		makeShareKey = new JButton("����Ű ����");
		makeShareKey.setBackground(Color.white);
		makeShareKey.setFocusable(false);
		makeShareKey.setFont(new Font("Dialog", Font.BOLD, 12));
		makeShareKey.setPreferredSize(new Dimension(120, 30));
		makeShareKey.setActionCommand("makeShareKey");
		makeShareKey.addActionListener(this);

		centerLeft_panel.setPreferredSize(new Dimension(100, 300));
		top_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		top_panel.add(conTf);
		top_panel.add(connect);
		top_panel.add(makeShareKey);
		centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerLeft_panel, centerRight_panel);
		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top_panel, centerSplitPane);
		return mainSplitPane;
	}

	// �̹��� �׸���
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

	public void run() {
		BufferedImage image = null;
		try {
			while (true) {
				if (ImageIO.read(ois) != null) {
					System.out.println("not null");
				}
				if (image != null) {
					int w = this.getWidth();
					int h = this.getHeight();
					image = (BufferedImage) image.getScaledInstance(w, h, Image.SCALE_DEFAULT);
					drawImage(image, w, h);
				} else {
					System.out.println("������");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// run

	// ���ӿ�û�� ȭ�� �޾ƿ���
	class ReceiveScreen extends JFrame implements Runnable {
		boolean onScreen = false;

		public ReceiveScreen() {
			super("����ȭ��");
			onScreen = true;
			setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
			setVisible(true);
		}

		public void run() {
			Image image = null;
			try {
				while (onScreen) {
					Thread.sleep(10);
					out.println("#share#");
					image = img;
					if (image != null) { // image�� null�� �ƴ� ���
						int w = this.getWidth();
						int h = this.getHeight();
						img = (Image) image.getScaledInstance(w, h, Image.SCALE_DEFAULT);
						// this.repaint();
						this.drawImage(img, w, h);
					} else {
						out.println("#share#");
						System.out.println("�̹��� ������");
					}
					if (!isVisible())
						onScreen = false;
				}
			} catch (Exception e) {

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
				g.drawImage(RobotClient.img, 0, 0, RobotClient.img.getWidth(this), RobotClient.img.getHeight(this),
						this);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals("connect")) {
			Thread t = new Thread(new ReceiveScreen());
			t.start();
		} else {
			Alert("���� ����", "�������ӿ� �����߽��ϴ�.");
		}
	}

	class SendMsg extends Thread {
		Socket socket;
		String msg;

		FileOutputStream fos;
		DataOutputStream dos;
		BufferedOutputStream bos;

		public SendMsg(Socket s, String msg) {
			this.socket = s;
			this.msg = msg;

			try {
				// ��Ʈ�� ����
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				dos.writeUTF("msg");
				dos.flush();

				dos.writeUTF(msg);
				dos.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class SendFile extends Thread {
		Socket socket;
		Image img;

		ObjectOutputStream oos;

		SendFile(Socket s, Image i) {
			this.socket = s;
			this.img = i;

			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				oos.writeObject(img);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		TestClient tc = new TestClient();
		tc.run();
	}

}