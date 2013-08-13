/*******************************************************************************
 * Copyright 2013 Virginia Polytechnic Institute and State University
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
package edu.vt.vbi.patric.core.identity.ui.actions;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;
import org.jboss.portal.core.identity.services.IdentityConstants;
//import org.jboss.portal.core.identity.services.IdentityMailService;
import org.jboss.portal.core.identity.ui.IdentityUIUser;
//import org.jboss.portal.core.identity.ui.common.IdentityUserBean;
import edu.vt.vbi.patric.core.identity.ui.common.IdentityUserBean;
//import org.jboss.portal.faces.el.PropertyValue;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;

/**
 * JBoss retrieve(actually, resets) password by username, but we want to do it by email, since it would be easier to remember than username.
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 * 
 */
public class LostPasswordAction extends org.jboss.portal.core.identity.ui.actions.LostPasswordAction {

	private static final Logger log = Logger.getLogger(LostPasswordAction.class);

	private IdentityUserBean identityUserBean;

	private String email;

	public void setEmail(String email) {
		this.email = email;
	}

	public IdentityUserBean getIdentityUserBean() {
		return identityUserBean;
	}

	public void setIdentityUserBean(IdentityUserBean identityUserBean) {
		this.identityUserBean = identityUserBean;
	}

	public String doomed() {
		//System.out.println("Hey, you are using Harry's customized LostPasswordAction.doomed() " + email);

		User user = null;
		ResourceBundle bundle = ResourceBundle.getBundle("conf.bundles.Identity", FacesContext.getCurrentInstance()
				.getViewRoot().getLocale());

		if (email != null && email.trim().length() > 0) {
			try {
				user = identityUserBean.findUserByUserEmail(email);
			}
			catch (NoSuchUserException e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(bundle.getString("IDENTITY_LOST_PASSWORD_STATUS_404")));
				return "status";
			}
			catch (Exception e) {
				e.printStackTrace();
				log.error("", e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(bundle.getString("IDENTITY_LOST_PASSWORD_ERROR")));
				return "status";
			}
		}
		else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please provide an email address"));
			return "status";
		}

		if (user != null) {
			try {
				String newPassword = this.genPassword(8);
				IdentityUIUser uiUser = new IdentityUIUser(user.getUserName());
				Map<String, String> mailMap = new HashMap<String, String>();

				mailMap.put(IdentityConstants.EMAIL_TO, (String) uiUser.getAttribute().getValue("email").getObject());
				mailMap.put("username", user.getUserName());
				mailMap.put("password", newPassword);

				// Update password
				identityUserBean.updatePassword(user.getUserName(), newPassword);
				// Sending email
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				super.getIdentityMailService().sendMail("lostPassword", mailMap, locale);
			}
			catch (Exception e) {
				log.error("", e);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("IDENTITY_LOST_PASSWORD_ERROR"),
								bundle.getString("IDENTITY_LOST_PASSWORD_ERROR")));
				return "status";
			}
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(bundle.getString("IDENTITY_LOST_PASSWORD_STATUS_SUCCESSFUL")));
		return "status";
	}

	private String genPassword(int length) throws NoSuchAlgorithmException {
		StringBuffer buffer = new StringBuffer();
		char[] characterMap = super.getPasswordCharacters().toCharArray();
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");

		for (int i = 0; i <= length; i++) {
			byte[] bytes = new byte[512];
			secureRandom.nextBytes(bytes);
			double number = secureRandom.nextDouble();
			int b = ((int) (number * characterMap.length));
			buffer.append(characterMap[b]);
		}
		return buffer.toString();
	}
}
