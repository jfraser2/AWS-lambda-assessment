package services.validation.request.interfaces.functional;

import helpers.ValidationErrorContainer;

public interface ValidateRequestLogic<RequestType>
{
	void validateRequest(RequestType aRequest, ValidationErrorContainer aListContainer);
}
