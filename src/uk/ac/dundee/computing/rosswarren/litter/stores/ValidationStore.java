package uk.ac.dundee.computing.rosswarren.litter.stores;
import java.util.*;

import uk.ac.dundee.computing.rosswarren.litter.connectors.UserConnector;

public class ValidationStore {
    /*  The properties */

	String name = "";
	String username = "";
	
    /* Errors */
    public static final Integer ERR_NAME = new Integer(1);
    public static final Integer ERR_USERNAME = new Integer(2);
    public static final Integer ERR_USERNAME_TAKEN = new Integer(3);

    // Holds error messages for the properties
    Map<String, Integer> errorCodes = new HashMap<String, Integer>();

    // Maps error codes to textual messages.
    // This map must be supplied by the object that instantiated this bean.
    Map<?, ?> msgMap;
    public void setErrorMessages(Map<?, ?> msgMap) {
        this.msgMap = msgMap;
    }
    
    public String getName()
    {
    	return name;
    }
    
    public void setName(String name)
    {
    	this.name = name;
    }

    public String getUserName()
    {
    	return username;
    }
    
    public void setUserName(String username)
    {
    	this.username = username;
    }

    public String getErrorMessage(String propName) {
        Integer code = (Integer)(errorCodes.get(propName));
        if (code == null) {
            return "";
        } else if (msgMap != null) {
            String msg = (String)msgMap.get(code);
            if (msg != null) {
                return msg;
            }
        }
        return "Error";
    }

    /* Form validation and processing */
    public boolean isValid() {
        // Clear all errors
        errorCodes.clear();

        // Validate Name
        if (name.length() == 0) {
            errorCodes.put("name", ERR_NAME);
        }
        
        if (username.length() == 0)
        {
        	errorCodes.put("username", ERR_USERNAME);
        }
        UserConnector connector = new UserConnector();
        if (connector.getUserByUsername(username) != null)
        {
        	errorCodes.put("username", ERR_USERNAME_TAKEN);
        }
        // If no errors, form is valid
        return errorCodes.size() == 0;
    }

    public boolean process() {
        if (!isValid()) {
            return false;
        }

        // Process form...

        // Clear the form
        name = "";
        username = "";
        errorCodes.clear();
        return true;
    }
}