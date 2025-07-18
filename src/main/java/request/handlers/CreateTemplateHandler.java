package request.handlers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dto.request.CreateTemplate;
import dto.request.UpdateTemplate;
import dto.request.GetById;
import entities.TemplateEntity;
import errorHandling.helpers.ApiValidationError;
import helpers.StringBuilderContainer;
import helpers.ValidationErrorContainer;
import hibenate.utils.HibernateUtil;
import services.TemplateImpl;
import services.interfaces.RequestValidation;
import services.interfaces.Template;
import services.validation.request.RequestValidationService;
import software.amazon.awssdk.http.HttpStatusCode;
import validation.exceptions.DatabaseRowNotFoundException;
import validation.exceptions.RequestValidationException;
import validation.exceptions.ShutdownException;

public class CreateTemplateHandler
	extends RequestHandlerBase
	implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> 
{
	protected static ObjectMapper mapper;
	protected static Gson gsonWithSerializeNullsAndPrettyPrint;
	protected static Gson gsonWithSerializeNulls;
	protected static Template templateService;
	protected static SessionFactory sessionFactory;
	
	static {
		
//		default is V7 can be changed
//		ValidationConfig.get().setSchemaVersion(SpecVersion.VersionFlag.V4);
		
	    // update (de)serializationConfig or other properties
	    mapper = new ObjectMapper();
	    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	    
	    gsonWithSerializeNullsAndPrettyPrint = new GsonBuilder().setPrettyPrinting().create();
	    gsonWithSerializeNulls = new GsonBuilder().create();
	    sessionFactory = HibernateUtil.getSessionFactory();
	    
	    templateService = new TemplateImpl(sessionFactory); //one per Class
	    
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context)
	{
		String requestOrigin = getRequestOrigin(input);
		StringBuilderContainer stringBuilderContainer = new StringBuilderContainer(); // request Scope
		ValidationErrorContainer requestValidationErrorsContainer = new ValidationErrorContainer(); //request Scope
		
		APIGatewayProxyResponseEvent retVar = null;
		
		switch (input.getResource()) {
		    case "/v1/createTemplate":
				retVar = creation(input, templateService, requestValidationErrorsContainer,
					stringBuilderContainer, requestOrigin);
		        break;
		    case "/v1/all/templates":
				retVar = findAll(input, templateService, requestValidationErrorsContainer,
						stringBuilderContainer, requestOrigin);
		        break;
		    case "/v1/findByTemplateId/{id}":
				retVar = findById(input, templateService, requestValidationErrorsContainer,
						stringBuilderContainer, requestOrigin);
		        break;
		    case "/v1/updateTemplate":
				retVar = update(input, templateService, requestValidationErrorsContainer,
						stringBuilderContainer, requestOrigin);
		        break;
		    case "/v1/template/shutdown":
				retVar = shutdownHook(requestOrigin);
		        break;
		}		
		
		stringBuilderContainer.onDestroy();
		requestValidationErrorsContainer.onDestroy();
		
		return retVar;
	}
	
	protected APIGatewayProxyResponseEvent creation(APIGatewayProxyRequestEvent input, Template templateService,
			ValidationErrorContainer requestValidationErrorsContainer, StringBuilderContainer stringBuilderContainer,
			String requestOrigin)
	{
		APIGatewayProxyResponseEvent retVar = null;
		RequestValidation<CreateTemplate> createTemplateValidation = 
				new RequestValidationService<CreateTemplate>(); // request Scope
		
		CreateTemplate createTemplate = (CreateTemplate) convertJsonToObject(input.getBody(), mapper, CreateTemplate.class);
		
		// single field validation
		createTemplateValidation.validateRequest(createTemplate, requestValidationErrorsContainer, null);
		List<ApiValidationError> errorList = requestValidationErrorsContainer.getValidationErrorList();
		
		boolean gotError = false;
		
		if (errorList.size() > 0)
		{
//				System.out.println("Right before the throw");
			retVar = handleRequestValidationException(new RequestValidationException(errorList, requestOrigin), mapper);
			gotError = true;
		}
		
		if (!gotError) {
			TemplateEntity te = templateService.buildTemplateEntity(createTemplate);
			TemplateEntity savedEntity = templateService.persistData(te);
			
			String jsonString = goodResponse(savedEntity, stringBuilderContainer, null, mapper);
			// support CORS
			retVar = new APIGatewayProxyResponseEvent();
			retVar.setHeaders(createResponseHeader(input));
			retVar.setStatusCode(HttpStatusCode.CREATED);
			retVar.setBody(jsonString);
		}
		
		return retVar;
	}
	
	protected APIGatewayProxyResponseEvent findAll(APIGatewayProxyRequestEvent input, Template templateService,
			ValidationErrorContainer requestValidationErrorsContainer, StringBuilderContainer stringBuilderContainer,
			String requestOrigin)
	{
		APIGatewayProxyResponseEvent retVar = null;

		List<TemplateEntity> aList = templateService.findAll();
		boolean isEmpty = true;
		if(null != aList && aList.size() > 0) {
			isEmpty = false;
		}
		
		if(isEmpty) {
			retVar = handleDatabaseRowNotFoundException(new DatabaseRowNotFoundException("Template Table is empty.", requestOrigin), mapper);
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
	
	protected APIGatewayProxyResponseEvent findById(APIGatewayProxyRequestEvent input, Template templateService,
			ValidationErrorContainer requestValidationErrorsContainer, StringBuilderContainer stringBuilderContainer,
			String requestOrigin)
	{
		APIGatewayProxyResponseEvent retVar = null;
		RequestValidation<GetById> getByIdValidation = 
				new RequestValidationService<GetById>(); // request Scope
		
		String templateId = input.getPathParameters().get("id");
		GetById data = new GetById(templateId);
		
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
			Long tempId = Long.valueOf(templateId);
			TemplateEntity record = templateService.findById(tempId);
			if(null == record) {
				String errorMessage = "The Template for Id: " + templateId + " does not exist.";
				retVar = handleDatabaseRowNotFoundException(new DatabaseRowNotFoundException(errorMessage, requestOrigin), mapper);
			} else {
				String jsonString = goodResponse(record, stringBuilderContainer, null, mapper);
				// support CORS
				retVar = new APIGatewayProxyResponseEvent();
				retVar.setHeaders(createResponseHeader(input));
				retVar.setStatusCode(HttpStatusCode.OK);
				retVar.setBody(jsonString);
			}
		}

		return retVar;
	}
	
	protected APIGatewayProxyResponseEvent update(APIGatewayProxyRequestEvent input, Template templateService,
			ValidationErrorContainer requestValidationErrorsContainer, StringBuilderContainer stringBuilderContainer,
			String requestOrigin)
	{
		APIGatewayProxyResponseEvent retVar = null;
		RequestValidation<UpdateTemplate> updateTemplateValidation = 
				new RequestValidationService<UpdateTemplate>(); // request Scope
		
		UpdateTemplate createTemplate = (UpdateTemplate) convertJsonToObject(input.getBody(), mapper, UpdateTemplate.class);
		
		// single field validation
		updateTemplateValidation.validateRequest(createTemplate, requestValidationErrorsContainer, null);
		List<ApiValidationError> errorList = requestValidationErrorsContainer.getValidationErrorList();
		
		boolean gotError = false;
		
		if (errorList.size() > 0)
		{
//				System.out.println("Right before the throw");
			retVar = handleRequestValidationException(new RequestValidationException(errorList, requestOrigin), mapper);
			gotError = true;
		}
		
		if (!gotError) {
			Long tempId = Long.valueOf(createTemplate.getTemplateId());
			TemplateEntity record = templateService.findById(tempId);
			if(null == record) {
				String errorMessage = "The Template for Id: " + createTemplate.getTemplateId() + " does not exist.";
				retVar = handleDatabaseRowNotFoundException(new DatabaseRowNotFoundException(errorMessage, requestOrigin), mapper);
			} else {
				record.setBody(createTemplate.getNewTemplateText());
				TemplateEntity updatedEntity = templateService.mergeData(record);
				
				String jsonString = goodResponse(updatedEntity, stringBuilderContainer, null, mapper);
				// support CORS
				retVar = new APIGatewayProxyResponseEvent();
				retVar.setHeaders(createResponseHeader(input));
				retVar.setStatusCode(HttpStatusCode.OK);
				retVar.setBody(jsonString);
			}
		}
		
		return retVar;
	}
	
    // Really the only way to do this because of the limitation
	// being created by the SIGKILL or SIGINT not being sent by Ctrl-C from sam.cmd
    // The only other possible way would be a modification of sam.cmd which uses python.exe
	// The sam.cmd would have to start using pythonw.exe, not going there
	protected APIGatewayProxyResponseEvent shutdownHook(String requestOrigin)
	{
		APIGatewayProxyResponseEvent retVar = null;
		
//        System.out.println("[runtime] Template ShutdownHook triggered");
//        System.out.println("[runtime] Template Clean up");
        
        // perform actual clean up work here.
        boolean shutdownFailed = false;
        try {
        	HibernateUtil.shutdown();
         } catch (Exception e) {
        	shutdownFailed = true; 
         }

        String outputMessage = "Template ShutdownHook triggered.";
        String outputStatus;
        
        if (!shutdownFailed) {
        	outputStatus = "OK";
        } else {
        	outputStatus = "TEMPLATE_SHUTDOWN_FAILED";
        }

		retVar = handleShutdownException(new ShutdownException(outputMessage, outputStatus, requestOrigin), mapper);
		return retVar;
	}
	

}
