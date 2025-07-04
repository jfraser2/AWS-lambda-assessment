package dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTemplate {
	@NotBlank(message = "The template text must not be blank")
	@Size(max = 200, message="Max Length is 200 characters")
	protected String templateText;
	
	public CreateTemplate()
	{
	}
	
	public CreateTemplate(String templateText)
	{
		this.templateText = templateText;
	}

	public String getTemplateText() {
		return templateText;
	}

	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}
	
}
