package services.interfaces;

import helpers.ValidationErrorContainer;
import services.validation.request.interfaces.functional.ValidateRequestLogic;

public interface RequestValidation<RequestType>
{
	void validateRequest(RequestType aRequest, ValidationErrorContainer aListContainer, ValidateRequestLogic<RequestType> overRide);
}
