package errorHandling.processing;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import validation.exceptions.DatabaseRowNotFoundException;
import validation.exceptions.RequestValidationException;
import validation.exceptions.ShutdownException;
import errorHandling.helpers.ApiError;

import software.amazon.awssdk.http.HttpStatusCode;

/*
	One thing to keep in mind here is to match the exceptions declared with @ExceptionHandler with the exception used as the argument of the method.
	If these don’t match, the compiler will not complain – no reason it should, and Spring will not complain either.

	However, when the exception is actually thrown at runtime, the exception resolving mechanism will fail with:

	1 java.lang.IllegalStateException: No suitable resolver for argument [0] [type=...]
	2 HandlerMethod details: ...

*/

//  Advice execution precedence
//@Order(Ordered.HIGHEST_PRECEDENCE)
public abstract class RequestValidationAdvice
{
	protected static final String UNEXPECTED_PROCESSING_ERROR = "{\"message\": \"Object could not convert to json\"}";
	protected static final String DEFAULT_REQUEST_ORIGIN = "http://localhost:8080";
	
	//other exception handlers or handler overrides below
	
    public APIGatewayV2HTTPResponse handleAccessDeniedException(
    		AccessDeniedException ex, String requestOrigin, ObjectMapper mapper)
    {
		ApiError apiError = new ApiError();
		apiError.setStatus("FORBIDDEN");
    	
 		String error = ex.getMessage();
        apiError.setMessage(error);
        
		String json = convertApiErrorToJson(apiError, mapper);
		apiError = null;
        
        return buildResponseEntity(json, HttpStatusCode.OK, requestOrigin);
    }
	
    public APIGatewayV2HTTPResponse handleIllegalArgumentException(
    		IllegalArgumentException ex, String requestOrigin, ObjectMapper mapper)
    {
		ApiError apiError = new ApiError();
		apiError.setStatus("BAD_REQUEST");
		
        apiError.setMessage(ex.getMessage());
        
		String json = convertApiErrorToJson(apiError, mapper);
		apiError = null;
        
        return buildResponseEntity(json, HttpStatusCode.OK, requestOrigin);
    }
    
    public APIGatewayV2HTTPResponse handleDatabaseRowNotFoundException(DatabaseRowNotFoundException ex, ObjectMapper mapper)
    {
		ApiError apiError = new ApiError();
		apiError.setStatus("NO_CONTENT");
		
        apiError.setMessage(ex.getMessage());
        
		String json = convertApiErrorToJson(apiError, mapper);
		apiError = null;
        
        return buildResponseEntity(json, HttpStatusCode.OK, ex.getRequestOrigin());
    }
    
    public APIGatewayV2HTTPResponse handleRequestValidationException(
    	RequestValidationException ex, ObjectMapper mapper)
    {
		ApiError apiError = new ApiError();
		apiError.setStatus("BAD_REQUEST");
		
		String error = "Validation errors";
        apiError.setMessage(error);
        apiError.setSubErrors(ex.getSubErrorList());
   
		String json = convertApiErrorToJson(apiError, mapper);
		apiError = null;
        
        return buildResponseEntity(json, HttpStatusCode.OK, ex.getRequestOrigin());
    }
	 
    public APIGatewayV2HTTPResponse handleShutdownException(
        ShutdownException ex, ObjectMapper mapper)
    {
		ApiError apiError = new ApiError();
		apiError.setStatus(ex.getStatus());
		
		String error = ex.getMessage();
        apiError.setMessage(error);
   
		String json = convertApiErrorToJson(apiError, mapper);
		apiError = null;
        
        return buildResponseEntity(json, HttpStatusCode.OK, ex.getRequestOrigin());
    }
    
	public APIGatewayV2HTTPResponse buildResponseEntity(String json, int aStatus, String requestOrigin)
	{
		APIGatewayV2HTTPResponse retVar = new APIGatewayV2HTTPResponse();
		// support CORS
		Map<String, String> aResponseHeader = createResponseHeader(requestOrigin);
		
		retVar.setIsBase64Encoded(false);
		retVar.setHeaders(aResponseHeader);
		retVar.setStatusCode(aStatus);
		retVar.setBody(json);
		
		return retVar;
	}
	
	public String convertApiErrorToJson(ApiError apiError, ObjectMapper mapper)
	{
		String json = null;
		try {
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String tempString = ow.writeValueAsString(apiError);
//			json = StringEscapeUtils.escapeJson(tempString);
			json = tempString;
		}
		catch(JsonProcessingException jpe)
		{
//			json = StringEscapeUtils.escapeJson(UNEXPECTED_PROCESSING_ERROR);
			json = UNEXPECTED_PROCESSING_ERROR;
		}
		
		return json;
	}
	
	public Map<String, String> createResponseHeader(String requestOrigin)
	{
		// support CORS
//		System.err.println("Access-Control-Allow-Origin is: " + requestOrigin);
		Map<String, String> aResponseHeader = new HashMap<String, String>();
		if (null != requestOrigin && requestOrigin.length() > 0) {
			aResponseHeader.put("Access-Control-Allow-Origin", requestOrigin);
		} else {
			aResponseHeader.put("Access-Control-Allow-Origin", DEFAULT_REQUEST_ORIGIN);
		}
//		aResponseHeader.put("Access-Control-Allow-Origin", "*");
		aResponseHeader.put("Content-Type", "application/json");
		
		return aResponseHeader;
		
	}
	

}
