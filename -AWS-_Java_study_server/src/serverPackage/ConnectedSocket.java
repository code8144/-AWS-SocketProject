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
import dto.MessageReqDto;
import dto.MessageRespDto;
import dto.RequestDto;
import dto.ResponseDto;
import lombok.Data;

@Data
public class ConnectedSocket extends Thread {
	private static List<Room> rooms = new ArrayList<>(); // List<Room>은 Room 객체를 담을 수 있는 List를 사용한 것
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
			inputStream = socket.getInputStream(); // inputStream 열어줌
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			while (true) {
				System.out.println("요청 기다림");
				String request = in.readLine(); // 들어온 소켓(requestDtoJson)을 request에 저장
				RequestDto requestDto = gson.fromJson(request, RequestDto.class); // request를 원래 형태(RequestDto)로 변환후
																					// requestDto 에 저장
				System.out.println(requestDto);

				switch (requestDto.getResource()) { // requestDto의 Resource를 가져와서 case별로 실행
				case "join":
					// 리소스가 join일 경우 바디는 userName이고, joinReqDto에 userName 저장
					JoinReqDto joinReqDto = gson.fromJson(requestDto.getBody(), JoinReqDto.class);

					List<String> usernameList = new ArrayList<>();
					// 람다식으로 forEach돌려서 connectedSocket에 있는 userName을 들고와서 usernameList에다 넣어줌
					socketList.forEach(connectedSocket -> {
						usernameList.add(connectedSocket.getUserName());
					});
					// userName 오류검사
					if (joinReqDto.getUsername() == null || joinReqDto.getUsername().isEmpty()
							|| usernameList.contains(joinReqDto.getUsername())) {
						ResponseDto usernameErrorResponseDto = new ResponseDto("usernameError", "no",
								"이미 존재하는 사용자명입니다.");
						sendToMe(usernameErrorResponseDto);
						continue;
					}

					userName = joinReqDto.getUsername(); // 오류검사에 걸리지 않으면 가져온 joinReqDto의 userName을 connectedSocket의
															// userName에 넣어줌

					// ResponseDto의 resource(joinSuccess), status("ok"), body(접속 성공!)에 저장후,
					// joinSuccessResponseDto에 넣어줌
					ResponseDto joinSuccessResponseDto = new ResponseDto("joinSuccess", "ok", "접속 성공");
					sendToMe(joinSuccessResponseDto); // sendToMe 메소드 사용하여 본인에게 메세지 보냄

					reflashRoomList(); // 방 리스트 현황정보 보여줌
					System.out.println("조인 끝남");
					break;

				case "create":
					String roomName = requestDto.getBody(); // roomName에 requestDto.getBody()를 넣음
					Room room = new Room(roomName, userName );// Room 객체를 생성하여 매개변수에 roomName(requestDto.getBody())과
																// kingName(userName)을 넣음
					rooms.add(room); // room을 rooms의 List로 넣음
					ResponseDto createSuccessResponseDto = new ResponseDto("createSuccess", "ok", room.getRoomName());
					sendToMe(createSuccessResponseDto); // sendToMe에 매개변수 responseDto대신 createSuccessResponseDto를 넣어서
														// outputStream해줌
					reflashRoomList();
					break;

				case "joinRoom":
					System.out.println(requestDto.getBody());
					String selectRoomName = requestDto.getBody();
					for (Room r : rooms) {
						if (r.getRoomName().equals(selectRoomName)) {
							r.getUsers().add(this);

							break;
						}
					}

					ResponseDto joinRoomResponseDto = new ResponseDto("joinRoomSuccess", "ok", selectRoomName);
					sendToMe(joinRoomResponseDto);
					break;
					
				case "sendMessage":
					MessageReqDto messageReqDto = gson.fromJson(requestDto.getBody(), MessageReqDto.class);
					ResponseDto responseDto;
					if (messageReqDto.getToUser().equalsIgnoreCase("all")) {
						String message = messageReqDto.getFromUser() + " : " + messageReqDto.getMessageValue();
						MessageRespDto messageRespDto = new MessageRespDto(message);
						for (Room r : rooms) {
							responseDto = new ResponseDto(requestDto.getResource(), "ok", gson.toJson(messageRespDto));
							sendToAll(responseDto, r.getUsers());
						}
					} else {
					 String message = messageReqDto.getFromUser() + "[" +
					 messageReqDto.getToUser() + "]: " + messageReqDto.getMessageValue();
					 MessageRespDto messageRespDto = new MessageRespDto(message);
					 
					 messageReqDto.getToUser();
					 }
					
					break;

				//
				// //roomName 중복검사
				// if(roomName == null || roomName.isEmpty() || roomList.contains(roomName)) {
				// System.out.println("이미 존재하는 방 입니다.");
				// ResponseDto roomResponseDto = new ResponseDto(requestDto.getResource(), "no",
				// null);
				// try {
				// outputStream = socket.getOutputStream();
				// PrintWriter out = new PrintWriter(outputStream, true);
				// out.println(gson.toJson(roomResponseDto));
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// continue;

				}
				// case "sendMessage" :
				// MessageReqDto messageReqDto = gson.fromJson(requestDto.getBody(),
				// MessageReqDto.class);
				//
				// if(messageReqDto.getToUser().equalsIgnoreCase("all")) {
				// String message = messageReqDto.getFromUser() + "[전체] : " +
				// messageReqDto.getMessageValue();
				// MessageRespDto messageRespDto = new MessageRespDto(message);
				// sendToAll(requestDto.getResource(), "ok", gson.toJson(messageRespDto));
				// }else {
				// String message = messageReqDto.getFromUser() + "[" +
				// messageReqDto.getToUser() + "]: " + messageReqDto.getMessageValue();
				// MessageRespDto messageRespDto = new MessageRespDto(message);
				// sendToUser(requestDto.getResource(), "ok", gson.toJson(messageRespDto),
				// messageReqDto.getToUser());
				// }
				// break;
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reflashRoomList() {
		List<String> roomNameList = new ArrayList<>();
		rooms.forEach(room -> { // 람다식으로 forEach문을 사용하여 room의 roomName을 roomNameList에 넣어줌
			roomNameList.add(room.getRoomName());
		});
		// responseDto의 resource(reflashRoom), status(ok),
		// body(gson.toJson(roomNameList))를 넣음
		ResponseDto responseDto = new ResponseDto("reflashRoom", "ok", gson.toJson(roomNameList));
		sendToAll(responseDto, socketList); // sendToAll 메소드를 사용하여 responseDto를 socketList안에 있는 사용자들한테 보내줌
	}

	private void sendToMe(ResponseDto responseDto) { // 로그인 성공, 방 생성 등의 본인에게만 띄워주는 정보를 보내줌
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			out.println(gson.toJson(responseDto)); // 메소드에 변수로 들어온 ResponseDto 형태의 객체를 클라이언트로 보내줌(Client Receive에서 받음)
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ResponseDto 객체를 JSON 형태로 변환하여, 주어진 ConnectedSocket 목록에 속한 소켓들에게 해당 JSON 데이터를
	// 전송하는 기능을 수행하는 메소드
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
