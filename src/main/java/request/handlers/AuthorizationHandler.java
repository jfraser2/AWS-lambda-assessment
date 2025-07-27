package request.handlers;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.http.HttpStatusCode;

public class AuthorizationHandler
	extends RequestHandlerBase
	implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>
{
	// For AuthorizerPayloadFormatVersion: 2.0 and enableSimpleResponses: true
	protected static final String AUTHORIZED_RESPONSE = "{\"isAuthorized\": true}";
	protected static final String UNAUTHORIZED_RESPONSE = "{\"isAuthorized\": false}";
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
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
		System.out.println("Entered the Authorization Handler");
		
		String requestOrigin = getRequestOrigin(input);
		String requestMethod = input.getRequestContext().getHttp().getMethod();
		System.out.println("The request Origin is: " + requestOrigin);
		System.out.println("The request Method is: " + requestMethod);
		System.out.println("raw path is: " + input.getRawPath());
		
		Boolean isAuthorized = true;
		
	    // Construct the response Body Map
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("isAuthorized", isAuthorized);
//        responseBody.put("context", authorizerContext);
        
        String jsonString = convertToJsonNoPrettyPrint(responseBody, mapper);

		APIGatewayV2HTTPResponse retVar = new APIGatewayV2HTTPResponse();
		retVar.setIsBase64Encoded(false);
		retVar.setHeaders(createOptionsResponseHeader(requestOrigin));
		retVar.setStatusCode(HttpStatusCode.OK);
//		retVar.setBody(StringEscapeUtils.escapeJson(AUTHORIZED_RESPONSE));
		retVar.setBody(jsonString);
		
        System.out.println("In AuthorizationHandler, json Being returned: " + retVar.getBody());
		
		return retVar;
	}
	
}
