package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	
	private SessionFactory sessionFactory = createSessionFactory();	
	private NotificationDao notificationDao;
	private TemplateDao templateDao;
	
	public NotificationImpl() {
		this.notificationDao = new NotificationDao();
		this.templateDao = new TemplateDao();
	}
	
	public NotificationEntity findById(Long notificationId) {
		NotificationEntity retVar = null;
		
		Optional<NotificationEntity> usne = notificationDao.findById(notificationId);
		if (usne.isPresent())
			retVar = usne.get();
		
		return retVar;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<NotificationEntity> findAll() {
		List<NotificationEntity> retVar = null;
		
		List<NotificationEntity> usne = notificationRepository.findAll();
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
	@Transactional(propagation = Propagation.REQUIRED)
	public NotificationEntity persistData(NotificationEntity notificationEntity) {
		
		NotificationEntity retVar = null;
		
		try {
			if (null != notificationEntity) {
				retVar = notificationRepository.save(notificationEntity);
			}	
		} catch (Exception e) {
//			System.out.println("Exception is: " + e.getMessage());
			retVar = null;
		}
		
		return retVar;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public NotificationEntity buildNotificationEntity(CreateNotification createNotificationRequest)
		throws BuildNotificationException
	{
		
		NotificationEntity retVar = null;
		TemplateEntity templateEntity = null;
		
		String templateId = createNotificationRequest.getTemplateId();
		if (null != templateId && templateId.length() > 0)
		{
			Long tempId = Long.valueOf(templateId);
			Optional<TemplateEntity> te = templateRepository.findById(tempId);
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
				templateEntity = templateRepository.save(tempEntity);
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
