package services.interfaces;

import java.util.List;

import dto.request.CreateTemplate;
import entities.TemplateEntity;

public interface Template {
	
	public TemplateEntity findById(Long id);
	public List<TemplateEntity> findAll();
	
	public TemplateEntity buildTemplateEntity(CreateTemplate createTemplateRequest);
	public TemplateEntity persistData(TemplateEntity templateEntity);

}
