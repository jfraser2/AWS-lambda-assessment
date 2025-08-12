package errorHandling.processing;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import validation.exceptions.DatabaseRowNotFoundException;
import validation.exceptions.EmptyListException;
import validation.exceptions.OptimisticLockingException;
import validation.exceptions.RequestValidationException;
import validation.exceptions.ShutdownException;
import errorHandling.helpers.ApiError;
import helpers.RequestOrigin;
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
	protected static final String DEFAULT_REQUEST_ORIGIN = "http://localhost:9000";
	
	//other exception handlers or handler overrides below
	
    public APIGatewayV2HTTPResponse handleAccessDeniedException(
    		AccessDeniedException ex, RequestOrigin requestOrigin, ObjectMapper mapper)
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
    		IllegalArgumentException ex, RequestOrigin requestOrigin, ObjectMapper mapper)
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
		apiError.setStatus("DATABASE_ROW_NOT_FOUND");
		
        apiError.setMessage(ex.getMessage());
        
		String json = convertApiErrorToJson(apiError, mapper);
		apiError = null;
        
        return buildResponseEntity(json, HttpStatusCode.NOT_FOUND, ex.getRequestOrigin());
    }
    
    public APIGatewayV2HTTPResponse handleEmptyListException(EmptyListException ex, ObjectMapper mapper)
    {
		ApiError apiError = new ApiError();
		apiError.setStatus("NO_CONTENT");
		
        apiError.setMessage(ex.getMessage());
        
		String json = convertApiErrorToJson(apiError, mapper);
		apiError = null;
        
        return buildResponseEntity(json, HttpStatusCode.NO_CONTENT, ex.getRequestOrigin());
    }
    
    public APIGatewayV2HTTPResponse handleOptimisticLockingException(OptimisticLockingException ex, ObjectMapper mapper)
    {
		ApiError apiError = new ApiError();
		apiError.setStatus("OPTIMISTIC_LOCKING_ERROR");
		
        apiError.setMessage(ex.getMessage());
        
		String json = convertApiErrorToJson(apiError, mapper);
		apiError = null;
        
		// 412 Http code official description is: Precondition Failed
        return buildResponseEntity(json, 412, ex.getRequestOrigin());
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
    
	public APIGatewayV2HTTPResponse buildResponseEntity(String json, int aStatus, RequestOrigin requestOrigin)
	{
		APIGatewayV2HTTPResponse retVar = new APIGatewayV2HTTPResponse();
		// support CORS
		Map<String, String> aResponseHeader = createErrorResponseHeader(requestOrigin);
		
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
	
	public Map<String, String> createErrorResponseHeader(RequestOrigin requestOrigin)
	{
		// support CORS
//		System.err.println("Access-Control-Allow-Origin is: " + requestOrigin);
		Map<String, String> aResponseHeader = new HashMap<String, String>();
		
		boolean originPopulated = (null != requestOrigin && null != requestOrigin.getOrigin() && requestOrigin.getOrigin().length() > 0);
		
		if (originPopulated) {
			aResponseHeader.put("Access-Control-Allow-Origin", requestOrigin.getOrigin()); //who is allowed to access the resource
		} else {
			aResponseHeader.put("Access-Control-Allow-Origin", "*"); //who is allowed to access the resource
		}
		
		if (originPopulated)
		{
			if (requestOrigin.isFromSwagger()) { 
//				aResponseHeader.put("Content-Type", "image/svg+xml;charset=utf-8"); // Swagger a.k.a OpenApi
				aResponseHeader.put("Content-Type", "application/json"); // Swagger a.k.a OpenApi
			} else {
				aResponseHeader.put("Content-Type", "application/json"); // browser or curl
			}
		} else {
			aResponseHeader.put("Content-Type", "application/json"); // default Content-Type
		}
		
//		aResponseHeader.put("Access-Control-Allow-Origin", "*"); //who is allowed to access the resource
		aResponseHeader.put("X-Requested-With", "*"); // enable CORS for AWS
		
		aResponseHeader.put("Access-Control-Allow-Methods", "OPTIONS,GET,POST,PUT,DELETE,PATCH"); // Allowed HTTP methods
		aResponseHeader.put("Access-Control-Allow-Headers", "Content-Type,Origin,Accept,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,x-requested-with,Referer,User-Agent,api_key,Host,X-Forwarded-Proto,X-Forwarded-Port,FromSwagger"); // Allowed headers		
		
		return aResponseHeader;
		
	}
	

}
