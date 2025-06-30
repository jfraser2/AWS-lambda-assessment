package validation.exceptions;

import java.util.List;

import errorHandling.helpers.ApiValidationError;

public class RequestValidationException
	extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3967697713578441774L;
	private List<ApiValidationError> subErrorList;
	private String requestOrigin; //support CORS
	
	public RequestValidationException(List<ApiValidationError> requestErrorList, String requestOrigin)
	{
		super();
		this.subErrorList = requestErrorList;
		this.requestOrigin = requestOrigin;
	}

	public List<ApiValidationError> getSubErrorList() {
		return this.subErrorList;
	}

	public String getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(String requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

}
