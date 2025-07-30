package request.handlers;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AuthorizationHandler
	extends RequestHandlerBase
	implements RequestHandler<APIGatewayV2HTTPEvent, Map<String, Object>>
{
	// For AuthorizerPayloadFormatVersion: 2.0 and enableSimpleResponses: true
	protected static final String AUTHORIZED_RESPONSE = "{\"isAuthorized\": true}";
	protected static final String UNAUTHORIZED_RESPONSE = "{\"isAuthorized\": false}";
	protected static final String HEADER_KEY = "X-Api-Key";
	protected static final String API_KEY_VALUE = "abc";
	protected static ObjectMapper mapper;
	protected static Gson gsonWithSerializeNullsAndPrettyPrint;
	protected static Gson gsonWithSerializeNulls;

	static {
		
//		default is V7 can be changed
//		ValidationConfig.get().setSchemaVersion(SpecVersion.VersionFlag.V4);
		
	    // update (de)serializationConfig or other properties
	    mapper = new ObjectMapper();
	    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // exclude null values
	    
	    gsonWithSerializeNullsAndPrettyPrint = new GsonBuilder().setPrettyPrinting().create();
	    gsonWithSerializeNulls = new GsonBuilder().create();
	    
	}

	@Override
	public Map<String, Object> handleRequest(APIGatewayV2HTTPEvent input, Context context) {
		System.out.println("Entered the Authorization Handler");
		
		String requestOrigin = getRequestOrigin(input);
		String requestMethod = input.getRequestContext().getHttp().getMethod();
		System.out.println("The request Origin is: " + requestOrigin);
		System.out.println("The request Method is: " + requestMethod);
		System.out.println("raw path is: " + input.getRawPath());
		
		Boolean isAuthorized = false;
		
		String sentApiKey = null;
		if (null != input && null != input.getHeaders()) {
			String testString = input.getHeaders().get(HEADER_KEY);
			if (null != testString && testString.length() > 0) {
				sentApiKey = testString.trim();
			}
			if (null != sentApiKey && API_KEY_VALUE.equalsIgnoreCase(sentApiKey)) {
				isAuthorized = true;
			}
		}
			
		// For AuthorizerPayloadFormatVersion: 2.0 and enableSimpleResponses: true
	    // Construct the response Body Map
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("isAuthorized", isAuthorized);

//        System.out.println("In AuthorizationHandler, response Headers are: " + retVar.getHeaders());
        System.out.println("In AuthorizationHandler, json Being returned: " + responseBody);
		
		return responseBody;
	}
	
}
