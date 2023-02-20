package dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class JoinRespDto {
	private String welcomeMessage;
	private List<String> connectedUsers;
}