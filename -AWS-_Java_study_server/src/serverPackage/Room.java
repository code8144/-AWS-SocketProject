package serverPackage;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
@Getter
@Data
public class Room {
	private String roomName;
	private String kingName;
	private List<ConnectedSocket> users;
	
	public Room(String roomName, String kingName) {
		this.roomName = roomName;
		this.kingName = kingName;
		this.users = new ArrayList<>();
	}
}
