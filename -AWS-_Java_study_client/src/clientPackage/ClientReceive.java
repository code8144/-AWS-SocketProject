package clientPackage;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import com.google.gson.Gson;

import dto.JoinRespDto;
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
				System.out.println(request);
				ResponseDto responseDto = gson.fromJson(request, ResponseDto.class);
				switch(responseDto.getResource()) {
				case "join" :
					JoinRespDto joinRespDto = gson.fromJson(responseDto.getBody(), JoinRespDto.class);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
