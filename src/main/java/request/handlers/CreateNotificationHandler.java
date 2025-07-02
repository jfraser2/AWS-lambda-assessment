package request.handlers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dto.request.CreateNotification;
import dto.request.GetById;
import helpers.StringBuilderContainer;
import helpers.ValidationErrorContainer;
import hibenate.utils.HibernateUtil;
import software.amazon.awssdk.http.HttpStatusCode;
//import software.amazon.awssdk.utils.Validate;
import dto.response.NonModelAdditionalFields;
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
	    mapper = new ObjectMapper();
	    
	    gsonWithSerializeNullsAndPrettyPrint = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	    gsonWithSerializeNulls = new GsonBuilder().serializeNulls().create();
	    sessionFactory = HibernateUtil.getSessionFactory();
	    
	    notificationService = new NotificationImpl(sessionFactory); //one per Class
	    
	    Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("[runtime] ShutdownHook triggered");

                System.out.println("[runtime] Cleaning up");
                // perform actual clean up work here.
                try {
                	HibernateUtil.shutdown();
                    Thread.sleep(200);
                 } catch (Exception e) {
                    System.out.println(e);
                 }

                System.out.println("[runtime] exiting");
                System.exit(0);
            }
        });	    
	    
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		String requestOrigin = getRequestOrigin(input);
		StringBuilderContainer stringBuilderContainer = new StringBuilderContainer(); // request Scope
		ValidationErrorContainer requestValidationErrorsContainer = new ValidationErrorContainer(); //request Scope
		
		APIGatewayProxyResponseEvent retVar = null;
		
		switch (input.getResource()) {
		    case "/v1/createNotification":
				retVar = creation(input, notificationService, requestValidationErrorsContainer,
					stringBuilderContainer, requestOrigin);
		        break;
		    case "/v1/all/notifications":
				retVar = findAll(input, notificationService, requestValidationErrorsContainer,
						stringBuilderContainer, requestOrigin);
		        break;
		    case "/v1/findByNotificationId/{id}":
				retVar = findById(input, notificationService, requestValidationErrorsContainer,
						stringBuilderContainer, requestOrigin);
		        break;
		}		
		
		stringBuilderContainer.onDestroy();
		requestValidationErrorsContainer.onDestroy();
		
		return retVar;
	}	
	
	protected APIGatewayProxyResponseEvent creation(APIGatewayProxyRequestEvent input, Notification notificationService,
		ValidationErrorContainer requestValidationErrorsContainer, StringBuilderContainer stringBuilderContainer,
		String requestOrigin)
	{
		APIGatewayProxyResponseEvent retVar = null;
		RequestValidation<CreateNotification> createNotificationValidation = 
				new RequestValidationService<CreateNotification>(); // request Scope
		
		CreateNotification createNotification = (CreateNotification) convertJsonToObject(input.getBody(), mapper, CreateNotification.class);
		
		// single field validation
		createNotificationValidation.validateRequest(createNotification, requestValidationErrorsContainer, null);
		List<ApiValidationError> errorList = requestValidationErrorsContainer.getValidationErrorList();
		
		boolean gotError = false;
		
		if (errorList.size() > 0)
		{
//			System.out.println("Right before the throw");
			retVar = handleRequestValidationException(new RequestValidationException(errorList, requestOrigin), mapper);
			gotError = true;
		} else if (!notificationService.validateTemplateFields(createNotification)) { // multiple field validation
			List<ApiValidationError> templateFieldsError = notificationService.generateTemplateFieldsError(createNotification);
			retVar = handleRequestValidationException(new RequestValidationException(templateFieldsError, requestOrigin), mapper);
			gotError = true;
		}
		
		if (!gotError) {
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
		}
		
		return retVar;
	}

	protected APIGatewayProxyResponseEvent findAll(APIGatewayProxyRequestEvent input, Notification notificationService,
			ValidationErrorContainer requestValidationErrorsContainer, StringBuilderContainer stringBuilderContainer,
			String requestOrigin)
	{
		APIGatewayProxyResponseEvent retVar = null;

		List<NotificationEntity> aList = notificationService.findAll();
		boolean isEmpty = true;
		if(null != aList && aList.size() > 0) {
			isEmpty = false;
		}
		
		if(isEmpty) {
			retVar = handleDatabaseRowNotFoundException(new DatabaseRowNotFoundException("Notification Table is empty.", requestOrigin), mapper);
		} else {
			List<Object> objectList = new ArrayList<Object>(aList);
			String jsonString = goodResponseList(objectList, stringBuilderContainer, gsonWithSerializeNullsAndPrettyPrint, mapper);
			
			// support CORS
			retVar = new APIGatewayProxyResponseEvent();
			retVar.setHeaders(createResponseHeader(input));
			retVar.setStatusCode(HttpStatusCode.OK);
			retVar.setBody(jsonString);
		}
		
		return retVar;
	}
	
	protected APIGatewayProxyResponseEvent findById(APIGatewayProxyRequestEvent input, Notification notificationService,
			ValidationErrorContainer requestValidationErrorsContainer, StringBuilderContainer stringBuilderContainer,
			String requestOrigin)
	{
		APIGatewayProxyResponseEvent retVar = null;
		RequestValidation<GetById> getByIdValidation = 
				new RequestValidationService<GetById>(); // request Scope
		
		String notificationId = input.getPathParameters().get("id");
		GetById data = new GetById(notificationId);
		
		getByIdValidation.validateRequest(data, requestValidationErrorsContainer, null);
		List<ApiValidationError> errorList = requestValidationErrorsContainer.getValidationErrorList();
		
		boolean gotError = false;
		
		if (errorList.size() > 0)
		{
//			System.out.println("Right before the throw");
			retVar = handleRequestValidationException(new RequestValidationException(errorList, requestOrigin), mapper);
			gotError = true;
		}
		
		if (!gotError) {
			Long tempId = Long.valueOf(notificationId);
			NotificationEntity record = notificationService.findById(tempId);
			if(null == record) {
				String errorMessage = "The Notification for Id: " + notificationId + " does not exist.";
				retVar = handleDatabaseRowNotFoundException(new DatabaseRowNotFoundException(errorMessage, requestOrigin), mapper);
			} else {
				String jsonString = null;
				String substitutedText = notificationService.generatePersonalization(record);
				if (null != substitutedText && substitutedText.length() > 0) {
					NonModelAdditionalFields nonModelAdditionalFields = new NonModelAdditionalFields();
					nonModelAdditionalFields.setContent(substitutedText);
					jsonString = goodResponse(record, stringBuilderContainer, nonModelAdditionalFields, mapper);			
				} else {
					jsonString = goodResponse(record, stringBuilderContainer, null, mapper);			
				}
				// support CORS
				retVar = new APIGatewayProxyResponseEvent();
				retVar.setHeaders(createResponseHeader(input));
				retVar.setStatusCode(HttpStatusCode.OK);
				retVar.setBody(jsonString);
			}
		}

		return retVar;
	}
	
}
