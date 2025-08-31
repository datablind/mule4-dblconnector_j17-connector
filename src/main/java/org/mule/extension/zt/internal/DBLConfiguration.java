package org.mule.extension.zt.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;


/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(DBLOperations.class)
@ConnectionProviders(DBLConnectionProvider.class)
public class DBLConfiguration {

@Parameter
@DisplayName("Encryption Key")
	private String encryptionKey;
	public String getEncryptionKey() {
		  return encryptionKey;
	}
/* 
@Parameter
@DisplayName("DataGuardAPI Key")
	private String apiKey;
	public String getApiKey() {
		  return apiKey;
	}
	
@Parameter
@DisplayName("API URI")
	private String apiUri;
	public String getApiUri() {
		return apiUri;
	}
*/
}

