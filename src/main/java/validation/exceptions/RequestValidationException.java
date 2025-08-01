package validation.exceptions;

import java.util.List;

import errorHandling.helpers.ApiValidationError;
import helpers.RequestOrigin;

public class RequestValidationException
	extends Exception
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = -3967697713578441774L;
	protected List<ApiValidationError> subErrorList;
	protected RequestOrigin requestOrigin; //support CORS
	
	public RequestValidationException(List<ApiValidationError> requestErrorList, RequestOrigin requestOrigin)
	{
		super();
		this.subErrorList = requestErrorList;
		this.requestOrigin = requestOrigin;
	}

	public List<ApiValidationError> getSubErrorList() {
		return this.subErrorList;
	}

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

}
