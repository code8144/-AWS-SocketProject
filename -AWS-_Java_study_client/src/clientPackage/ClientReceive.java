package clientPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import dto.JoinRespDto;
import dto.MessageReqDto;
import dto.ResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class ClientReceive extends Thread {
	
	private final Socket socket;
	private InputStream inputStream;
	private Gson gson;
	
	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			gson = new Gson();
				
			while(true) {
				String request = in.readLine();
				//
				System.out.println("test" + request);
				ResponseDto responseDto = gson.fromJson(request, ResponseDto.class);
				switch(responseDto.getResource()) {
				case "joinSuccess" :
					Client.getInstance().getMainCard().show(Client.getInstance().getMainPanel(), "chatListPanel");
					break;
				case "reflashRoom" :
					List<String> roomNameList = gson.fromJson(responseDto.getBody(), List.class);
					Client.getInstance().getRoomListModel().clear();
	                Client.getInstance().getRoomListModel().addAll(roomNameList);
					break;
				case "usernameError" :
					String errorMessage = responseDto.getBody();
	                 
					JOptionPane.showMessageDialog(null, errorMessage, "카카오톡 알림", JOptionPane.ERROR_MESSAGE);

					break;
//					JoinRespDto joinRespDto = gson.fromJson(responseDto.getBody(), JoinRespDto.class);
//					Client.getInstance().getRoomListModel().clear();
//					Client.getInstance().getRoomListModel().addElement("=== 방 목록 ===");
//					Client.getInstance().getRoomListModel().addAll(joinRespDto.getConnectedUsers());
//					Client.getInstance().getRoomList().setSelectedIndex(0);
//					break;
				
				case "createSuccess" :
					String roomName = responseDto.getBody();
					Client.getInstance().getRoomTitle().setText(roomName);
					Client.getInstance().getMainCard().show(Client.getInstance().getMainPanel(), "chatPanel");
					
					break;
				
				case "sendMessage" :
					MessageReqDto messageReqDto = gson.fromJson(responseDto.getBody(), MessageReqDto.class);
					Client.getInstance().getChattingResult().append(messageReqDto.getMessageValue() + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
