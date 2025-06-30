package request.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.lambda.thirdparty.com.google.gson.Gson;
import com.amazonaws.lambda.thirdparty.com.google.gson.JsonElement;
import com.amazonaws.lambda.thirdparty.com.google.gson.JsonParser;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import helpers.StringBuilderContainer;
import dto.response.NonModelAdditionalFields;
import dto.response.ResultStatus;
import errorHandling.processing.RequestValidationAdvice;

public abstract class RequestHandlerBase
	extends RequestValidationAdvice
{
	
	protected static final String EOL = System.getProperty("line.separator");
	protected static final String INDENT = "  ";

	protected static final String GODD_RESPONSE_SUFFIX = "}";
	protected static final String JSON_FIELD_SEPARATOR = ",";
	
	private String generateGoodResponsePrefix(Object databaseEntityObject, ObjectMapper mapper) {
		
		ResultStatus statusObject = new ResultStatus("OK");
		String tempJson = convertToJsonNoPrettyPrint(statusObject, mapper);
		int endIndex = tempJson.length() - 1;
		String statusJson = tempJson.substring(0, endIndex) + JSON_FIELD_SEPARATOR;
		
		String entityName = databaseEntityObject.getClass().getSimpleName();
		return (statusJson + "\"" + entityName + "\": ");
	}
	
	private String removeObjectBeginAndEnd(String objectString) {
		
		String retVar = null;
		
		if (null != objectString && objectString.length() > 3) {
			retVar = objectString.substring(1, objectString.length() - 1);
		}
		
		return retVar;
	}
	
	private String gsonConvertRawJsonToPrettyPrint(String rawJsonString, Gson gsonWithSerializeNullsAndPrettyPrint)
	{
		String jsonString = null;
		
		try {
			if (null != rawJsonString && rawJsonString.length() > 0)
			{
//				Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
				
				JsonElement jsonElement = JsonParser.parseString(rawJsonString);				
				jsonString = gsonWithSerializeNullsAndPrettyPrint.toJson(jsonElement);
			}
		}
		catch(Exception jpe)
		{
			jsonString = null;
		}
		
		return jsonString;
		
	}
	
	private String convertListToJson(List<Object> anObjectList, Gson gsonWithSerializeNullsAndPrettyPrint)
	{
		String jsonString = null;
		
		try {
			if (null != anObjectList && anObjectList.size() > 0 && null != gsonWithSerializeNullsAndPrettyPrint)
			{
//				Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
				jsonString = gsonWithSerializeNullsAndPrettyPrint.toJson(anObjectList);
			}
		}
		catch(Exception jpe)
		{
			jsonString = null;
		}
		
		return jsonString;
		
	}
	
	private String convertListToJsonNoPrettyPrint(List<Object> anObjectList, Gson gsonWithSerializeNulls)
	{
		String jsonString = null;
		
		try {
			if (null != anObjectList && anObjectList.size() > 0 && null != gsonWithSerializeNulls)
			{
				jsonString = gsonWithSerializeNulls.toJson(anObjectList);
			}
		}
		catch(Exception jpe)
		{
			jsonString = null;
		}
		
		return jsonString;
		
	}

	private String convertToPrettyPrintJson(String rawJsonString, ObjectMapper mapper)
	{
		String outputString = null;
		
		try {
			if (null != rawJsonString)
			{
				ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
				outputString = ow.writeValueAsString(mapper.readValue(rawJsonString, Object.class));
			}
		}
		catch(JsonProcessingException jpe)
		{
			outputString = null;
		}
		
		return outputString;
	}

	private String convertToJsonPrettyPrint(Object anObject, ObjectMapper mapper)
	{
		String jsonString = null;
		
		try {
			if (null != anObject && null != mapper)
			{
				ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
				
				jsonString = ow.writeValueAsString(anObject);
			}
		}
		catch(JsonProcessingException jpe)
		{
			jsonString = null;
		}
		
		return jsonString;
	}
	
	private String convertToJsonNoPrettyPrint(Object anObject, ObjectMapper mapper)
	{
		String jsonString = null;
		
		try {
			if (null != anObject && null != mapper)
			{
				jsonString = mapper.writeValueAsString(anObject);
			}
		}
		catch(JsonProcessingException jpe)
		{
			jsonString = null;
		}
		
		return jsonString;
	}
	
	protected String goodResponse(Object anObject, StringBuilderContainer aContainer, NonModelAdditionalFields nonModelAdditionalFields,
			ObjectMapper mapper)
	{
		String jsonString = convertToJsonNoPrettyPrint(anObject, mapper);
		
		// Since it is Autowired clear the buffer before you use it
		aContainer.clearStringBuffer();
		StringBuilder aBuilder = aContainer.getStringBuilder();
		
		aBuilder.append(generateGoodResponsePrefix(anObject, mapper));
		aBuilder.append(jsonString);
		if (null != nonModelAdditionalFields) {
			String tempJson = convertToJsonNoPrettyPrint(nonModelAdditionalFields, mapper);
			String fixedObjectJson = removeObjectBeginAndEnd(tempJson);
			if (null != fixedObjectJson) {
				aBuilder.append(JSON_FIELD_SEPARATOR); // a comma
				aBuilder.append(fixedObjectJson);
			}
			
		}
		aBuilder.append(GODD_RESPONSE_SUFFIX);
		String rawJson = aBuilder.toString();
		
//		System.out.println("raw json is: " + rawJson);
		
		
		return convertToPrettyPrintJson(rawJson, mapper);
	}
	
	protected String goodResponseList(List<Object> anObject, StringBuilderContainer aContainer, Gson gson, ObjectMapper mapper)
	{
		String jsonString = convertListToJsonNoPrettyPrint(anObject, gson);
		
		// Since it is Autowired clear the buffer before you use it
		aContainer.clearStringBuffer();
		StringBuilder aBuilder = aContainer.getStringBuilder();
		
		Object tempObject = anObject.get(0);
		
		aBuilder.append(generateGoodResponsePrefix(tempObject, mapper));
		aBuilder.append(jsonString);
		aBuilder.append(GODD_RESPONSE_SUFFIX);
		String rawJson = aBuilder.toString();

//		System.out.println("raw json is: " + rawJson);
		
		return gsonConvertRawJsonToPrettyPrint(rawJson, gson);
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
//		System.out.println("Access-Control-Allow-Origin is: " + request.getHeader("Origin"));
		Map<String, String> aResponseHeader = new HashMap<String, String>();
		aResponseHeader.put("Access-Control-Allow-Origin", getRequestOrigin(request));
//		aResponseHeader.put("Access-Control-Allow-Origin", "*");
		aResponseHeader.put("Content-Type", "application/json");
		
		return aResponseHeader;
		
	}
	
	protected Object convertJsonToObject(String jsonString, ObjectMapper mapper, Class<?> clazz) {
		Object retVar = null;
		if (null != jsonString && jsonString.length() > 0 && null != mapper && null != clazz) {
			try {
				retVar = mapper.readValue(jsonString, clazz);
			} catch (JsonProcessingException e) {
				retVar = null;
			}
		}
		
		return retVar;
	}
	

}
