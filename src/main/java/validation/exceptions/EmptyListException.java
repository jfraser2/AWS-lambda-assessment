package validation.exceptions;

import helpers.RequestOrigin;

public class EmptyListException
	extends Exception
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1896610476358234443L;
	protected RequestOrigin requestOrigin; //support CORS

	public EmptyListException(String exceptionText, RequestOrigin requestOrigin) {
		super(exceptionText);
		this.requestOrigin = requestOrigin;
	}

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

}
