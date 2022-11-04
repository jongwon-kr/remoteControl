package remoteConnect;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.border.MatteBorder;

public class remoteScreen {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					remoteScreen window = new remoteScreen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public remoteScreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBounds(100, 100, 1076, 666);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel connectStatePanel = new JPanel();
		connectStatePanel.setBorder(new MatteBorder(3, 0, 0, 0, (Color) new Color(68, 68, 255)));
		connectStatePanel.setBackground(new Color(244, 250, 255));
		frame.getContentPane().add(connectStatePanel, BorderLayout.SOUTH);
		connectStatePanel.setLayout(new BorderLayout(0, 0));
				
				JLabel conTimeLabel = new JLabel("연결 시간 : 00 : 00");
				conTimeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
				conTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				connectStatePanel.add(conTimeLabel, BorderLayout.WEST);
				
				JLabel conUserLabel = new JLabel("연결 상대 : aaa");
				conUserLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
				conUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
				connectStatePanel.add(conUserLabel, BorderLayout.CENTER);
				
				JButton phoneBtn = new JButton("음성 연결");
				phoneBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
				connectStatePanel.add(phoneBtn, BorderLayout.EAST);

		JPanel receiveScreenPanel = new JPanel();
		receiveScreenPanel.setBackground(new Color(244, 250, 255));
		frame.getContentPane().add(receiveScreenPanel, BorderLayout.CENTER);
	}

}
