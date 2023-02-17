package clientPackage;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import dto.RequestDto;

import java.awt.Color;
import javax.swing.JLabel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JList;
import java.awt.CardLayout;
import javax.swing.JTextArea;

public class Client extends JFrame {
	
private static Client instance;
	
	public static Client getInstance() {
		if(instance == null) {
			instance = new Client();
		}
		return instance;
	}
	private CardLayout mainCard;
	private Gson gson;
	private JPanel mainPanel;
	private Socket socket;
	private JTextField IdInput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Client() {
		setBackground(Color.YELLOW);
		gson = new Gson();
		
		setIconImage(Toolkit.getDefaultToolkit().getImage("src\\image\\kakao.png"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 800);
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(mainPanel);
		mainCard = new CardLayout();
		mainPanel.setLayout(mainCard);
		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(null);
		loginPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.add(loginPanel, "loginPanel");
		
		IdInput = new JTextField();
		IdInput.setText("김상현");
		IdInput.setHorizontalAlignment(SwingConstants.CENTER);
		IdInput.setFont(new Font("굴림", Font.BOLD, 20));
		IdInput.setColumns(10);
		IdInput.setBounds(83, 403, 291, 46);
		loginPanel.add(IdInput);
		
		JButton loginButton = new JButton("");
		loginButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String Id = null;
				Id = IdInput.getText();
				
				
				try {
					socket = new Socket("127.0.0.1", 9090);
					
					JOptionPane.showMessageDialog(null, 
							"김상현님 환영합니다.", 
							"카카오톡 알림", 
							JOptionPane.INFORMATION_MESSAGE);
					
					ClientReceive clientReceive = new ClientReceive(socket);
					clientReceive.start();
					
					
					mainCard.show(mainPanel, "chatListPanel");
					
					} catch (ConnectException e1) {
					
					JOptionPane.showMessageDialog(null, 
							"서버 접속 실패", 
							"접속실패", 
							JOptionPane.ERROR_MESSAGE);
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				
			}
		});
				
		loginButton.setIcon(new ImageIcon("src\\image\\kakao_login_medium_wide.png"));
		loginButton.setBackground(Color.BLACK);
		loginButton.setBounds(83, 465, 290, 35);
		loginPanel.add(loginButton);
		
		JLabel background = new JLabel("");
		background.setIcon(new ImageIcon("src\\image\\kakao.png"));
		background.setBounds(0, 0, 464, 761);
		loginPanel.add(background);
		
		JPanel chatListPanel = new JPanel();
		chatListPanel.setBackground(new Color(255, 233, 30));
		mainPanel.add(chatListPanel, "chatListPanel");
		chatListPanel.setLayout(null);
		
		JList list = new JList();
		list.setBounds(88, 0, 366, 751);
		chatListPanel.add(list);
		
		JLabel logo = new JLabel("");
		logo.setIcon(new ImageIcon("src\\image\\KakaoTalk_20230216_110411110_02.png"));
		logo.setBounds(12, 56, 68, 53);
		chatListPanel.add(logo);
		
		JButton produce_room = new JButton("");
		produce_room.setBackground(new Color(249, 225, 0));
		produce_room.setIcon(new ImageIcon("src\\image\\55.png"));
		produce_room.setBounds(28, 131, 30, 26);
		chatListPanel.add(produce_room);
		
		JPanel chatPanel = new JPanel();
		chatPanel.setBackground(new Color(255, 233, 60));
		mainPanel.add(chatPanel, "chatPanel");
		chatPanel.setLayout(null);
		
		JLabel Logo = new JLabel("");
		Logo.setBackground(new Color(255, 230, 33));
		Logo.setIcon(new ImageIcon("src\\image\\666.png"));
		Logo.setBounds(25, 10, 40, 36);
		chatPanel.add(Logo);
		
		JLabel title = new JLabel("제목 : 김상현님의 방");
		title.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		title.setBounds(104, 10, 149, 36);
		chatPanel.add(title);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("src\\image\\777.png"));
		lblNewLabel.setBounds(412, 10, 30, 36);
		chatPanel.add(lblNewLabel);
		
		JTextArea chatting = new JTextArea();
		chatting.setBounds(0, 56, 454, 695);
		chatPanel.add(chatting);
	}
	}

