package request.handlers;

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
import software.amazon.awssdk.utils.Validate;
import software.amazon.lambda.powertools.validation.ValidationConfig;

public class CreateNotificationHandler
	extends RequestHandlerBase
	implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> 
{
	static ObjectMapper mapper;
	static Gson gsonWithSerializeNullsAndPrettyPrint;
	static Gson gsonWithSerializeNulls;	
	static StringBuilderContainer stringBuilderContainer;
	static ValidationErrorContainer validationErrorContainer;
	
	static {
		
//		default is V7 can be changed
//		ValidationConfig.get().setSchemaVersion(SpecVersion.VersionFlag.V4);
		
	    // update (de)serializationConfig or other properties
	    mapper = ValidationConfig.get().getObjectMapper();
	    
	    gsonWithSerializeNullsAndPrettyPrint = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	    gsonWithSerializeNulls = new GsonBuilder().serializeNulls().create();
	    
	   stringBuilderContainer = new StringBuilderContainer();
	   validationErrorContainer = new ValidationErrorContainer();
	   
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		// TODO Auto-generated method stub
		CreateNotification createNotification = (CreateNotification) convertJsonToObject(input.getBody(), mapper, CreateNotification.class);
		
		stringBuilderContainer.onDestroy();
		validationErrorContainer.onDestroy();
		
		return null;
	}	
	

}
