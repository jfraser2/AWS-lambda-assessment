package validation.exceptions;

public class DatabaseRowNotFoundException
	extends Exception
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = -5601843022086989457L;
	protected Long rowId;
	protected String requestOrigin; //support CORS
	
	public DatabaseRowNotFoundException(String exceptionText, String requestOrigin) {
		super(exceptionText);
		this.rowId = null;
		this.requestOrigin = requestOrigin;
	}

	public DatabaseRowNotFoundException(String exceptionText, Long rowId, String requestOrigin) {
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

	public String getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(String requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

}
