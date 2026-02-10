# DataBlind Connector User Manual

The DataBlind Connector provides secure data encryption and decryption capabilities for JSON data within MuleSoft applications. This connector integrates with the DataBlind encryption framework to enable field-level encryption, decryption, and data filtering operations.

## Overview

The DataBlind Connector offers the following key capabilities:

- **JSON Field Encryption**: Encrypt specific fields within JSON documents
- **JSON Field Decryption**: Decrypt previously encrypted JSON fields
- **NLP-Based Encryption**: Use natural language processing to automatically identify, encrypt & mask sensitive fields
- **Data Filtering**: Reduce JSON data by filtering out sensitive information
- **Token Management**: Generate and manage override tokens for enhanced authorization

## Concept

![Concept](/assets/data-blind-concept-diagram.png)

## Sample Input & Output JSON

### Input JSON
```json
{
	    "legal" : 
 		[   
 			{ 
 			"firstName" : "John",  
 			"lastName"  : "Doe",
 			"age"       : 23 
 			},
			{
			"firstName" : "Mary",  
 			"lastName"  : "Smith",
 			"age"      : 32 
 			}
 		],                           
	    "marketing": 
		[ 
  			{ 
  			"firstName" : "Sally",
  			"lastName"  : "Green",
  			"age"      : 27 
 			}, 
  			{ 
  			"firstName" : "Jim", 
  			"lastName"  : "Galley",
  			"age"       : 41 
  			}
  		],
  	    "companyName" : "True Value Corporation",
  	    "address" : "123 First Street, Newyork, NY, USA",
  	    "contactNumber" : "123456789"
}
```

### Sensitive Fields
```json
{
        "legal.firstName" : "AES:CBC",       /* legal.firstName will be encrypted using AES CBC Algorithm */
	"legal.lastName" : "FE:PersonName",  /* legal.lastName will be encrypted as a Format Preserved PERSON-NAME field */
        "legal.age" : "MASK:#",              /* legal.age will be masked as ######## */
        "contactNumber" : "FE:PhoneNumber"   /* contactNumber will be encrypted as a Format Preserved PHONE-NUMBER field */
}
```

### Output JSON
```json
{
    "legal": [
        {
            "firstName": "ooQ9OqV3wIZeG+MkEk1KFw==",
            "lastName": "Ees",
            "age": "#######################"
        },
        {
            "firstName": "/bbIa8Bzy76zsfqnUKWt7A==",
            "lastName": "Edmgy",
            "age": "#######################"
        }
    ],
    "marketing": [
        {
            "firstName": "Sally",
            "lastName": "Green",
            "age": 27
        },
        {
            "firstName": "Jim",
            "lastName": "Galley",
            "age": 41
        }
    ],
    "companyName": "True Value  Corporation",
    "address": "123 First Street, Newyork, NY, USA",
    "contactNumber": "128388658"
}
```

## Security Considerations

### Encryption Key Management
- Store encryption keys securely using MuleSoft Secure Configuration Properties
- Rotate encryption keys regularly
- Use different keys for different environments (dev, test, prod)

### API Key Security
- Store API keys in secure configuration properties
- Use environment-specific API keys
- Regularly rotate API keys

### Token Management
- Use short-lived override tokens
- Implement proper token validation
- Store tokens securely

## Best Practices

### Configuration
1. **Use Secure Properties**: Store sensitive configuration values in secure properties
2. **Environment Separation**: Use different configurations for different environments
3. **Connection Pooling**: Leverage connection pooling for remote API operations

### Data Processing
1. **Field Selection**: Only encrypt fields that contain sensitive data
2. **Tweak Values**: Use unique tweak values for each operation
3. **Error Handling**: Implement proper error handling for all operations
4. **Logging**: Enable appropriate logging for debugging and monitoring

### Performance
1. **Co-located DataBlind**: If the sensitive fields can be specified for a JSON payload, the datablind uses the Colocated library for performing the blinding. This eliminates the NLP API calling overhead. Use this approach if the sensitive fields can be specified manually.
2. **Batch Processing**: When calling the NLP enabled DataBlind URI, process multiple records in a large JSON instead of calling the API multiple times with smaller JSON with few records.

## Complete Flow Example
Please refer to the following example project:
[https://github.com/datablind/dblconnector_j17_demo](https://github.com/datablind/dblconnector_j17_demo)

## Troubleshooting

### Common Issues
1. **Encryption Key Errors**: Ensure the encryption key is properly configured and accessible
2. **Connection Errors**: Verify API URI and API key configuration for remote operations
3. **Field Mapping Errors**: Check that sensitive field names match exactly in JSON documents
4. **Tweak Value Mismatch**: Ensure the same tweak value is used for encryption and decryption

## Support
For issues and questions related to the DataBlind Connector:

- **Documentation**: Refer to this README and inline code documentation
- **Logs**: Check application logs for detailed error information
- **MuleSoft Community**: Post questions in the MuleSoft Community forums
- **Support**: Contact mulesoftconnector@ztensor.com and/or your MuleSoft support representative

## License
This connector is proprietary software owned by ZTensor, Inc. All rights reserved.

This software is licensed for commercial use only. Unauthorized copying, distribution, or use of this software, via any medium, is strictly prohibited.

For licensing information, please contact mulesoftconnector@ztensor.com.
