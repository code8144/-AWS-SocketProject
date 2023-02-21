package serverPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import dto.JoinReqDto;
import dto.JoinRespDto;
import dto.MessageReqDto;
import dto.MessageRespDto;
import dto.RequestDto;
import dto.ResponseDto;
import lombok.Data;

@Data

class ConnectedSocket extends Thread {
	private static List<String> userList = new ArrayList<>();
	private static List<ConnectedSocket> socketList = new ArrayList<>();
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Gson gson;

	private String userName;

	public ConnectedSocket(Socket socket) {
		this.socket = socket;
		gson = new Gson();
		socketList.add(this);
	}

	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			
			while(true) {
				String request = in.readLine();
				RequestDto requestDto = gson.fromJson(request, RequestDto.class);
				
				switch(requestDto.getResource()) {
				case "join" : 
					JoinReqDto joinReqDto = gson.fromJson(requestDto.getBody(), JoinReqDto.class);
					userName = joinReqDto.getUsername();
					
					//userName 중복검사
					if(userName == null || userName.isEmpty() || userList.contains(userName)) {
						System.out.println("이미 존재하는 이름입니다.");
						ResponseDto usernameResponseDto = new ResponseDto(requestDto.getResource(), "no", null);
						try {
							outputStream = socket.getOutputStream();
						PrintWriter out = new PrintWriter(outputStream, true);
						out.println(gson.toJson(usernameResponseDto));
						} catch (IOException e) {
							e.printStackTrace();
						}
						continue;	
						
					}
						userList.add(userName);
							
					ResponseDto responseDto = new ResponseDto(requestDto.getResource(), "ok", gson.toJson(null));
		               try {
		                  outputStream = socket.getOutputStream();
		                  PrintWriter out = new PrintWriter(outputStream, true);
		                  out.println(gson.toJson(responseDto));
		               } catch (IOException e) {
		                  e.printStackTrace();
		               }

//		               System.out.println(userName + "님이 접속하셨습니다.");
//						
//					List<String> connectedUsers = new ArrayList<>();
//					for(ConnectedSocket connectedSocket : socketList) {
//						connectedUsers.add(connectedSocket.getUserName());
//					}
//					JoinRespDto joinRespDto = new JoinRespDto(userName + "님의 방", connectedUsers);
//					System.out.println(joinRespDto);
//					sendToAll(requestDto.getResource(), "ok", gson.toJson(joinRespDto));
//					
					break;
				case "sendMessage" :
					MessageReqDto messageReqDto = gson.fromJson(requestDto.getBody(), MessageReqDto.class);
	
					if(messageReqDto.getToUser().equalsIgnoreCase("all")) {
						String message = messageReqDto.getFromUser() + "[전체] : " + messageReqDto.getMessageValue();
						MessageRespDto messageRespDto = new MessageRespDto(message);
						sendToAll(requestDto.getResource(), "ok", gson.toJson(messageRespDto));
					}else {
						String message = messageReqDto.getFromUser() + "[" + messageReqDto.getToUser() + "]: " + messageReqDto.getMessageValue();
						MessageRespDto messageRespDto = new MessageRespDto(message);
						sendToUser(requestDto.getResource(), "ok", gson.toJson(messageRespDto), messageReqDto.getToUser());
					}
					break;
				}
			}}catch(IOException e)
	{
			e.printStackTrace();
		}
	}

	private void sendToAll(String resource, String status, String body) throws IOException {
		ResponseDto responseDto = new ResponseDto(resource, status, body);
		for (ConnectedSocket connectedSocket : socketList) {
			OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);

			out.println(gson.toJson(responseDto));
		}
	}

	private void sendToUser(String resource, String status, String body, String toUser) throws IOException {
		ResponseDto responseDto = new ResponseDto(resource, status, body);
		for (ConnectedSocket connectedSocket : socketList) {
			if (connectedSocket.getUserName().equals(toUser) || connectedSocket.getUserName().equals(userName)) {
				OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
				PrintWriter out = new PrintWriter(outputStream, true);

				out.println(gson.toJson(responseDto));
			}
		}
	}
}

public class Server {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(9090);
			System.out.println("서버와 연결중....");

			while (true) {
				Socket socket = serverSocket.accept();
				ConnectedSocket connectedSocket = new ConnectedSocket(socket);
				connectedSocket.start();
				System.out.println("서버와 연결되었습니다.");
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.out.println("===서버와 연결 종료===");
		}

	}

}
