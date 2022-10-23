package Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements Runnable, ActionListener {
	private PhoneServer phoneServer;
	private Phone phone;

	private JPanel left_panel;
	private JPanel right_top_panel;
	private JPanel right_down_panel;
	private JTextArea textArea = new JTextArea();
	private JTextField nameField = new JTextField();
	private JTextField textField = new JTextField();
	private JDialog JDia = new JDialog();
	private JButton phoneBtn = new JButton();
	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;

	public Client() {
		setTitle("client");
		setSize(960, 620);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);
		setVisible(true);

		left_panel = new JPanel();
		textArea.setBounds(10, 10, 700, 300);
		add(textArea);

		nameField.setBounds(10, 320, 200, 50);
		add(nameField);

		textField.setBounds(10, 380, 700, 50);
		textField.setActionCommand("textField");
		textField.addActionListener(this);
		add(textField);

		phoneBtn = new JButton("call");
		phoneBtn.setBackground(Color.white);
		phoneBtn.setFocusable(false);
		phoneBtn.setFont(new Font("Dialog", Font.BOLD, 12));
		phoneBtn.setPreferredSize(new Dimension(120, 30));
		phoneBtn.setActionCommand("phone");
		phoneBtn.addActionListener(this);

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
	}

	public void run() {
		String in_msg = null;
		while (true) {
			try {
				in_msg = reader.readLine();
				if (in_msg != null) {
					System.out.println("메시지" + in_msg);
					textArea.append(in_msg + "\n");
					if (in_msg.contains("#robot#")) {
						try {
							Robot r = new Robot();
							System.out.println(in_msg.split(":")[0]);
							if (in_msg.split(":")[1].contains("move")) {
								int x, y;
								x = Integer.valueOf(in_msg.split("move")[1].split(",")[0]);
								y = Integer.valueOf(in_msg.split("move")[1].split(",")[1]);
								r.mouseMove(x, y);
								System.out.println("move" + x + ", " + y);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("textField")) {
			try {
				writer.println(nameField.getText() + " : " + textField.getText());
				textField.setText("");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (command.equals("phone")) {

		}
	}

	public void connect() {
		try {
			socket = new Socket("192.168.0.8", 1234);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			new Thread(this).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.connect();
	}
}