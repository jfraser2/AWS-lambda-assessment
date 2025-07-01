package request.handlers;

import java.util.List;

import org.hibernate.SessionFactory;

import com.amazonaws.lambda.thirdparty.com.google.gson.Gson;
import com.amazonaws.lambda.thirdparty.com.google.gson.GsonBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.request.CreateNotification;
import helpers.StringBuilderContainer;
import helpers.ValidationErrorContainer;
import hibenate.utils.HibernateUtil;
import software.amazon.awssdk.http.HttpStatusCode;
//import software.amazon.awssdk.utils.Validate;
import software.amazon.lambda.powertools.validation.ValidationConfig;
import validation.exceptions.BuildNotificationException;
import validation.exceptions.DatabaseRowNotFoundException;
import entities.NotificationEntity;
import services.NotificationImpl;
import services.interfaces.Notification;
import services.interfaces.RequestValidation;
import services.validation.request.RequestValidationService;
import validation.exceptions.RequestValidationException;
import errorHandling.helpers.ApiValidationError;

public class CreateNotificationHandler
	extends RequestHandlerBase
	implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> 
{
	static ObjectMapper mapper;
	static Gson gsonWithSerializeNullsAndPrettyPrint;
	static Gson gsonWithSerializeNulls;
	static Notification notificationService;
	static SessionFactory sessionFactory;
	
	static {
		
//		default is V7 can be changed
//		ValidationConfig.get().setSchemaVersion(SpecVersion.VersionFlag.V4);
		
	    // update (de)serializationConfig or other properties
	    mapper = ValidationConfig.get().getObjectMapper();
	    
	    gsonWithSerializeNullsAndPrettyPrint = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	    gsonWithSerializeNulls = new GsonBuilder().serializeNulls().create();
	    sessionFactory = HibernateUtil.getSessionFactory();
	    
	    notificationService = new NotificationImpl(sessionFactory); //one per Class
	    
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		String requestOrigin = getRequestOrigin(input);
		StringBuilderContainer stringBuilderContainer = new StringBuilderContainer(); // request Scope
		ValidationErrorContainer requestValidationErrorsContainer = new ValidationErrorContainer(); //request Scope
		RequestValidation<CreateNotification> createNotificationValidation = 
				new RequestValidationService<CreateNotification>(); // request Scope
		
		APIGatewayProxyResponseEvent retVar = null;
		CreateNotification createNotification = (CreateNotification) convertJsonToObject(input.getBody(), mapper, CreateNotification.class);
		
		// single field validation
		createNotificationValidation.validateRequest(createNotification, requestValidationErrorsContainer, null);
		List<ApiValidationError> errorList = requestValidationErrorsContainer.getValidationErrorList();
		
		if (errorList.size() > 0)
		{
//			System.out.println("Right before the throw");
			retVar = handleRequestValidationException(new RequestValidationException(errorList, requestOrigin), mapper);
		} else if (!notificationService.validateTemplateFields(createNotification)) { // multiple field validation
			List<ApiValidationError> templateFieldsError = notificationService.generateTemplateFieldsError(createNotification);
			retVar = handleRequestValidationException(new RequestValidationException(templateFieldsError, requestOrigin), mapper);
		}
		
		NotificationEntity ne = null;
		boolean buildOk = true;
		String failureMessage = null;
		
		try {
			ne = notificationService.buildNotificationEntity(createNotification);
		} catch (BuildNotificationException bne) {
			buildOk = false;
			failureMessage = bne.getMessage();
		}
		
		if (!buildOk) {
			retVar = handleDatabaseRowNotFoundException(new DatabaseRowNotFoundException(failureMessage, requestOrigin), mapper);
		} else {
			String jsonString = goodResponse(ne, stringBuilderContainer, null, mapper);
			// support CORS
			retVar = new APIGatewayProxyResponseEvent();
			retVar.setHeaders(createResponseHeader(input));
			retVar.setStatusCode(HttpStatusCode.CREATED);
			retVar.setBody(jsonString);
		}
		
		
		stringBuilderContainer.onDestroy();
		requestValidationErrorsContainer.onDestroy();
		
		return retVar;
	}	
	

}
