package errorHandling.helpers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

//import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ApiError
{
	protected String status; //HttpStatus as Text
	   
	@JsonSerialize(using = ZonedDateTimeConverter.class)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss")
	protected ZonedDateTime timestamp;
	   
	protected String message;
	protected String debugMessage;
	@JsonSerialize(using = ListApiValidationErrorConverter.class)
	protected List<ApiValidationError> subErrors;

	public ApiError() {
	    Instant instant = Instant.now(); // Current instant from London(Greenwich)
	    ZoneId zoneId = ZoneId.of("America/Chicago");
	    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
		setTimestamp(zonedDateTime);
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

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}
	   
}
