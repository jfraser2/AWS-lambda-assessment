package validation.exceptions;

import helpers.RequestOrigin;

public class OptimisticLockingException
	extends Exception
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = -7463445834923143603L;
	protected Long rowId;
	protected RequestOrigin requestOrigin; //support CORS

	public OptimisticLockingException(String exceptionText, RequestOrigin requestOrigin) {
		super(exceptionText);
		this.rowId = null;
		this.requestOrigin = requestOrigin;
	}

	public OptimisticLockingException(String exceptionText, Long rowId, RequestOrigin requestOrigin) {
		super(exceptionText);
		this.rowId = rowId;
		this.requestOrigin = requestOrigin;
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public RequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(RequestOrigin requestOrigin) {
		this.requestOrigin = requestOrigin;
	}
	
}
