package errorHandling.helpers;

import enums.ZonedDateTimeEnum;
import java.time.ZonedDateTime;
import java.util.List;

//import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ApiError
{
	protected String requestStatus; //HttpStatus as Text
	   
	@JsonSerialize(using = ZonedDateTimeConverter.class)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss")
	protected ZonedDateTime timestamp;
	   
	protected String message;
	protected String debugMessage;
	@JsonSerialize(using = ListApiValidationErrorConverter.class)
	protected List<ApiValidationError> subErrors;

	public ApiError() {
	    ZonedDateTime zonedDateTime = ZonedDateTimeEnum.INSTANCE.now();
		setTimestamp(zonedDateTime);
	}

	public ApiError(String zoneIdName) {
	    ZonedDateTime zonedDateTime = ZonedDateTimeEnum.INSTANCE.now(zoneIdName);
		setTimestamp(zonedDateTime);
	}
	
	public String getRequestStatus() {
		return this.requestStatus;
	}

	public void setRequestStatus(String statusText) {
		this.requestStatus = statusText;
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

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}
	   
}
