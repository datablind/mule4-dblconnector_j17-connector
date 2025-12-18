/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */
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

