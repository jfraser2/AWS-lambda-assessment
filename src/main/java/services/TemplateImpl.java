package services;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dao.TemplateDao;
import dto.request.CreateTemplate;
import entities.TemplateEntity;
import services.interfaces.Template;

public class TemplateImpl
	implements Template
{
	private final Session session;
	private final TemplateDao templateDao;
	
	public TemplateImpl(SessionFactory sessionFactory) {
		this.templateDao = new TemplateDao();
		this.session = sessionFactory.openSession();
	}
	
	@Override
	public TemplateEntity findById(Long templateId) {
		TemplateEntity retVar = null;
		
		Optional<TemplateEntity> usne = templateDao.findById(this.session, templateId);
		if (usne.isPresent())
			retVar = usne.get();
		
		return retVar;
	}

	@Override
	public List<TemplateEntity> findAll() {
		List<TemplateEntity> retVar = null;
		
		List<TemplateEntity> usne = templateDao.findAll(this.session);
		if (null != usne)
			retVar = usne;
		
		return retVar;
	}
	

	@Override
	public TemplateEntity persistData(TemplateEntity templateEntity) {
		
		TemplateEntity retVar = null;
		
		try {
			if (null != templateEntity) {
				templateDao.save(this.session, templateEntity);
				retVar = templateEntity;
			}	
		} catch (Exception e) {
			retVar = null;
		}
		
		return retVar;
	}
	
	@Override
	public TemplateEntity mergeData(TemplateEntity templateEntity) {
		
		TemplateEntity retVar = null;
		
		try {
			if (null != templateEntity) {
				templateDao.merge(this.session, templateEntity);
				retVar = templateEntity;
			}	
		} catch (Exception e) {
			retVar = null;
		}
		
		return retVar;
	}

	@Override
	public TemplateEntity buildTemplateEntity(CreateTemplate createTemplateRequest) {
		
		TemplateEntity retVar = null;
		
		try {
			String templateBody = createTemplateRequest.getTemplateText();
			if (null != templateBody && templateBody.length() > 0)
			{
				retVar = new TemplateEntity();
				retVar.setBody(templateBody);
			}
		} catch (Exception e) {
			retVar = null;
		}
		
		return retVar;
	}

}
