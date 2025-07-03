package validation.exceptions;

public class ShutdownException
	extends Exception
{
	protected String status;
	protected String requestOrigin; //support CORS
	
	public ShutdownException(String exceptionText, String statusText, String requestOrigin) {
		super(exceptionText);
		this.status = statusText;
		this.requestOrigin = requestOrigin;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(String requestOrigin) {
		this.requestOrigin = requestOrigin;
	}
	
}
