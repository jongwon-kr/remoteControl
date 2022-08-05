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
import java.awt.event.InputEvent;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import remoteConnect.RobotClient.receiveScreen;
import remoteConnect.RobotClient.sendScreen;

public class TestClient extends JFrame implements ActionListener, Runnable {
	Robot r;
	ServerSocket server_socket;
	Socket socket_to_host;
	String host_address = "127.0.0.1";
	String s_local_address;
	// server와 통신할 port번호
	int port_to_host_numberServer = 55242;
	// client끼리 통신할 port번호

	ObjectInputStream ois = null;
	static PrintWriter out;

	Thread t_connection;
	String connectKey;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int SCREEN_WIDTH = screenSize.width; // 화면 가로 너비
	final int SCREEN_HEIGHT = screenSize.height; // 화면 세로 너비
	static Image img = null; // 생성자. UI 배치.

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// run

	public static void main(String[] args) {

	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Thread t, rt;
		if (command.equals("connect")) {
			out.println("SDfasdfasdf");
			connectKey = conTf.getText();
			out.println("#connect#" + connectKey);
			try {
				int cnt = 0;
				while (!connectCheck) {
					Thread.sleep(1000);
					System.out.println("서버에 접속중입니다.");
					cnt++;
					if (cnt == 6)
						break;
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (connectCheck) {
				Alert("접속 성공", "접속에 성공했습니다.");
				out.println("#share#");
				t = new Thread(new receiveScreen());
				t.start();
			} else {
				Alert("접속 실패", "원격접속에 실패했습니다.");
			}
		}
	}

}
