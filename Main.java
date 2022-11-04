package remoteConnect;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.UIManager;

public class Main {

	private JFrame frmRemoteConnect;
	private JTextField bottom_name_tf;
	private JTextField id_tf;
	private JTextField bottom_code_tf;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmRemoteConnect.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRemoteConnect = new JFrame();
		frmRemoteConnect.setTitle("remote connect");
		frmRemoteConnect.getContentPane().setBackground(new Color(244, 250, 255));
		frmRemoteConnect.setBounds(100, 100, 645, 365);
		frmRemoteConnect.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRemoteConnect.getContentPane().setLayout(null);
		
		JPanel connect_panel = new JPanel();
		connect_panel.setBounds(250, 15, 360, 300);
		connect_panel.setBorder(new LineBorder(new Color(117, 186, 255), 1, true));
		frmRemoteConnect.getContentPane().add(connect_panel);
		connect_panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		// top_panel
		JPanel connect_panel_top = new JPanel();
		connect_panel_top.setBackground(new Color(230, 242, 255));
		connect_panel_top.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(117, 186, 255)));
		connect_panel.add(connect_panel_top);
		connect_panel_top.setLayout(null);
		
		JLabel top_label1 = new JLabel("PC 원격코드");
		top_label1.setBounds(12, 26, 334, 30);
		connect_panel_top.add(top_label1);
		top_label1.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		top_label1.setHorizontalAlignment(SwingConstants.CENTER);
		top_label1.setBackground(new Color(255, 255, 255));
		
		JLabel top_label2 = new JLabel("이 코드를 다른 PC에 입력하면 원격 접속이 됩니다 ");
		top_label2.setBounds(22, 66, 324, 19);
		connect_panel_top.add(top_label2);
		top_label2.setHorizontalAlignment(SwingConstants.CENTER);
		top_label2.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		
		JLabel top_label3 = new JLabel("코드를 생성하면 표시됩니다.");
		top_label3.setBounds(12, 95, 334, 30);
		connect_panel_top.add(top_label3);
		top_label3.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		top_label3.setHorizontalAlignment(SwingConstants.CENTER);
		
		// bottom panel
		JPanel connect_panel_bottom = new JPanel();
		connect_panel_bottom.setBackground(new Color(230, 242, 255));
		connect_panel.add(connect_panel_bottom);
		connect_panel_bottom.setLayout(null);
		
		JLabel bottom_label1 = new JLabel("접속하려는 PC의 닉네임과 원격 코드를 입력하세요.");
		bottom_label1.setBounds(12, 30, 334, 25);
		bottom_label1.setHorizontalAlignment(SwingConstants.CENTER);
		bottom_label1.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		connect_panel_bottom.add(bottom_label1);
		
		bottom_name_tf = new JTextField();
		bottom_name_tf.setBounds(69, 65, 165, 25);
		bottom_name_tf.setColumns(10);
		connect_panel_bottom.add(bottom_name_tf);
		
		JButton connect_btn = new JButton("connect");
		connect_btn.setBackground(new Color(240, 240, 240));
		connect_btn.setBounds(251, 65, 95, 52);
		connect_btn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		connect_panel_bottom.add(connect_btn);
		
		bottom_code_tf = new JTextField();
		bottom_code_tf.setBounds(69, 92, 165, 25);
		bottom_code_tf.setColumns(10);
		connect_panel_bottom.add(bottom_code_tf);

		
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
		
		JLabel name_label = new JLabel("사용할 닉네임을 적어주세요.");
		name_label.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		name_label.setHorizontalAlignment(SwingConstants.CENTER);
		name_label.setBounds(22, 15, 200, 25);
		frmRemoteConnect.getContentPane().add(name_label);
		
		id_tf = new JTextField();
		id_tf.setBounds(22, 50, 200, 25);
		frmRemoteConnect.getContentPane().add(id_tf);
		id_tf.setColumns(10);
		
		
		JButton login_btn = new JButton("코드 생성");
		login_btn.setBackground(new Color(255, 255, 255));
		login_btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		login_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		login_btn.setBorder(new EmptyBorder(0, 0, 0, 0));
		login_btn.setBounds(79, 85, 86, 30);
		frmRemoteConnect.getContentPane().add(login_btn);
		
		
		JLabel con_state_label = new JLabel("접속상태 : OFF");
		con_state_label.setBounds(12, 301, 226, 15);
		frmRemoteConnect.getContentPane().add(con_state_label);
	}
}
