package dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateTemplate {
	
	@NotBlank(message = "templateId must not be blank")
	@Pattern(regexp = "[0-9]+", message = "Invalid templateId. Only Numbers allowed")
	private String templateId;
	
	@NotBlank(message = "The new template text must not be blank")
	@Size(max = 200, message="Max Length is 200 characters")
	private String newTemplateText;
	
	public UpdateTemplate() {
		
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getNewTemplateText() {
		return newTemplateText;
	}

	public void setNewTemplateText(String newTemplateText) {
		this.newTemplateText = newTemplateText;
	}

}
