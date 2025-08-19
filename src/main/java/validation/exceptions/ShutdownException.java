package validation.exceptions;

import helpers.RequestOrigin;

public class ShutdownException
	extends Exception
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = 2305399875677747137L;
	protected String requestStatus;
	protected RequestOrigin requestOrigin; //support CORS
	
	public ShutdownException(String exceptionText, String statusText, RequestOrigin requestOrigin) {
		super(exceptionText);
		this.requestStatus = statusText;
		this.requestOrigin = requestOrigin;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String status) {
		this.requestStatus = status;
	}

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}
	
}
