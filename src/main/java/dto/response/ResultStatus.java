package dto.response;

public class ResultStatus {
	
	protected String requestStatus;
	
	public ResultStatus() {
		
	}

	public ResultStatus(String status) {
		super();
		this.requestStatus = status;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String status) {
		this.requestStatus = status;
	}

	
}
