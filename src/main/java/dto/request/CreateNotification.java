package dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateNotification {
	@NotBlank(message = "Notification phone number must not be blank")
	@Pattern(regexp = "^\\d{3}[- .]?\\d{3}[- .]?\\d{4}$", message = "Invalid Phone Number Format.")
	@Size(max = 20, message="Max Length is 20 characters")
	protected String phoneNumber;
	
	@Size(min = 0, max = 25, message="Max Length is 25 characters")
    protected String personalization;
	
	@Pattern(regexp = "[0-9]*", message = "Invalid Template Id. Only Numbers allowed")
	protected String templateId;
	
	@Size(min = 0, max = 200, message="Max Length is 200 characters")
	protected String templateText;
	
    
    public CreateNotification()
    {
    }


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getPersonalization() {
		return personalization;
	}


	public void setPersonalization(String personalization) {
		this.personalization = personalization;
	}


	public String getTemplateId() {
		return templateId;
	}


	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}


	public String getTemplateText() {
		return templateText;
	}


	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}
    

    
}

