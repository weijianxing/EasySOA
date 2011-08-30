package org.easysoa.doctypes;

import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("easysoaValidator")
@Scope(ScopeType.EVENT)
@Install(precedence = Install.FRAMEWORK)
public class PropertyValidatorBean extends EasySOADoctype {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(PropertyValidatorBean.class);

	public void validateUrl(FacesContext context,
	        UIComponent component, Object value) {
	    try {
	        new URL((String) value);
	    }
	    catch (MalformedURLException e) {
	        throw new ValidatorException(new FacesMessage("Invalid URL"));
	    }
	}
	
}