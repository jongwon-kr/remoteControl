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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

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
	Socket socket_to_host, socket_to_host2;
	String host_address = "127.0.0.1";
	String s_local_address;
	// server와 통신할 port번호
	int port_to_host_number = 4444;
	int port_to_host_number2 = 5555;
	ObjectInputStream ois;
	static PrintWriter out;

	Thread t_connection;
	String connectKey;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int SCREEN_WIDTH = screenSize.width; // 화면 가로 너비
	final int SCREEN_HEIGHT = screenSize.height; // 화면 세로 너비
	static Image img = null;

	JPanel top_panel; // 상단 패널
	JButton connect; // 연결 버튼
	JButton makeShareKey; // 공유키 생성
	JTextField conTf; // 연결 textfield

	JPanel centerLeft_panel; // 중앙 패널
	JPanel centerRight_panel;
	JSplitPane centerSplitPane; // 중앙 splitpane
	JSplitPane mainSplitPane; // 메인화면 splitpane
	JDialog dialog;

	JMenuBar menubar;
	JMenu menu;
	JMenuItem server_ip;

	boolean connectCheck = false;

	public TestClient() {
		super("원격 연결");
		ConnectCreation();
		getContentPane().add(setUI()).setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(484, 363);
		setVisible(true);
	}

	public int ConnectCreation() {
		try {
			socket_to_host = new Socket(host_address, port_to_host_number);
			socket_to_host2 = new Socket(host_address, port_to_host_number2);

			out = new PrintWriter(socket_to_host.getOutputStream());
			ois = new ObjectInputStream(socket_to_host2.getInputStream());

			t_connection = new Thread(this);
			t_connection.start();
		} catch (UnknownHostException e) {
			Alert("경고", "알수없는 호스트입니다.");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			Alert("경고", "연결에 실패하였습니다.");
			return 0;
		} // try-catch
		connectCheck = true;
		return 1;
	}

	public void Alert(String alert_title, String alert_message) {
		// alert 메소드
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

	// Main화면 UI설정
	public Component setUI() {
		top_panel = new JPanel(new FlowLayout());
		centerLeft_panel = new JPanel();
		centerRight_panel = new JPanel();

		conTf = new JTextField();
		conTf.setPreferredSize(new Dimension(100, 30));

		// 연결 버튼
		connect = new JButton("연결");
		connect.setBackground(Color.white);
		connect.setFocusable(false);
		connect.setFont(new Font("Dialog", Font.BOLD, 12));
		connect.setPreferredSize(new Dimension(60, 30));
		connect.setActionCommand("connect");
		connect.addActionListener(this);

		// 공유키 생성 버튼
		makeShareKey = new JButton("공유키 생성");
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

	// 이미지 그리기
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
		try {
			while (true) {
				img = (Image) ois.readObject();
				if (img != null) {
					int w = this.getWidth();
					int h = this.getHeight();
					img = (Image) img.getScaledInstance(w, h, Image.SCALE_DEFAULT);
					drawImage(img, w, h);
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// run

	// 접속요청한 화면 받아오기
	class ReceiveScreen extends JFrame implements Runnable {
		boolean onScreen = false;

		public ReceiveScreen() {
			super("접속화면");
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
					if (image != null) { // image가 null이 아닌 경우
						int w = this.getWidth();
						int h = this.getHeight();
						img = (Image) image.getScaledInstance(w, h, Image.SCALE_DEFAULT);
						// this.repaint();
						this.drawImage(img, w, h);
					} else {
						out.println("#share#");
						System.out.println("이미지 못받음");
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
			Alert("접속 실패", "원격접속에 실패했습니다.");
		}
	}

	public static void main(String[] args) {
		TestClient tc = new TestClient();
		tc.run();
	}

}