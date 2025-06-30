package services.validation.request;

import helpers.ValidationErrorContainer;
import services.interfaces.RequestValidation;
import services.validation.request.interfaces.functional.ValidateRequestLogic;

// uses java generics
public class RequestValidationService<RequestType>
	extends RequestValidationDefaultMethods<RequestType>
	implements RequestValidation<RequestType>
{
	
	public RequestValidationService()
	{
	}
	
	public void validateRequest(RequestType aRequest, ValidationErrorContainer aListContainer, ValidateRequestLogic<RequestType> overRide)
	{
		ValidateRequestLogic<RequestType> methodToExecute = null;
		if (null != overRide)
		{
			methodToExecute = overRide;
		}
		else
		{
			methodToExecute = getDefaultValidateRequest();
		}
		
		methodToExecute.validateRequest(aRequest, aListContainer);
		
		return;
	}
	
}
