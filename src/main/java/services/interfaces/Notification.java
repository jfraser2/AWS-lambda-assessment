package services.interfaces;

import java.util.List;

import dto.request.CreateNotification;
import validation.exceptions.BuildNotificationException;
import entities.NotificationEntity;
import errorHandling.helpers.ApiValidationError;

public interface Notification {
	
	public NotificationEntity findById(Long id);
	public List<NotificationEntity> findAll();
	
	public String generatePersonalization(NotificationEntity notificationEntity);
	
	public boolean validateTemplateFields(CreateNotification createNotificationRequest);
	public NotificationEntity buildNotificationEntity(CreateNotification createNotificationRequest) throws BuildNotificationException;
	public NotificationEntity persistData(NotificationEntity notificationEntity);
	
	public List<ApiValidationError> generateTemplateFieldsError(CreateNotification createNotificationRequest);
}
