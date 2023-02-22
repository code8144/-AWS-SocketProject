package serverPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import dto.JoinReqDto;
import dto.RequestDto;
import dto.ResponseDto;
import dto.RoomReqDto;
import lombok.Data;

@Data
public class ConnectedSocket extends Thread {
	private static List<Room> rooms = new ArrayList<>();
	private static List<ConnectedSocket> socketList = new ArrayList<>();
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Gson gson;

	private String userName;
	private String roomName;

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
				System.out.println("요청 기다림");
				String request = in.readLine();
				RequestDto requestDto = gson.fromJson(request, RequestDto.class);
				System.out.println(requestDto);
				
				switch(requestDto.getResource()) {
					case "join" : 
						JoinReqDto joinReqDto = gson.fromJson(requestDto.getBody(), JoinReqDto.class);
						
						List<String> usernameList = new ArrayList<>();
						socketList.forEach(connectedSocket -> {
							usernameList.add(connectedSocket.getUserName());
						});
						//userName 중복검사
						if(joinReqDto.getUsername() == null || joinReqDto.getUsername().isEmpty() || usernameList.contains(joinReqDto.getUsername())) {
							ResponseDto usernameErrorResponseDto = new ResponseDto("usernameError", "no", "이미 존재하는 사용자명입니다.");
							sendToMe(usernameErrorResponseDto);
							continue;
						}
						
						userName = joinReqDto.getUsername();
						
						ResponseDto joinSuccessResponseDto = new ResponseDto("joinSuccess", "ok", "접속 성공!");
						sendToMe(joinSuccessResponseDto);
						
						reflashRoomList();
						System.out.println("조인 끝남");
						break;
						
					case "create" : 
						String roomName = requestDto.getBody();
						Room room = new Room(roomName, userName);
						rooms.add(room);
						ResponseDto createSuccessResponseDto = new ResponseDto("createSuccess", "ok", room.getRoomName());
						sendToMe(createSuccessResponseDto);
						reflashRoomList();
						break;
	//					
	//					//roomName 중복검사
	//					if(roomName == null || roomName.isEmpty() || roomList.contains(roomName)) {
	//						System.out.println("이미 존재하는 방 입니다.");
	//						ResponseDto roomResponseDto = new ResponseDto(requestDto.getResource(), "no", null);
	//						try {
	//							outputStream = socket.getOutputStream();
	//							PrintWriter out = new PrintWriter(outputStream, true);
	//							out.println(gson.toJson(roomResponseDto));
	//						} catch (IOException e) {
	//							e.printStackTrace();
	//						}
	//						continue;	
							
						}
	//				case "sendMessage" :
	//					MessageReqDto messageReqDto = gson.fromJson(requestDto.getBody(), MessageReqDto.class);
	//				
	//					if(messageReqDto.getToUser().equalsIgnoreCase("all")) {
	//						String message = messageReqDto.getFromUser() + "[전체] : " + messageReqDto.getMessageValue();
	//						MessageRespDto messageRespDto = new MessageRespDto(message);
	//						sendToAll(requestDto.getResource(), "ok", gson.toJson(messageRespDto));
	//					}else {
	//						String message = messageReqDto.getFromUser() + "[" + messageReqDto.getToUser() + "]: " + messageReqDto.getMessageValue();
	//						MessageRespDto messageRespDto = new MessageRespDto(message);
	//						sendToUser(requestDto.getResource(), "ok", gson.toJson(messageRespDto), messageReqDto.getToUser());
	//					}
	//					break;
//				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void reflashRoomList() {
		List<String> roomNameList = new ArrayList<>();
		rooms.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});
		ResponseDto responseDto = new ResponseDto("reflashRoom", "ok", gson.toJson(roomNameList));
		sendToAll(responseDto, socketList);
	}
	
	private void sendToMe(ResponseDto responseDto) {
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			out.println(gson.toJson(responseDto));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendToAll(ResponseDto responseDto, List<ConnectedSocket> connectedSockets) {
		try {
			for (ConnectedSocket connectedSocket : connectedSockets) {
				OutputStream outputStream;
				outputStream = connectedSocket.getSocket().getOutputStream();
				PrintWriter out = new PrintWriter(outputStream, true);

				out.println(gson.toJson(responseDto));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
