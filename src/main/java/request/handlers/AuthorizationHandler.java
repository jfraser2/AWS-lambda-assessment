package request.handlers;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.http.HttpStatusCode;

public class AuthorizationHandler
	extends RequestHandlerBase
	implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
{
	protected static final String AUTHORIZED_RESPONSE = "{\"isAuthorized\": \"true\"}";
	protected static final String UNAUTHORIZED_RESPONSE = "{\"isAuthorized\": \"false\"}";
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
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		System.out.println("Entered the Authorization Handler");
		
		APIGatewayProxyResponseEvent retVar = new APIGatewayProxyResponseEvent();
		retVar.setIsBase64Encoded(false);
		retVar.setHeaders(createResponseHeader(input));
		retVar.setStatusCode(HttpStatusCode.OK);
		retVar.setBody(AUTHORIZED_RESPONSE);
		
        System.out.println("In AuthorizationHandler, json Being returned: " + retVar.getBody());
		
		return retVar;
	}
	
	protected String getRequestOrigin(APIGatewayProxyRequestEvent request)
	{
		String retVar = null;
		
		if (null != request && null != request.getHeaders()) {
			retVar = request.getHeaders().get("Origin");
		}
		
		return retVar;
	}
	
	protected Map<String, String> createResponseHeader(APIGatewayProxyRequestEvent request)
	{
		// support CORS
//		System.err.println("Access-Control-Allow-Origin is: " + request.getHeader("Origin"));
		Map<String, String> aResponseHeader = new HashMap<String, String>();
		
		String requestOrigin = getRequestOrigin(request);
		aResponseHeader.put("Access-Control-Allow-Origin", requestOrigin);
//		aResponseHeader.put("Access-Control-Allow-Origin", "*");
		aResponseHeader.put("Content-Type", "application/json");
		
		return aResponseHeader;
		
	}
	
}
