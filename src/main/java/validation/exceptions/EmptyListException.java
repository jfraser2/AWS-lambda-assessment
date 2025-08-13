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
	protected String className;

	public EmptyListException(String exceptionText, RequestOrigin requestOrigin, String className) {
		super(exceptionText);
		this.requestOrigin = requestOrigin;
		this.className = className;
	}

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
}
