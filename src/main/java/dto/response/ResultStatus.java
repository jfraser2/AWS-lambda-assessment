package dto.response;

public class ResultStatus {
	
	protected String status;
	
	public ResultStatus() {
		
	}

	public ResultStatus(String status) {
		super();
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}
