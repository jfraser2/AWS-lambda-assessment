package validation.exceptions;

import helpers.RequestOrigin;

public class ShutdownException
	extends Exception
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = 2305399875677747137L;
	protected String status;
	protected RequestOrigin requestOrigin; //support CORS
	
	public ShutdownException(String exceptionText, String statusText, RequestOrigin requestOrigin) {
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

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}
	
}
