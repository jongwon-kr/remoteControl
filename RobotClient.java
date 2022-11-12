package remoteConnect;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import remoteConnect.PhoneServer.Connect;
import remoteConnect.PhoneServer.Mic;
import remoteConnect.PhoneServer.Speaker;

public class RobotClient extends JFrame implements ActionListener, Runnable, Serializable {
	Robot r;
	ServerSocket server_socket;
	Socket socket_to_host;
	String host_address = "192.168.0.103";
	String send_server_address;
	// server�� ����� port��ȣ
	int port_to_host_number = 12566;
	int screen_port = 3333;
	// ���� Ű
	boolean connectCheck = false, connectOn = false;
	static String shareKey, nickName = "";

	BufferedReader in = null;
	static PrintWriter out;

	// ȭ�� ������ ���� bufferedInputStream, outputStream
	BufferedInputStream bin;
	BufferedOutputStream bout;

	Thread t_connection;
	String pathname, connectKey, connectName;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int SCREEN_WIDTH = screenSize.width; // ȭ�� ���� �ʺ�
	final int SCREEN_HEIGHT = screenSize.height; // ȭ�� ���� �ʺ�
	static Image img = null; // ������. UI ��ġ.

	JDialog dialog;
	JPanel connect_panel; // ����� �г�
	JPanel connect_panel_top; // ����� �г� ���
	JPanel connect_panel_bottom; // ����� �г� �ϴ�

	// ����� ��� �г�
	JLabel top_label1;
	JLabel top_label2;
	JLabel top_label3;

	// ����� �ϴ� �г�
	JLabel bottom_label1;
	JTextField bottom_name_tf;
	JTextField bottom_code_tf;
	JButton connect_btn;

	// �α��� ��
	JLabel name_label;
	JTextField id_tf;
	JButton create_btn;

	JLabel con_state_label; // ���� ����

	public RobotClient() {
		super("���� ����");
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

		top_label1 = new JLabel("PC �����ڵ�");
		top_label1.setBounds(12, 26, 334, 30);
		connect_panel_top.add(top_label1);
		top_label1.setFont(new Font("���� ���", Font.BOLD, 22));
		top_label1.setHorizontalAlignment(SwingConstants.CENTER);
		top_label1.setBackground(new Color(255, 255, 255));

		top_label2 = new JLabel("�� �ڵ带 �ٸ� PC�� �Է��ϸ� ���� ������ �˴ϴ� ");
		top_label2.setBounds(22, 66, 324, 19);
		connect_panel_top.add(top_label2);
		top_label2.setHorizontalAlignment(SwingConstants.CENTER);
		top_label2.setFont(new Font("���� ���", Font.PLAIN, 13));

		top_label3 = new JLabel("�ڵ带 �����ϸ� ǥ�õ˴ϴ�.");
		top_label3.setBounds(12, 95, 334, 30);
		connect_panel_top.add(top_label3);
		top_label3.setFont(new Font("���� ���", Font.BOLD, 15));
		top_label3.setHorizontalAlignment(SwingConstants.CENTER);

		// bottom panel
		connect_panel_bottom = new JPanel();
		connect_panel_bottom.setBackground(new Color(230, 242, 255));
		connect_panel.add(connect_panel_bottom);
		connect_panel_bottom.setLayout(null);

		bottom_label1 = new JLabel("�����Ϸ��� PC�� �г��Ӱ� ���� �ڵ带 �Է��ϼ���.");
		bottom_label1.setBounds(12, 30, 334, 25);
		bottom_label1.setHorizontalAlignment(SwingConstants.CENTER);
		bottom_label1.setFont(new Font("���� ���", Font.BOLD, 14));
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
		connect_btn.setFont(new Font("���� ���", Font.BOLD, 14));
		connect_panel_bottom.add(connect_btn);
		connect_btn.setBackground(Color.white);
		connect_btn.addActionListener(this);
		connect_btn.setActionCommand("connect");
		connect_btn.setFocusable(false);
		connect_btn.setEnabled(false);
		connect_panel_bottom.add(connect_btn);

		JLabel bottom_name_label = new JLabel("Name");
		bottom_name_label.setFont(new Font("���� ���", Font.PLAIN, 13));
		bottom_name_label.setHorizontalAlignment(SwingConstants.RIGHT);
		bottom_name_label.setBounds(12, 65, 50, 25);
		connect_panel_bottom.add(bottom_name_label);

		JLabel bottom_code_label = new JLabel("Code");
		bottom_code_label.setFont(new Font("���� ���", Font.PLAIN, 13));
		bottom_code_label.setHorizontalAlignment(SwingConstants.RIGHT);
		bottom_code_label.setBounds(12, 92, 50, 25);
		connect_panel_bottom.add(bottom_code_label);

		name_label = new JLabel("����� �г����� �����ּ���.");
		name_label.setFont(new Font("���� ���", Font.BOLD, 13));
		name_label.setHorizontalAlignment(SwingConstants.CENTER);
		name_label.setBounds(22, 15, 200, 25);
		add(name_label);

		id_tf = new JTextField();
		id_tf.setBounds(32, 50, 180, 25);
		getContentPane().add(id_tf);

		create_btn = new JButton("Create");
		create_btn.setBackground(Color.white);
		create_btn.setFont(new Font("���� ���", Font.BOLD, 12));
		create_btn.addActionListener(this);
		create_btn.setBounds(79, 85, 86, 30);
		create_btn.setActionCommand("create");
		create_btn.setFocusable(true);
		add(create_btn);

		con_state_label = new JLabel("���ӻ��� : OFF");
		con_state_label.setBounds(12, 301, 226, 15);
		add(con_state_label);
		setVisible(true);
	}

	public int ConnectCreation() {
		try {
			socket_to_host = new Socket(host_address, port_to_host_number);
			in = new BufferedReader(new InputStreamReader(socket_to_host.getInputStream()));
			out = new PrintWriter(socket_to_host.getOutputStream(), true);
		} catch (UnknownHostException e) {
			Alert("���", "�˼����� ȣ��Ʈ�Դϴ�.");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			Alert("���", "���ῡ �����Ͽ����ϴ�.");
			return 0;
		} // try-catch
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
		dialog.setLocationRelativeTo(null);
		dialog.setSize(320, 100);
		dialog.setContentPane(ttt);
		dialog.show();
	}

	// �̺�Ʈ
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Thread t, rt;
		if (command.equals("connect")) {
			connectKey = bottom_code_tf.getText();
			connectName = bottom_name_tf.getText();
			out.println("#share#" + ":" + connectKey + ":" + connectName);
			try {
				int cnt = 0;
				while (!connectCheck) {
					Thread.sleep(200);
					System.out.println("������ �������Դϴ�.");
					cnt++;
					if (cnt == 6)
						break;
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (connectCheck) {
				Alert("���� ����", "���ӿ� �����߽��ϴ�.");
				Thread rScreen = new Thread(new ReceiveScreen());
				rScreen.start();
				System.out.println("???");
			} else {
				Alert("���� ����", "�������ӿ� �����߽��ϴ�.");
			}
		} else if (command.equals("create")) {
			shareKey = String.valueOf((int) (Math.random() * 100000));
			System.out.println(shareKey);
			top_label3.setText("�����ڵ� : " + String.valueOf(shareKey));
			nickName = id_tf.getText();
			name_label.setText(nickName + "���� ���� �Ǿ����ϴ�.");
			id_tf.setVisible(false);
			create_btn.setVisible(false);
			connect_btn.setEnabled(true);
			bottom_name_tf.setEnabled(true);
			bottom_code_tf.setEnabled(true);
			con_state_label.setText("���ӻ��� : ON");
			Alert("����", "����Ű�� �����Ǿ����ϴ�. : " + String.valueOf(shareKey));
		}
	}

	public void run() {
		String in_msg = null;
		try {
			r = new Robot();
			double x, y;
			while (true) {
				in_msg = in.readLine();
				System.out.println("�Ѿ�� �޽���" + in_msg);
				if (in_msg != null) {
					if (in_msg.startsWith("#press#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						if (in_msg.split(":")[3].equals("left")) {
							r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
						} else if (in_msg.split(":")[3].equals("middle")) {
							r.mousePress(InputEvent.BUTTON2_DOWN_MASK);
						} else if (in_msg.split(":")[3].equals("right")) {
							r.mousePress(InputEvent.BUTTON3_DOWN_MASK);
						}
					} else if (in_msg.startsWith("#release#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						if (in_msg.split(":")[3].equals("left")) {
							r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
						} else if (in_msg.split(":")[3].equals("middle")) {
							r.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
						} else if (in_msg.split(":")[3].equals("right")) {
							r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
						}
					} else if (in_msg.startsWith("#drag#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						x = Double.valueOf(in_msg.split(":")[3]) * SCREEN_WIDTH / 65535;
						y = Double.valueOf(in_msg.split(":")[4]) * SCREEN_HEIGHT / 65535;
						r.mouseMove((int) Math.round(x), (int) Math.round(y));
					} else if (in_msg.startsWith("#move#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						x = Double.valueOf(in_msg.split(":")[3]) * SCREEN_WIDTH / 65535;
						y = Double.valueOf(in_msg.split(":")[4]) * SCREEN_HEIGHT / 65535;
						r.mouseMove((int) Math.round(x), (int) Math.round(y));
					} else if (in_msg.startsWith("#wheel#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						if (in_msg.split(":")[3].equals("1")) {
							r.mouseWheel(1);
						} else if (in_msg.split(":")[3].equals("-1")) {
							r.mouseWheel(-1);
						}
					} else if (in_msg.startsWith("#keyPressed#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						r.keyPress(Integer.valueOf(in_msg.split(":")[3]));
					} else if (in_msg.startsWith("#keyReleased#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						r.keyRelease(Integer.valueOf(in_msg.split(":")[3]));
					} else if (in_msg.startsWith("#shareKey#") && connectKey != null && connectName != null) {
						if (connectKey.equals(in_msg.split(":")[1]) && connectName.equals(in_msg.split(":")[2])) {
							connectCheck = true;
						}
					} else if (in_msg.startsWith("#share#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						// ȭ�� ���� ����
						Thread sc = new Thread(new SendScreen());
						sc.start();
					} else if (in_msg.startsWith("#connectSuccess#")) {
						connectOn = true;
						if (!shareKey.equals(in_msg.split(":")[1]) && !nickName.equals(in_msg.split(":")[2])) {
							send_server_address = in_msg.split(":")[3];
						}
					} else if (in_msg.startsWith("#phone#") && shareKey.equals(in_msg.split(":")[1])
							&& nickName.equals(in_msg.split(":")[2])) {
						Thread pt = new Thread(new Phone());
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

	// ������ ��û�ϴ� ��ǻ�Ϳ��� ȭ�� ����
	class SendScreen implements Runnable {
		Socket sendSocket = null;
		BufferedOutputStream bout;
		BufferedImage bufImg;

		public void run() {
			try {
				while (true) {
					Thread.sleep(100);
					if (!connectOn) {
						out.println("#shareKey#" + ":" + shareKey + ":" + nickName);
					} else {
						break;
					}
				}
				if (connectOn) {
					try {
						while (sendSocket == null) {
							sendSocket = new Socket(send_server_address, 3333);
							System.out.println(send_server_address + "�� ����Ϸ�");
						}
						bout = new BufferedOutputStream(sendSocket.getOutputStream());
						while (bout != null) {
							Thread.sleep(100);
							bufImg = capture();
							ImageIO.write(bufImg, "bmp", bout);
							bout.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public BufferedImage capture() {
			Robot robot;
			BufferedImage bufImage = null;
			try {
				robot = new Robot();
				Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
				bufImage = robot.createScreenCapture(area); // Robot Ŭ������ �̿��Ͽ� ��ũ�� ĸ��.
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bufImage;
		}
	}

	// ���ӿ�û�� ȭ�� �޾ƿ���
	class ReceiveScreen extends JFrame
			implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, ActionListener {
		boolean onScreen = false;
		String checkMouseButton;
		JPanel connectStatePanel;
		JLabel conTimeLabel;
		JLabel conUserLabel;
		static JButton phoneBtn;
		static JPanel receiveScreenPanel;
		Timer timer;
		TimerTask timerTask;
		int connectTime;

		public ReceiveScreen() {
			super("����ȭ��");
			onScreen = true;
			connectStatePanel = new JPanel();
			connectStatePanel.setBorder(new MatteBorder(3, 0, 0, 0, (Color) new Color(68, 68, 255)));
			connectStatePanel.setBackground(new Color(244, 250, 255));
			connectStatePanel.setLayout(new BorderLayout());
			add("South", connectStatePanel);

			conTimeLabel = new JLabel("���� �ð� : 0�� 0��");
			conTimeLabel.setFont(new Font("���� ���", Font.PLAIN, 14));
			conTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			connectStatePanel.add("West", conTimeLabel);

			JLabel conUserLabel = new JLabel("���� ��� : " + bottom_name_tf.getText());
			conUserLabel.setFont(new Font("���� ���", Font.PLAIN, 14));
			conUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
			connectStatePanel.add("Center", conUserLabel);

			JButton phoneBtn = new JButton("���� ����");
			phoneBtn.addActionListener(this);
			phoneBtn.setActionCommand("phone");
			phoneBtn.setFocusable(false);
			phoneBtn.setFont(new Font("���� ���", Font.PLAIN, 14));

			connectStatePanel.add("East", phoneBtn);

			receiveScreenPanel = new JPanel();
			receiveScreenPanel.setBackground(new Color(244, 250, 255));
			receiveScreenPanel.addMouseListener(this);
			receiveScreenPanel.addMouseMotionListener(this);
			receiveScreenPanel.addMouseWheelListener(this);
			receiveScreenPanel.addKeyListener(this);
			add("Center", receiveScreenPanel);
			setSize(1280, 754);
			setVisible(true);
			receiveScreenPanel.setFocusable(true);
			receiveScreenPanel.requestFocus();
		}

		// connectPhone
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Thread pt = null;
			if (command.equals("phone")) {
				System.out.println("���� ��ȭ");
				pt = new Thread(new PhoneServer());
				out.println("#phone#" + ":" + connectKey + ":" + connectName);
			}
		}

		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				checkMouseButton = "left";
			} else if (SwingUtilities.isMiddleMouseButton(e)) {
				checkMouseButton = "middle";
			} else if (SwingUtilities.isRightMouseButton(e)) {
				checkMouseButton = "right";
			}
			out.println("#press#" + ":" + connectKey + ":" + connectName + ":" + checkMouseButton);
			// ���콺 ��ư Ŭ��
		}

		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				checkMouseButton = "left";
			} else if (SwingUtilities.isMiddleMouseButton(e)) {
				checkMouseButton = "middle";
			} else if (SwingUtilities.isRightMouseButton(e)) {
				checkMouseButton = "right";
			}
			out.println("#release#" + ":" + connectKey + ":" + connectName + ":" + checkMouseButton);
		}

		public void mouseEntered(MouseEvent e) {
			// mouse Enter
		}

		public void mouseExited(MouseEvent e) {
			// mouse Exit
		}

		public void mouseDragged(MouseEvent e) {
			double x = e.getX() * 65535 / receiveScreenPanel.getWidth();
			double y = e.getY() * 65535 / receiveScreenPanel.getHeight();
			out.println("#drag#" + ":" + connectKey + ":" + connectName + ":" + x + ":" + y);
		}

		public void mouseMoved(MouseEvent e) {
			double x = e.getX() * 65535 / receiveScreenPanel.getWidth();
			double y = e.getY() * 65535 / receiveScreenPanel.getHeight();
			out.println("#move#" + ":" + connectKey + ":" + connectName + ":" + x + ":" + y);
			// move
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			// �Ʒ��� 1 ���� -1
			out.println("#wheel#" + ":" + connectKey + ":" + connectName + ":" + String.valueOf(e.getWheelRotation()));
		}

		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			// �ѿ���ȯ�� �ȵ�,, �Է��� �� �ѱ��̸� press�� �Ͼ�� �ʴ´�.
			if (e.getKeyCode() != 0)
				out.println(
						"#keyPressed#" + ":" + connectKey + ":" + connectName + ":" + String.valueOf(e.getKeyCode()));
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() != 0)
				out.println(
						"#keyReleased#" + ":" + connectKey + ":" + connectName + ":" + String.valueOf(e.getKeyCode()));
		}

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void run() {
			Thread rServer = new Thread(new ReceiveServer());
			rServer.start();
			connectTime = 0;
			timer = new Timer();
			timerTask = new TimerTask() {
				public void run() {
					connectTime++;
					conTimeLabel.setText("���� �ð� : " + (int) connectTime / 60 + "�� " + connectTime % 60 + "��");
				}
			};
			timer.schedule(timerTask, 1000, 1000);
		}
	}

	class ReceiveServer implements Runnable {
		ServerSocket receiveServer;
		Socket receiveSocket;
		BufferedInputStream bin;
		BufferedImage bufImg;

		public void run() {
			try {
				receiveServer = new ServerSocket(screen_port);
				out.println("#connectSuccess#" + ":" + nickName + ":" + shareKey + ":"
						+ String.valueOf(InetAddress.getLocalHost()).split("/")[1]);
				while (receiveSocket == null) {
					receiveSocket = receiveServer.accept();
				}
				System.out.println("�������� ����");
				bin = new BufferedInputStream(receiveSocket.getInputStream());
				while (true) {
					bufImg = ImageIO.read(ImageIO.createImageInputStream(bin));
					if (bufImg != null)
						ReceiveScreen.receiveScreenPanel.getGraphics().drawImage(bufImg, 0, 0,
								ReceiveScreen.receiveScreenPanel.getWidth(),
								ReceiveScreen.receiveScreenPanel.getHeight(), ReceiveScreen.receiveScreenPanel);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// ��ȭ
	class Phone extends JFrame implements Runnable {
		public JLabel intro = new JLabel("Waiting for connect...");
		public JLabel time = new JLabel("0:00");
		public int time_cnt = 0;

		public Connect con = null;
		public Mic mic = null;
		public Speaker spk = null;

		public AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 11025, 16, 2, 4, 11025, false);

		public Phone() {
			this.setSize(300, 80);
			this.setTitle("Phone");
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
						targetLine.read(data, 0, data.length);
						p.con.output.write(data, 0, data.length);
						p.con.output.flush();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

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

				}
			}
		}

		public void run() {
			// TODO Auto-generated method stub

		}
	}

	// ��ȣ ����
	class PhoneServer extends JFrame implements Runnable {
		public JLabel intro = new JLabel("Waiting for connect...");
		public JLabel time = new JLabel("0:00");
		public int time_cnt = 0;

		public Connect con = null;
		public Mic mic = null;
		public Speaker spk = null;

		public AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 11025, 16, 2, 4, 11025, false);

		public PhoneServer() {
			this.setSize(300, 80);
			this.setTitle("Phone");
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
		}

		public void startCommunication() {
			this.mic.start();
			this.spk.start();
		}

		public class Connect extends Thread {
			ServerSocket ss = null;
			Socket socket = null;
			BufferedInputStream input = null;
			BufferedOutputStream output = null;
			byte[] temp = new byte[10000];

			PhoneServer p = null;

			Connect(PhoneServer p) {
				this.p = p;
				try {
					ss = new ServerSocket(9999);
					this.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void run() {
				try {
					socket = ss.accept();
					input = new BufferedInputStream(socket.getInputStream());
					output = new BufferedOutputStream(socket.getOutputStream());

					p.startCommunication();
					p.intro.setText("Connected!");
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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public class Mic extends Thread {
			TargetDataLine targetLine = null;
			byte[] data = null;
			PhoneServer p = null;

			public Mic(PhoneServer p) {
				this.p = p;
				try {
					DataLine.Info info = new DataLine.Info(TargetDataLine.class, p.format);
					targetLine = (TargetDataLine) AudioSystem.getLine(info);
					targetLine.open();
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
						targetLine.read(data, 0, data.length);
						p.con.output.write(data, 0, data.length);
						p.con.output.flush();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		}

		public class Speaker extends Thread {
			SourceDataLine sourceLine = null;
			byte[] data = null;
			PhoneServer p = null;

			Speaker(PhoneServer p) {
				this.p = p;
				try {
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, p.format);
					sourceLine = (SourceDataLine) AudioSystem.getLine(info);
					sourceLine.open();
					data = new byte[(int) (sourceLine.getBufferSize() / 5512)];
					System.out.println("Ready to use Speaker");
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

				}
			}
		}

		public void run() {

		}
	}

	public static void main(String[] args) {
		RobotClient rc = new RobotClient();
		rc.run();
	}
}