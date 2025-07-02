package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dao.NotificationDao;
import dao.TemplateDao;
import dto.request.CreateNotification;
import validation.exceptions.BuildNotificationException;
import entities.NotificationEntity;
import entities.TemplateEntity;
import errorHandling.helpers.ApiValidationError;
import services.interfaces.Notification;

public class NotificationImpl
	implements Notification
{
	private static final String SUBSTITUTION_TEXT = "(personal)";
	private static final String REGEX_SUBSTITUTION_TEXT = "\\(personal\\)";
	
	private final Session session;
	
	private final NotificationDao notificationDao;
	private final TemplateDao templateDao;
	
	public NotificationImpl(SessionFactory sessionFactory) {
		this.notificationDao = new NotificationDao();
		this.templateDao = new TemplateDao();
		this.session = sessionFactory.openSession();
	}
	
	public NotificationEntity findById(Long notificationId) {
		NotificationEntity retVar = null;
		
		Optional<NotificationEntity> usne = notificationDao.findById(this.session, notificationId);
		if (usne.isPresent())
			retVar = usne.get();
		
		return retVar;
	}

	@Override
	public List<NotificationEntity> findAll() {
		List<NotificationEntity> retVar = null;
		
		List<NotificationEntity> usne = notificationDao.findAll(this.session);
		if (null != usne)
			retVar = usne;
		
		return retVar;
	}
	

	@Override
	public String generatePersonalization(NotificationEntity notificationEntity) {
		String retVar = null;
		
		if (null != notificationEntity && null != notificationEntity.getPersonalization() &&
			notificationEntity.getPersonalization().length() > 0) {
			
			if (notificationEntity.getTemplateEntity().getBody().contains(SUBSTITUTION_TEXT)) {
				
				retVar = notificationEntity.getTemplateEntity().getBody().replaceAll(REGEX_SUBSTITUTION_TEXT,
					notificationEntity.getPersonalization());
			} else {
				retVar = notificationEntity.getTemplateEntity().getBody();
			}
			
		}
		
		return retVar;
	}

	@Override
	public NotificationEntity persistData(NotificationEntity notificationEntity) {
		
		NotificationEntity retVar = null;
		
		try {
			if (null != notificationEntity) {
				notificationDao.save(this.session, notificationEntity);
				retVar = notificationEntity;
			}	
		} catch (Exception e) {
//			System.out.println("Exception is: " + e.getMessage());
			retVar = null;
		}
		
		return retVar;
	}

	@Override
	public NotificationEntity buildNotificationEntity(CreateNotification createNotificationRequest)
		throws BuildNotificationException
	{
		
		NotificationEntity retVar = null;
		TemplateEntity templateEntity = null;
		
		String templateId = createNotificationRequest.getTemplateId();
		if (null != templateId && templateId.length() > 0)
		{
			Long tempId = Long.valueOf(templateId);
			Optional<TemplateEntity> te = templateDao.findById(this.session, tempId);
			if (te.isPresent()) {
				templateEntity = te.get();
			} else {
				throw new BuildNotificationException("The Template for Id: " + templateId + " does not exist.");
			}
		} else {
			String tempVar = createNotificationRequest.getTemplateText();
			if (null != tempVar && tempVar.length() > 0)
			{	
				TemplateEntity tempEntity = new TemplateEntity();
				tempEntity.setBody(createNotificationRequest.getTemplateText());
				templateDao.save(this.session, tempEntity);
			}	
		}
		
		if (null != templateEntity) {
			retVar = new NotificationEntity();
			retVar.setPhoneNumber(createNotificationRequest.getPhoneNumber());
			retVar.setPersonalization(createNotificationRequest.getPersonalization());
			retVar.setTemplateEntity(templateEntity);
		}
		
		return retVar;
	}
	
	@Override
	public boolean validateTemplateFields(CreateNotification createNotificationRequest) {
		
		boolean retVar = false;
		
		String templateId = createNotificationRequest.getTemplateId();
		if (null != templateId && templateId.length() > 0)
		{
			retVar = true;
		} else {
			String tempVar = createNotificationRequest.getTemplateText();
			if (null != tempVar && tempVar.length() > 0)
			{
				retVar = true;
			}	
		}
		
		return retVar;
	}

	@Override
	public List<ApiValidationError> generateTemplateFieldsError(CreateNotification createNotificationRequest) {
		
		List<ApiValidationError> retVar = new ArrayList<>();
		
		String objectName = createNotificationRequest.getClass().getSimpleName();
		String message = "Either the templateId or the templateText must be populated.";
		
		ApiValidationError theError = new ApiValidationError(objectName, message);
		retVar.add(theError);
		
		return retVar;
	}
	
}
