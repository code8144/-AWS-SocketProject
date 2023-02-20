package dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class JoinRespDto {
	private List<String> connectedUsers;
}