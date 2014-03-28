/*******************************************************************************
 * Copyright 2014 Virginia Polytechnic Institute and State University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package edu.vt.vbi.patric.core.identity.ui.validators;

//import org.jboss.portal.identity.UserModule;
import edu.vt.vbi.patric.identity.UserModule;

import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.portlet.PortletContext;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;

public class EmailValidator implements Validator {

	private static final String EMAIL_VALIDATION = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.(([0-9]{1,3})|([a-zA-Z]{2,3})|(aero|coop|info|museum|name))$";
	private UserModule userModule;
	private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(EmailValidator.class);
	
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		ResourceBundle bundle = ResourceBundle.getBundle("conf.bundles.Identity", context.getViewRoot().getLocale());
		if (value != null) {
			if (!(value instanceof String)) {
				throw new IllegalArgumentException("The value must be a String");
			}

			//System.out.println("PATRIC version EmailValidator");
			// check database
			PortletContext portletContext = (PortletContext)context.getExternalContext().getContext();
			userModule = (UserModule)portletContext.getAttribute("UserModule");
			try {
				userModule.findUserByUserEmail(value.toString());
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						bundle.getString("IDENTITY_VALIDATION_ERROR_EMAIL_TAKEN"),
						bundle.getString("IDENTITY_VALIDATION_ERROR_EMAIL_TAKEN")));
			}
			catch (NoSuchUserException e) {
				// No user found - proceed
			}
			catch (IllegalArgumentException e) {
				log.error("EmailValidator: IllegalArgumentException");
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						bundle.getString("IDENTITY_VALIDATION_ERROR_EMAIL_ERROR"),
						bundle.getString("IDENTITY_VALIDATION_ERROR_EMAIL_ERROR")));
			}
			catch (IdentityException e) {
				log.error("EmailValidator: IdentityException");
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						bundle.getString("IDENTITY_VALIDATION_ERROR_EMAIL_ERROR"),
						bundle.getString("IDENTITY_VALIDATION_ERROR_EMAIL_ERROR")));
			}
			// check pattern
			if (!Pattern.matches(EMAIL_VALIDATION, (String) value)) {
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						bundle.getString("IDENTITY_VALIDATION_ERROR_INVALID_EMAIL"),
						bundle.getString("IDENTITY_VALIDATION_ERROR_INVALID_EMAIL")));
			}
		}
		else {
			throw new ValidatorException(new FacesMessage("Required"));
		}
	}
}
