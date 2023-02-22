package clientPackage;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import dto.JoinReqDto;
import dto.MessageReqDto;
import dto.RequestDto;
import dto.RoomReqDto;
import lombok.Getter;

@Getter
public class Client extends JFrame {

	private static Client instance;

	public static Client getInstance() {
		if (instance == null) {
			instance = new Client();
		}
		return instance;
	}
	private String roomName;
	private JTextArea chattingResult;
	private String userName;
	private CardLayout mainCard;
	private Gson gson;
	private JPanel mainPanel;
	private Socket socket;
	private JTextField IdInput;
	private JList<String> roomList;
	private DefaultListModel<String> roomListModel;
	private JScrollPane roomListpane;
	private JTextField chattingMessage;
	private JLabel roomTitle;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = Client.getInstance();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Client() {

		try {
			socket = new Socket("127.0.0.1", 9090);

			JOptionPane.showMessageDialog(null, "서버와 연결되었습니다.", "카카오톡 알림", JOptionPane.INFORMATION_MESSAGE);

			ClientReceive clientReceive = new ClientReceive(socket);
			clientReceive.start();
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

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
		IdInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				}
			}
		});
		IdInput.setText("");
		IdInput.setHorizontalAlignment(SwingConstants.CENTER);
		IdInput.setFont(new Font("굴림", Font.BOLD, 20));
		IdInput.setColumns(10);
		IdInput.setBounds(83, 403, 291, 46);
		loginPanel.add(IdInput);

		JButton loginButton = new JButton("");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		loginButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				login();

			}
		});

		loginButton.setIcon(new ImageIcon("src\\image\\kakao_login_medium_wide.png"));
		loginButton.setBackground(Color.BLACK);
		loginButton.setBounds(83, 465, 290, 35);
		loginPanel.add(loginButton);

		JLabel background = new JLabel("");
		background.setIcon(new ImageIcon("src\\image\\kakao.png"));
		background.setBounds(162, 27, 464, 761);
		loginPanel.add(background);

		JPanel chatListPanel = new JPanel();
		chatListPanel.setBackground(new Color(255, 233, 30));
		mainPanel.add(chatListPanel, "chatListPanel");
		chatListPanel.setLayout(null);

		roomListModel = new DefaultListModel<>();

		JLabel logo = new JLabel("");
		logo.setIcon(new ImageIcon("src\\image\\KakaoTalk_20230216_110411110_02.png"));
		logo.setBounds(12, 56, 68, 53);
		chatListPanel.add(logo);

		JButton produce_room = new JButton("");
		produce_room.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		produce_room.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				try {
					roomName = JOptionPane.showInputDialog(null, "방제목을 입력해주세요.", "카카오톡 알림",
							JOptionPane.INFORMATION_MESSAGE);

					RequestDto requestDto = new RequestDto("create", roomName);
					String requestDtoJson = gson.toJson(requestDto);

					System.out.println(requestDtoJson);
					OutputStream outputStream = socket.getOutputStream();
					PrintWriter out = new PrintWriter(outputStream, true);
					out.println(requestDtoJson);

				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		produce_room.setBackground(new Color(249, 225, 0));
		produce_room.setIcon(new ImageIcon("src\\image\\55.png"));
		produce_room.setBounds(28, 131, 30, 26);
		chatListPanel.add(produce_room);

		roomListpane = new JScrollPane();
		roomListpane.setBounds(88, 0, 366, 751);
		chatListPanel.add(roomListpane);

		roomListModel = new DefaultListModel<>();
		roomList = new JList<String>(roomListModel);
		roomListpane.setViewportView(roomList);

		JPanel chatPanel = new JPanel();
		chatPanel.setBackground(new Color(255, 233, 60));
		mainPanel.add(chatPanel, "chatPanel");
		chatPanel.setLayout(null);

		JLabel Logo = new JLabel("");
		Logo.setBackground(new Color(255, 230, 33));
		Logo.setIcon(new ImageIcon("src\\image\\666.png"));
		Logo.setBounds(25, 10, 40, 36);
		chatPanel.add(Logo);
		
		roomTitle = new JLabel("");
		roomTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		roomTitle.setBounds(80, 10, 200, 30);
		chatPanel.add(roomTitle);

		chattingResult = new JTextArea();
		chattingResult.setBounds(0, 56, 454, 619);
		chatPanel.add(chattingResult);

		JButton outButton = new JButton("");
		outButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		outButton.setIcon(new ImageIcon("src\\image\\777.png"));
		outButton.setBackground(new Color(255, 230, 33));
		outButton.setBounds(402, 10, 40, 36);
		chatPanel.add(outButton);

		chattingMessage = new JTextField();
		chattingMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});
		chattingMessage.setBounds(0, 673, 383, 78);
		chatPanel.add(chattingMessage);
		chattingMessage.setColumns(10);

		JButton sendButton = new JButton("");
		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!chattingMessage.getText().isBlank()) {
					sendMessage();
				}
			}
		});
		sendButton.setBackground(new Color(255, 255, 255));
		sendButton.setIcon(new ImageIcon("src\\image\\1010.png"));
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		sendButton.setBounds(382, 673, 72, 78);
		chatPanel.add(sendButton);
	}

	private void sendRequest(String resource, String body) {
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);

			RequestDto requestDto = new RequestDto(resource, body);

			out.println(gson.toJson(requestDto));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage() {
		if (!chattingMessage.getText().isBlank()) {
			String toUser = roomList.getSelectedIndex() == 0 ? "all" : roomList.getSelectedValue();

			MessageReqDto messageReqDto = new MessageReqDto(toUser, userName, chattingMessage.getText());

			sendRequest("sendMessage", gson.toJson(messageReqDto));

			chattingMessage.setText("");
		}
	}
	private void login() {
		String userName = IdInput.getText();

		try {
			JoinReqDto joinReqDto = new JoinReqDto(userName);
			String joinReqDtoJson = gson.toJson(joinReqDto);
			RequestDto requestDto = new RequestDto("join", joinReqDtoJson);
			String requestDtoJson = gson.toJson(requestDto);
			System.out.println(requestDto.getResource());
			System.out.println(joinReqDtoJson);

			OutputStream outputStream;
			outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			out.println(requestDtoJson);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
