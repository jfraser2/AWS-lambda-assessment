package validation.exceptions;

import helpers.RequestOrigin;

public class DatabaseRowNotFoundException
	extends Exception
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = -5601843022086989457L;
	protected Long rowId;
	protected RequestOrigin requestOrigin; //support CORS
	
	public DatabaseRowNotFoundException(String exceptionText, RequestOrigin requestOrigin) {
		super(exceptionText);
		this.rowId = null;
		this.requestOrigin = requestOrigin;
	}

	public DatabaseRowNotFoundException(String exceptionText, Long rowId, RequestOrigin requestOrigin) {
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
