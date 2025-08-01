package helpers;

public class RequestOrigin {
	
	protected String origin = null;
	protected Boolean fromSwagger = false;
	
	public RequestOrigin() {
		
	}
	
	public RequestOrigin(String origin) {
		this.origin = origin;
		this.fromSwagger = false;
	}
	
	public RequestOrigin(String origin, Boolean fromSwagger) {
		this.origin = origin;
		this.fromSwagger = fromSwagger;
	}

	public String getOrigin() {
		return this.origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Boolean isFromSwagger() {
		return fromSwagger;
	}

	public void setFromSwagger(Boolean fromSwagger) {
		this.fromSwagger = fromSwagger;
	}

}
