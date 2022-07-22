package remoteConnect;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RobotClient extends JFrame
		implements KeyListener, MouseListener, ActionListener, Runnable, ListSelectionListener {
	ServerSocket server_socket;
	Socket socket_to_host;
	String host_address = "127.0.0.1";
	String s_local_address;
	// server�� ����� port��ȣ
	int port_to_host_number = 12156;
	// client���� ����� port��ȣ
	int p2p_port_number = 12166;
	Vector v_client_address;

	BufferedReader in = null;
	PrintWriter out;
	Thread t_connection;
	String pathname;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int SCREEN_WIDTH = screenSize.width; // ȭ�� ���� �ʺ�
	final int SCREEN_HEIGHT = screenSize.height; // ȭ�� ���� �ʺ�

	Robot r;

	JFrame remoteScreen; // �������� ������

	JPanel top_panel; // ��� �г�
	JButton connect; // ���� ��ư
	JTextField conTf; // ���� textfield

	JPanel centerLeft_panel; // �߾� �г�
	JPanel centerRight_panel;
	JSplitPane centerSplitPane; // �߾� splitpane
	JSplitPane mainSplitPane; // ����ȭ�� splitpane
	JDialog dialog;

	public RobotClient() {
		super("���� ����");
		setRemoteScreen();
		getContentPane().add(setUI()).setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(484, 363);
		setVisible(true);
	}

	public int ConnectCreation() {
		try {
			socket_to_host = new Socket(host_address, port_to_host_number);
			server_socket = new ServerSocket(p2p_port_number);
			getIpAddress();

			in = new BufferedReader(new InputStreamReader(socket_to_host.getInputStream()));
			out = new PrintWriter(socket_to_host.getOutputStream(), true);
			t_connection = new Thread(this);
			t_connection.start();
			P2p_server robot_server = new P2p_server(server_socket);
			// p2p���� Ŭ����
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

	public void getIpAddress() {
		InetAddress local_ip = socket_to_host.getLocalAddress();
		s_local_address = local_ip.toString();
		// ������ ���ϸ� /127.0.0.1 ó�� �����µ�, ���� ������(/) �� �����ϱ� ���� �ڵ� �κ�
		for (int i = s_local_address.length() - 1; i >= 0; i--) {
			if (s_local_address.charAt(i) == '/') {
				s_local_address = s_local_address.substring(i + 1);
				break;
			} // if
		} // for
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

		centerLeft_panel.setPreferredSize(new Dimension(100, 300));
		top_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		top_panel.add(conTf);
		top_panel.add(connect);
		centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerLeft_panel, centerRight_panel);
		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top_panel, centerSplitPane);
		return mainSplitPane;
	}

	// ��������� ������ ����
	public void setRemoteScreen() {
		remoteScreen = new JFrame();
		remoteScreen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		remoteScreen.setVisible(false);
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

	// �̺�Ʈ
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals("connect")) {
			remoteScreen.setVisible(true);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

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

	public static void main(String[] args) {
		new RobotClient();
	}
}
