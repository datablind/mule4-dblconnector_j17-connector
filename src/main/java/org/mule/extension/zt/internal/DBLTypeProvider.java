/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */
package org.mule.extension.zt.internal;

import java.util.Set;

import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueBuilder;
import org.mule.runtime.extension.api.values.ValueResolvingException;

public class DBLTypeProvider implements ValueProvider {
	
	@Override
	public Set<Value> resolve() throws ValueResolvingException {
		return ValueBuilder.getValuesFor("Date (MM/dd/yyyy)" , 
				"DateTime (MM/dd/yyyy HH:mm:ss)", 
						"IP Address (v4)" , 
						"GPS", 
						"UPC-A",  
						"Credit Card Number" , 
						"SSN" , 
						"Street Address",
						"Street Address Nice" , 
						"Currency Amount" ,				
						"Phone Number" , 
						"Person Name" , 
						"Person Name Nice" , 
						"Email Address" );
	}
}
 