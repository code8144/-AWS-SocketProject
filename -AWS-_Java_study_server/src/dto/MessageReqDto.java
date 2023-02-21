package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class MessageReqDto {
	private String toUser;
	private String fromUser;
	private String messageValue;

}
