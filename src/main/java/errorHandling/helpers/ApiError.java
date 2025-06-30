package errorHandling.helpers;

//import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

//import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ApiError
{
	private String status; //HttpStatus as Text
	   
	@JsonSerialize(using = DateConverter.class)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss")
	private Date timestamp;
	   
	private String message;
	private String debugMessage;
	@JsonSerialize(using = ListApiValidationErrorConverter.class)
	private List<ApiValidationError> subErrors;

	public ApiError() {
		setTimestamp(new Date());
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String statusText) {
		this.status = statusText;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public List<ApiValidationError> getSubErrors() {
		return subErrors;
	}

	public void setSubErrors(List<ApiValidationError> subErrors) {
		this.subErrors = subErrors;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	   
}
