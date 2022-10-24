package remoteConnect;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class RobotClient extends JFrame implements ActionListener, Runnable, Serializable {
	Robot r;
	ServerSocket server_socket;
	Socket socket_to_host;
	String host_address = "192.168.35.250";
	String s_local_address;
	// server와 통신할 port번호
	int port_to_host_number = 2222;
	final int screen_port = 3333;
	// client끼리 통신할 port번호
	int p2p_port_number = 1055;
	// 공유 키
	int shareKey = 0;
	boolean connectCheck = false, connectOn = false;
	String nickName = "";

	Vector v_client_address;

	BufferedReader in = null;
	static PrintWriter out;

	// 화면 전송을 위한 bufferedInputStream, outputStream
	BufferedInputStream bin;
	BufferedOutputStream bout;

	Thread t_connection;
	String pathname, connectKey, connectName;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int SCREEN_WIDTH = screenSize.width; // 화면 가로 너비
	final int SCREEN_HEIGHT = screenSize.height; // 화면 세로 너비
	static Image img = null; // 생성자. UI 배치.
	int shareTime = 600;

	JDialog dialog;
	JPanel connect_panel; // 연결부 패널
	JPanel connect_panel_top; // 연결부 패널 상단
	JPanel connect_panel_bottom; // 연결부 패널 하단

	// 연결부 상단 패널
	JLabel top_label1;
	JLabel top_label2;
	JLabel top_label3;

	// 연결부 하단 패널
	JLabel bottom_label1;
	JTextField bottom_name_tf;
	JTextField bottom_code_tf;
	JButton connect_btn;

	// 로그인 부
	JLabel name_label;
	JTextField id_tf;
	JButton create_btn;

	JLabel con_state_label; // 연결 상태

	public RobotClient() {
		super("remote connect");
		ConnectCreation();
		getContentPane().setBackground(new Color(244, 250, 255));
		setBounds(100, 100, 645, 365);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		connect_panel = new JPanel();
		connect_panel.setBounds(250, 15, 360, 300);
		connect_panel.setBorder(new LineBorder(new Color(117, 186, 255), 1, true));
		add(connect_panel);
		connect_panel.setLayout(new GridLayout(0, 1, 0, 0));

		// top_panel
		connect_panel_top = new JPanel();
		connect_panel_top.setBackground(new Color(230, 242, 255));
		connect_panel_top.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(117, 186, 255)));
		connect_panel.add(connect_panel_top);
		connect_panel_top.setLayout(null);

		top_label1 = new JLabel("PC 원격코드");
		top_label1.setBounds(12, 26, 334, 30);
		connect_panel_top.add(top_label1);
		top_label1.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		top_label1.setHorizontalAlignment(SwingConstants.CENTER);
		top_label1.setBackground(new Color(255, 255, 255));

		top_label2 = new JLabel("이 코드를 다른 PC에 입력하면 원격 접속이 됩니다 ");
		top_label2.setBounds(22, 66, 324, 19);
		connect_panel_top.add(top_label2);
		top_label2.setHorizontalAlignment(SwingConstants.CENTER);
		top_label2.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

		top_label3 = new JLabel("코드를 생성하면 표시됩니다.");
		top_label3.setBounds(12, 95, 334, 30);
		connect_panel_top.add(top_label3);
		top_label3.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		top_label3.setHorizontalAlignment(SwingConstants.CENTER);

		// bottom panel
		connect_panel_bottom = new JPanel();
		connect_panel_bottom.setBackground(new Color(230, 242, 255));
		connect_panel.add(connect_panel_bottom);
		connect_panel_bottom.setLayout(null);

		bottom_label1 = new JLabel("접속하려는 PC의 닉네임과 원격 코드를 입력하세요.");
		bottom_label1.setBounds(12, 30, 334, 25);
		bottom_label1.setHorizontalAlignment(SwingConstants.CENTER);
		bottom_label1.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		connect_panel_bottom.add(bottom_label1);

		bottom_name_tf = new JTextField();
		bottom_name_tf.setBounds(69, 65, 165, 25);
		bottom_name_tf.setEnabled(false);
		connect_panel_bottom.add(bottom_name_tf);

		bottom_code_tf = new JTextField();
		bottom_code_tf.setBounds(69, 92, 165, 25);
		bottom_code_tf.setEnabled(false);
		connect_panel_bottom.add(bottom_code_tf);

		connect_btn = new JButton("connect");
		connect_btn.setBounds(251, 65, 95, 52);
		connect_btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		connect_panel_bottom.add(connect_btn);
		connect_btn.setBackground(Color.white);
		connect_btn.addActionListener(this);
		connect_btn.setActionCommand("connect");
		connect_btn.setFocusable(false);
		connect_btn.setEnabled(false);
		connect_panel_bottom.add(connect_btn);

		JLabel bottom_name_label = new JLabel("Name");
		bottom_name_label.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		bottom_name_label.setHorizontalAlignment(SwingConstants.RIGHT);
		bottom_name_label.setBounds(12, 65, 50, 25);
		connect_panel_bottom.add(bottom_name_label);

		JLabel bottom_code_label = new JLabel("Code");
		bottom_code_label.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		bottom_code_label.setHorizontalAlignment(SwingConstants.RIGHT);
		bottom_code_label.setBounds(12, 92, 50, 25);
		connect_panel_bottom.add(bottom_code_label);

		name_label = new JLabel("사용할 닉네임을 적어주세요.");
		name_label.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		name_label.setHorizontalAlignment(SwingConstants.CENTER);
		name_label.setBounds(22, 15, 200, 25);
		add(name_label);

		id_tf = new JTextField();
		id_tf.setBounds(32, 50, 180, 25);
		getContentPane().add(id_tf);

		create_btn = new JButton("Create");
		create_btn.setBackground(Color.white);
		create_btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		create_btn.addActionListener(this);
		create_btn.setBounds(79, 85, 86, 30);
		create_btn.setActionCommand("create");
		create_btn.setFocusable(connectCheck);
		add(create_btn);

		con_state_label = new JLabel("접속상태 : OFF");
		con_state_label.setBounds(12, 301, 226, 15);
		add(con_state_label);

		setVisible(true);
	}

	public int ConnectCreation() {
		try {
			socket_to_host = new Socket(host_address, port_to_host_number);
			getIpAddress();

			in = new BufferedReader(new InputStreamReader(socket_to_host.getInputStream()));
			out = new PrintWriter(socket_to_host.getOutputStream(), true);
		} catch (UnknownHostException e) {
			Alert("경고", "알수없는 호스트입니다.");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			Alert("경고", "연결에 실패하였습니다.");
			return 0;
		} // try-catch
		return 1;
	}

	public void getIpAddress() {
		InetAddress local_ip = socket_to_host.getLocalAddress();
		s_local_address = local_ip.toString();
		// 위에서 구하면 /127.0.0.1 처럼 나오는데, 앞의 슬래쉬(/) 를 제거하기 위한 코드 부분
		for (int i = s_local_address.length() - 1; i >= 0; i--) {
			if (s_local_address.charAt(i) == '/') {
				s_local_address = s_local_address.substring(i + 1);
				break;
			} // if
		} // for
	}

	public void Alert(String alert_title, String alert_message) {
		// alert 메소드
		dialog = new JDialog(this, alert_title, true);
		JLabel lll = new JLabel(alert_message);
		lll.setVerticalTextPosition(SwingConstants.CENTER);
		lll.setHorizontalTextPosition(SwingConstants.CENTER);
		JPanel ttt = new JPanel();
		ttt.add(lll);
		dialog.setLocationRelativeTo(null);
		dialog.setSize(320, 100);
		dialog.setContentPane(ttt);
		dialog.show();
	}

	// 이벤트
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Thread t, rt;
		if (command.equals("connect")) {
			connectKey = bottom_code_tf.getText();
			connectName = bottom_name_tf.getText();
			out.println("#share#" + nickName);
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
				out.println("#connectSuccess#");
				t = new Thread(new ReceiveScreen(socket_to_host));
				t.start();
			} else {
				Alert("접속 실패", "원격접속에 실패했습니다.");
			}
		} else if (command.equals("create")) {
			out.println("공유키가 생성되었습니다.");
			shareKey = (int) (Math.random() * 100000);
			System.out.println(shareKey);
			top_label3.setText("원격코드 : " + String.valueOf(shareKey));
			nickName = id_tf.getText();
			name_label.setText(nickName + "으로 접속 되었습니다.");
			id_tf.setVisible(false);
			create_btn.setVisible(false);
			connect_btn.setEnabled(true);
			bottom_name_tf.setEnabled(true);
			bottom_code_tf.setEnabled(true);
			con_state_label.setText("접속상태 : ON");
			Alert("공유", "공유키가 생성되었습니다. : " + String.valueOf(shareKey));
		}
	}

	public void run() {
		String in_msg = null;
		Thread st;
		System.out.println("run");
		try {
			r = new Robot();
			while (true) {
				in_msg = in.readLine();
				System.out.println("넘어온 메시지" + in_msg);
				// 보통 #c#이 넘어오다가 검색하면 #s#sdfsdfwef 이 넘어옴
				// System.out.println(in_msg)
				if (in_msg != null) {
					if (in_msg.startsWith("#connect#")) {
						if (shareKey != 0)
							out.println("#shareKey#" + shareKey);
					}
					if (in_msg.startsWith("#press#") && shareKey != 0) {
						r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					} else if (in_msg.startsWith("#release#") && shareKey != 0) {
						r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					} else if (in_msg.startsWith("#drag#") && shareKey != 0) {
						r.mouseMove(Integer.valueOf(in_msg.substring(6).split(":")[0]),
								Integer.valueOf(in_msg.substring(6).split(":")[1]));
					} else if (in_msg.startsWith("#move#") && shareKey != 0) {
						r.mouseMove(Integer.valueOf(in_msg.substring(6).split(":")[0]),
								Integer.valueOf(in_msg.substring(6).split(":")[1]));
					} else if (in_msg.startsWith("#shareKey#") && connectKey != null) {
						if (connectKey.equals(in_msg.substring(10))) {
							connectCheck = true;
						}
					} else if (in_msg.startsWith("#share#") && shareKey != 0) {
						// 화면 공유 시작
						Alert("원격접속", "다른 PC와 원격접속이 되었습니다.");
						SendScreen sc = new SendScreen(socket_to_host);
						sc.start();
					} else if (in_msg.startsWith("#send#") && shareKey == 0) {
						System.out.println("이미지 받음");
					} else if (in_msg.startsWith("#connectSuccess#")) {
						connectOn = true;
					}
				} else {
					break;
				}
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// run

	class P2p_server extends Thread {
		ServerSocket p2p_server_socket;
		Socket p2p_client;
		PrintWriter requestor;

		public P2p_server(ServerSocket ss) {
			p2p_server_socket = ss;
			start();
		}

		public synchronized void setRequestor(PrintWriter requestor) {
			this.requestor = requestor;
		}

		public void run() {
			while (true) {
				try {
					p2p_client = p2p_server_socket.accept();
					System.out.println("p2p_server run");
				} catch (Exception ex) {

				}
				P2p_connection pc = new P2p_connection(p2p_client, this);
				pc.start();
			}
		}
	}

	class P2p_connection extends Thread {
		Socket socket;
		P2p_server p2p_server;
		BufferedReader in;
		FileInputStream fis;
		File file;
		String file_name;
		BufferedOutputStream out;

		public P2p_connection(Socket s, P2p_server p2p) {
			socket = s;
			p2p_server = p2p;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedOutputStream(socket.getOutputStream());
			} catch (Exception ex) {

			}
		}

		public void run() {
			String msg = "";
			System.out.println("p2p_connection run");
			try {
				while (true) {
					msg = in.readLine();
					if (msg != null) {
						System.out.println("incoming msg : " + msg);
						if (msg.startsWith("#r#")) {
							file_name = msg.substring(3);
							file = new File(pathname + file_name);
							fis = new FileInputStream(file);
							int c;
							while ((c = fis.read()) != -1) {
								out.write(c);
							}
							out.flush();
							break;
						}
					} else {
						break;
					}
				}
			} catch (Exception ex) {
				System.err.println("in the P2p_connection : " + ex);
			} finally {
				try {
					out.close();
					fis.close();
					in.close();
				} catch (Exception exc) {
					System.err.println("in the P2p_connection finally : " + exc);
				}
			}
		}

	}

	class RemoteScreen {
		Robot r;

		public RemoteScreen() {
			try {
				r = new Robot();
				while (true) {

				}
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	// 접속을 요청하는 컴퓨터에게 화면 전송
	class SendScreen extends Thread implements Serializable {
		Socket socket;
		ObjectOutputStream oos;

		public SendScreen(Socket s) {
			this.socket = s;
			try {
				oos = new ObjectOutputStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			try {
				while (true) {
					Thread.sleep(10);
					if (!connectOn) {
						out.println("#shareKey#" + shareKey);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Image capture() {
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
	}

	// 접속요청한 화면 받아오기
	class ReceiveScreen extends JFrame implements Runnable, MouseListener, MouseMotionListener {
		boolean onScreen = false;
		Socket socket;

		ObjectInputStream ois;

		public ReceiveScreen(Socket s) {
			super("접속화면");
			this.socket = s;
			try {
				ois = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			onScreen = true;
			addMouseListener(this);
			addMouseMotionListener(this);
			setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
			setVisible(true);
		}

		public void run() {
			try {
				BufferedImage image = null;
				while (onScreen) {
					Thread.sleep(10);
					try {
						System.out.println("이미지 읽는중");
						image = (BufferedImage) ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					if (image != null) { // image가 null이 아닌 경우
						int w = this.getWidth();
						int h = this.getHeight();
						img = image.getScaledInstance(w, h, Image.SCALE_DEFAULT);
						// this.repaint();
						this.drawImage(img, w, h);
					} else {
						System.out.println("이미지 못받음");
					}
					if (!isVisible())
						onScreen = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
				g.drawImage(RobotClient.img, 0, 0, RobotClient.img.getWidth(this), RobotClient.img.getHeight(this),
						this);
			}
		}

		public void mouseClicked(MouseEvent e) {
			// mouse Click
		}

		public void mousePressed(MouseEvent e) {
			out.println("#press#" + e.getX() + ":" + e.getY());
			// 마우스 버튼 클릭
		}

		public void mouseReleased(MouseEvent e) {
			out.println("#release#" + e.getX() + ":" + e.getY());
		}

		public void mouseEntered(MouseEvent e) {
			// mouse Enter
		}

		public void mouseExited(MouseEvent e) {
			// mouse Exit
		}

		public void mouseDragged(MouseEvent e) {
			out.println("#drag#" + e.getX() + ":" + e.getY());
		}

		public void mouseMoved(MouseEvent e) {
			out.println("#move#" + e.getX() + ":" + e.getY());
			// move
		}
	}

	public static void main(String[] args) {
		RobotClient rc = new RobotClient();
		rc.run();
	}
}