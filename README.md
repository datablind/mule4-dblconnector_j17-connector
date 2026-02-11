# DataBlind Connector 5.0

This document provides comprehensive information about the DataBlind Connector, including release history, technical specifications, and usage instructions.

## Table of Contents
[1. Release Notes](#1-release-notes)  
[2. Technical Reference](#2-technical-reference)  
[3. User Manual](#3-user-manual)  
[4. Demo Application](#4-demo-application)  

---

<a name="1-release-notes"></a>
# 1. Release Notes

## Version History

| Version | Date       | Changes                                                                          |
| ------- | ---------- | -------------------------------------------------------------------------------- |
| 1.0.0   | 2026-02-11 | Connector Cerification changes, Updated to Java 17, Optimized connection management, AI/NLP based PII detection. |

---

<a name="2-technical-reference"></a>
# 2. Technical Reference

## Requirements

| Component         | Version        |
| ----------------- | -------------- |
| Mule Runtime      | 4.6.0 or later |
| Java              | 17             |
| DataBlind Library | 3.0.8          |

Add this dependency to your application pom.xml
```

		<dependency>
			<groupId>org.ztensor.connectors</groupId>
			<artifactId>mule-datablind-connector</artifactId>
			<version>1.0.0</version>
			<classifier>mule-plugin</classifier>
		</dependency>
```
## Configurations

### Default Configuration

#### Parameters

| Name                   | Type                                         | Description                                                            | Default Value | Required |
| ---------------------- | -------------------------------------------- | ---------------------------------------------------------------------- | ------------- | -------- |
| **NLP API Connection** | [DataBlind Connection](#connection-types)    | Configure this section to enable AI/NLP based PII identification.            | No            |          |
| **Encryption Key**     | String                                       | The encryption key used for data encryption and decryption operations. | ✓             |          |

#### Connection Types

##### DataBlind Connection

A connection provider that manages HTTP client connections for AI enabled remote DataBlind API operations.

###### Parameters

| Name                 | Type     | Description                                                    | Default Value | Required |
| -------------------- | -------- | -------------------------------------------------------------- | ------------- | -------- |
| **NLP API URI**      | String   | DataBlind API (e.g., https://host/Dev).   |             |          |
| **NLP API Key**      | String   | DataBlind API Key.         |             |          |
| **API Timeout**      | Integer  | HTTP Request Timeout (in milliseconds)                         | 30000         | No       |
| **API Timeout Unit** | TimeUnit | API Request Timeout Unit                                       | MILLISECONDS  | No       |

## Operations

### EncryptJson
Encrypts specified fields within a JSON document using the configured encryption key.

#### Parameters

| Name                 | Type   | Description                                                                   | Default Value  | Required |
| -------------------- | ------ | ----------------------------------------------------------------------------- | -------------- | -------- |
| **Sensitive Fields** | String | Json containing sensitive fields along with their data types.                 |               |          |
| **Sensitive JSON**   | Binary | The JSON document containing fields to be encrypted                           |               |          |
| **Tweak**            | String | A unique value used in the encryption process for additional security         |               |          |
| **OverRide Token**   | String | Optional override token, allows an authorized user to retrieve the clear data | "NOTOKEN"      | No       |
| **Pass Phrase**      | String | Optional passphrase, allows an authorized user to retrieve the clear data     | "NOPASSPHRASE" | No       |

#### Example
```xml
<zt:encrypt-json config-ref="DataBlind_Config"
    sensitiveFields = "{'name' : 'FE:PersonName', 'ssn' : 'FE:SSN', 'creditCard' : 'AES:CREDIT_CARD'}" 
    tweak="047474" >
    <zt:sensitive-json>{"name":"John Doe","creditCard":"1234-5678-9012-3456","ssn":"123-45-6789"}</zt:sensitive-json>
</zt:encrypt-json>
```

### EncryptJsonUsingNLP
Encrypts JSON fields automatically using natural language processing to identify sensitive data.

#### Parameters

| Name               | Type   | Description                                                                    | Default Value  | Required |
| ------------------ | ------ | ------------------------------------------------------------------------------ | -------------- | -------- |
| **Sensitive JSON** | Binary | The JSON document to be processed for automatic field detection and encryption |               |          |
| **Tweak**          | String | A unique value used in the encryption process for additional security          |               |          |
| **OverRide Token** | String | Optional override token, allows an authorized user to retrieve the clear data  | "NOTOKEN"      | No       |
| **Pass Phrase**    | String | Optional passphrase, allows an authorized user to retrieve the clear data      | "NOPASSPHRASE" | No       |

#### Example
```xml
<zt:encrypt-json-using-nlp config-ref="DataBlind_Config"
    tweak="047474">
    <zt:sensitive-json>{"name":"John Doe","creditCard":"1234-5678-9012-3456","ssn":"123-45-6789"}</zt:sensitive-json>
</zt:encrypt-json-using-nlp>
```

### DecryptJson
Decrypts previously encrypted fields within a JSON document.

#### Parameters

| Name                 | Type   | Description                                                                                             | Default Value  | Required |
| -------------------- | ------ | ------------------------------------------------------------------------------------------------------- | -------------- | -------- |
| **Sensitive Fields** | String | Json containing sensitive fields along with their data types (must match fields used during encryption) | ✓              |          |
| **Encrypted JSON**   | Binary | The JSON document containing encrypted fields to be decrypted                                           | ✓              |          |
| **Tweak**            | String | The tweak value used during encryption (must match exactly)                                             | ✓              |          |
| **OverRide Token**   | String | Optional override token, allows an authorized user to retrieve the clear data                           | "NOTOKEN"      | No       |
| **Pass Phrase**      | String | Optional passphrase, allows an authorized user to retrieve the clear data                               | "NOPASSPHRASE" | No       |

#### Example
```xml
<zt:decrypt-json config-ref="DataBlind_Config"
    sensitiveFields = "{'name' : 'FE:PersonName', 'ssn' : 'FE:SSN', 'creditCard' : 'AES:CREDIT_CARD'}" 
    tweak="047474">
    <zt:encrypted-json>{"name":"John Doe","creditCard":"[ENCRYPTED]","ssn":"[ENCRYPTED]"}</zt:encrypted-json>
</zt:decrypt-json>
```

### reduceJson
Reduces JSON data by filtering out sensitive information.

#### Parameters

| Name                 | Type   | Description                                                                               | Default Value  | Required |
| -------------------- | ------ | ----------------------------------------------------------------------------------------- | -------------- | -------- |
| **Sensitive Fields** | String | String containing comma separated sensitive fields (e.g., "account.creditCard,ssn,email") |               |          |
| **Sensitive JSON**   | Binary | The JSON document containing fields to be filtered                                        |               |          |
| **Operation**        | String | The filtering operation to perform ("remove" or "retain")                                 |               |          |
| **OverRide Token**   | String | Optional override token, allows an authorized user to retrieve all data                   | "NOTOKEN"      | No       |
| **Pass Phrase**      | String | Optional passphrase, allows an authorized user to retrieve all data                       | "NOPASSPHRASE" | No       |

#### Example
```xml
<zt:reduce-json config-ref="DataBlind_Config"
    sensitiveFields="account.creditCard,ssn,email" operation="remove">
    <zt:sensitive-json>{"name":"John Doe","account":{"creditCard":"1234-5678-9012-3456"},"ssn":"123-45-6789"}</zt:sensitive-json>
</zt:reduce-json>
```

### OverrideToken
Generates an override token for users requiring authorization to access all original data.

#### Parameters

| Name                   | Type    | Description                                   | Default Value | Required |
| ---------------------- | ------- | --------------------------------------------- | ------------- | -------- |
| **Passphrase**         | String  | The passphrase used to generate the token     |              |   Yes       |
| **Expiration Seconds** | Integer | The number of seconds until the token expires |              |   Yes       |

#### Example
```xml
<zt:override-token config-ref="DataBlind_Config"
    passPhrase="my-secure-passphrase" expirationSecs="3600" />
```

### OverrideTokenWithNewKey
Generates an override token using a new encryption key.

#### Parameters

| Name                   | Type    | Description                                        | Default Value | Required |
| ---------------------- | ------- | -------------------------------------------------- | ------------- | -------- |
| **Key**                | String  | The new encryption key to use for token generation |              |   Yes       |
| **Passphrase**         | String  | The passphrase used to generate the token          |              |   Yes       |
| **Expiration Seconds** | Integer | The number of seconds until the token expires      |              |   Yes       |

#### Example
```xml
<zt:override-token-with-new-key
    key="1234567890123456" passPhrase="my-secure-passphrase" expirationSecs="3600" />
```

## Error Handling

The DataBlind Connector throws the following error types:

- **DATACRYPT_ERROR**: General encryption/decryption operation errors
- **INVALID_PARAMETER**: Input validation errors
- **TIME_OUT**: Connection-related timeout errors
- **NOT_ALLOWED**: Authorization or policy violations
- **CONNECTIVITY**: Connection-related errors when using remote API

In some rare scenarios, the operation can not be completed and an error is not thrown, the following response is returned:

```json
{
  "Success": "false",
  "error": "Error description"
}
```

### Supported Encryptable Data Types

| Category | Data Type | Description |
|----------|-----------|-------------|
| **Format-Preserving (FE:)** | `FE:AlphaNumeric` | Alphanumeric strings |
| | `FE:Numeric` | Numeric strings |
| | `FE:PhoneNumber` | Phone numbers |
| | `FE:PersonName` | Person names |
| | `FE:StreetAddress` | Street addresses |
| | `FE:StreetAddressNice` | Street addresses (formatted) |
| | `FE:Date-MM/dd/yyyy` | Dates in MM/dd/yyyy |
| | `FE:DateTime-MM/dd/yyyy HH:mm:ss` | Date/time in MM/dd/yyyy HH:mm:ss |
| | `FE:MultiFormatDateTime` | Date/time in multiple formats |
| | `FE:IP-Address-v4` | IPv4 addresses |
| | `FE:GPS` | GPS coordinates |
| | `FE:UPC-A` | UPC-A barcodes |
| | `FE:CreditCardNumber` | Credit card numbers |
| | `FE:SSN` | Social Security numbers |
| | `FE:CurrencyAmount` | Currency amounts |
| | `FE:EmailAddress` | Email addresses |
| | `FE:WebUrl` | Web URLs |
| **Other** | `AES:CBC` | AES-128 CBC encryption (non-format-preserving) |
| **Masking** | `MASK:X` | Replaces each character with X (e.g. MASK:* → ****) |

*Total: 18 FE types, 1 AES type, and the MASK:X pattern.*

---

<a name="3-user-manual"></a>
# 3. User Manual

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
## Configuration Samples

### Connector Configuration

![Concept](/assets/DataBlindConfig1.png)

### Blind JSON

![Concept](/assets/blindJson.png)

### Blind JSON using NLP

![Concept](/assets/blindJsonNLP.png)

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



## Troubleshooting

### Common Issues
1. **Encryption Key Errors**: Ensure the encryption key is properly configured and accessible
2. **Connection Errors**: Verify API URI and API key configuration if the connctor is using AI/NLP based PII identification and encryption.
3. **Field Mapping Errors**: Check that sensitive field names match exactly in JSON documents
4. **Tweak Value Mismatch**: Ensure the same tweak value is used for encryption and decryption

## Support
For issues and questions related to the DataBlind Connector:

- **Logs**: Check application logs for detailed error information
- **MuleSoft Community**: Post questions in the MuleSoft Community forums
- **Support**: Contact mulesoftconnector@ztensor.com and/or your MuleSoft support representative

<a name="4-demo-application"></a>
## 4. Demo Application
Please refer to the following example project:
[https://github.com/datablind/dblconnector_j17_demo](https://github.com/datablind/dblconnector_j17_demo)

## License
This connector is proprietary software owned by ZTensor, Inc. All rights reserved.

This software is licensed for commercial use only. Unauthorized copying, distribution, or use of this software, via any medium, is strictly prohibited.

For licensing information, please contact mulesoftconnector@ztensor.com.
