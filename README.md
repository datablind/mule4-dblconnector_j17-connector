# DataBlind Connector 4.0.24 Reference

The DataBlind Connector provides secure data encryption and decryption capabilities for JSON data within MuleSoft applications. This connector integrates with the DataBlind encryption framework to enable field-level encryption, decryption, and data filtering operations.

## Overview

The DataBlind Connector offers the following key capabilities:

- **JSON Field Encryption**: Encrypt specific fields within JSON documents
- **JSON Field Decryption**: Decrypt previously encrypted JSON fields
- **NLP-Based Encryption**: Use natural language processing to automatically identify, encrypt & mask sensitive fields
- **Data Filtering**: Reduce JSON data by filtering out sensitive information
- **Token Management**: Generate and manage override tokens for enhanced security

## Requirements

| Requirement | Version |
|-------------|---------|
| Mule Runtime | 4.6.0 or later |
| Java | 17 |
| DataBlind Library | 3.0.3 |

## Configurations

### Default Configuration

#### Parameters

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| **Connection** | [DataBlind Connection](#config_connection) | The connection parameters to provide to this configuration. | | ✓ |
| **Encryption Key** | String | The encryption key used for data encryption and decryption operations. | | ✓ |

#### Connection Types

##### DataBlind Connection

A connection provider that manages HTTP client connections for AI enabled remote DataBlind API operations.

###### Parameters

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| **API URI** | String | The base URI for the DataBlind API (e.g., https://host/Dev). | | No |
| **DataGuardAPI Key** | String | The API key for authentication with the DataBlind API. | | No |

## Operations

### EncryptJson

Encrypts specified fields within a JSON document using the configured encryption key.

#### Parameters

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| **Sensitive Fields** | String | Json containing sensitive fields along with their data types. |  | ✓ |
| **Sensitive JSON** | String | The JSON document containing fields to be encrypted | | ✓ |
| **Tweak** | String | A unique value used in the encryption process for additional security | | ✓ |
| **OverRide Token** | String | Optional override token, allows an authorized user to retrieve the clear data | "NOTOKEN" | No |
| **Pass Phrase** | String | Optional passphrase, allows an authorized user to retrieve the clear data | "NOPASSPHRASE" | No |

#### Example

```xml
<zt:encrypt-json config-ref="DataBlind_Config"
    sensitive-fields = "{
        'name' : 'FE:PersonName',
        'ssn' : "FE:SSN',
        'creditCard' : 'AES:CREDIT_CARD'
    }, tweak="047474" >
    <zt:sensitive-json>{"name":"John Doe","creditCard":"1234-5678-9012-3456","ssn":"123-45-6789"}</zt:sensitive-json>
</zt:encrypt-json>
```

### EncryptJsonUsingNLP

Encrypts JSON fields automatically using natural language processing to identify sensitive data.

#### Parameters

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| **Sensitive JSON** | String | The JSON document to be processed for automatic field detection and encryption | | ✓ |
| **Tweak** | String | A unique value used in the encryption process for additional security | | ✓ |
| **OverRide Token** | String | Optional override token, allows an authorized user to retrieve the clear data | "NOTOKEN" | No |
| **Pass Phrase** | String | Optional passphrase, allows an authorized user to retrieve the clear data | "NOPASSPHRASE" | No |

#### Example

```xml
<dbl:encrypt-json-using-nlp config-ref="DataBlind_Config" connection-ref="DataBlind_Connection">
    <dbl:sensitive-json>{"name":"John Doe","creditCard":"1234-5678-9012-3456","ssn":"123-45-6789"}</dbl:sensitive-json>
    <dbl:tweak>nlp-tweak-value</dbl:tweak>
</dbl:encrypt-json-using-nlp>
```

### DecryptJson

Decrypts previously encrypted fields within a JSON document.

#### Parameters

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| **Sensitive Fields** | String | Json containing sensitive fields along with their data types (must match fields used during encryption) | | ✓ |
| **Encrypted JSON** | String | The JSON document containing encrypted fields to be decrypted | | ✓ |
| **Tweak** | String | The tweak value used during encryption (must match exactly) | | ✓ |
| **OverRide Token** | String | Optional override token, allows an authorized user to retrieve the clear data | "NOTOKEN" | No |
| **Pass Phrase** | String | Optional passphrase, allows an authorized user to retrieve the clear data | "NOPASSPHRASE" | No |

#### Example

```xml
<dbl:decrypt-json config-ref="DataBlind_Config">
    <dbl:sensitive-fields>creditCard,ssn,email</dbl:sensitive-fields>
    <dbl:encrypted-json>{"name":"John Doe","creditCard":"[ENCRYPTED]","ssn":"[ENCRYPTED]"}</dbl:encrypted-json>
    <dbl:tweak>unique-tweak-value</dbl:tweak>
</dbl:decrypt-json>
```

### FilterJson

Reduces JSON data by filtering out sensitive information.

#### Parameters

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| **Sensitive Fields** | String | String containing comma separated sensitive fields (e.g., "account.creditCard,ssn,email") | | ✓ |
| **Sensitive JSON** | String | The JSON document containing fields to be filtered | | ✓ |
| **Operation** | String | The filtering operation to perform ("remove" or "retain") | | ✓ |
| **OverRide Token** | String | Optional override token, allows an authorized user to retrieve all data | "NOTOKEN" | No |
| **Pass Phrase** | String | Optional passphrase, allows an authorized user to retrieve all data | "NOPASSPHRASE" | No |

#### Example

```xml
<dbl:filter-json config-ref="DataBlind_Config">
    <dbl:sensitive-fields>account.creditCard,ssn</dbl:sensitive-fields>
    <dbl:sensitive-json>{"name":"John Doe","account":{"creditCard":"1234-5678-9012-3456"},"ssn":"123-45-6789"}</dbl:sensitive-json>
    <dbl:operation>remove</dbl:operation>
</dbl:filter-json>
```

### OverrideToken

Generates an override token for users requiring authorization to access all original data.

#### Parameters

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| **Passphrase** | String | The passphrase used to generate the token | | ✓ |
| **Expiration Seconds** | Integer | The number of seconds until the token expires | | ✓ |

#### Example

```xml
<dbl:override-token config-ref="DataBlind_Config">
    <dbl:passphrase>my-secure-passphrase</dbl:passphrase>
    <dbl:expiration-secs>3600</dbl:expiration-secs>
</dbl:override-token>
```

### OverrideTokenWithNewKey

Generates an override token using a new encryption key.

#### Parameters

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| **Key** | String | The new encryption key to use for token generation | | ✓ |
| **Passphrase** | String | The passphrase used to generate the token | | ✓ |
| **Expiration Seconds** | Integer | The number of seconds until the token expires | | ✓ |

#### Example

```xml
<dbl:override-token-with-new-key>
    <dbl:key>new-encryption-key</dbl:key>
    <dbl:passphrase>my-secure-passphrase</dbl:passphrase>
    <dbl:expiration-secs>3600</dbl:expiration-secs>
</dbl:override-token-with-new-key>
```

## Error Handling

The DataBlind Connector provides comprehensive error handling with the following error types:

- **DATACRYPT_ERROR**: General encryption/decryption operation errors
- **CONNECTION_ERROR**: Connection-related errors when using remote API
- **VALIDATION_ERROR**: Input validation errors

### Error Response Format

```json
{
  "Success": "false",
  "error": "Error description"
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

1. **Batch Processing**: Process multiple records in batches when possible
2. **Connection Reuse**: Reuse connections for multiple operations
3. **Caching**: Cache frequently used tokens and configurations

## Examples

### Complete Flow Example

```xml
<flow name="secure-data-processing">
    <http:listener config-ref="HTTP_Listener_config" path="/process-data"/>
    
    <!-- Encrypt sensitive fields -->
    <dbl:encrypt-json config-ref="DataBlind_Config">
        <dbl:sensitive-fields>creditCard,ssn,email</dbl:sensitive-fields>
        <dbl:sensitive-json>#[payload]</dbl:sensitive-json>
        <dbl:tweak>#[uuid()]</dbl:tweak>
    </dbl:encrypt-json>
    
    <!-- Store encrypted data -->
    <db:insert config-ref="Database_Config">
        <db:sql>INSERT INTO secure_data (data, tweak) VALUES (:#[payload], :#[vars.tweak])</db:sql>
    </db:insert>
    
    <!-- Return success response -->
    <ee:transform>
        <ee:message>
            <ee:set-payload><![CDATA[%dw 2.0
output application/json
---
{
    "status": "success",
    "message": "Data encrypted and stored successfully"
}]]></ee:set-payload>
        </ee:message>
    </ee:transform>
</flow>
```

### Decryption Flow Example

```xml
<flow name="data-retrieval">
    <http:listener config-ref="HTTP_Listener_config" path="/retrieve-data/{id}"/>
    
    <!-- Retrieve encrypted data -->
    <db:select config-ref="Database_Config">
        <db:sql>SELECT data, tweak FROM secure_data WHERE id = :#[attributes.uriParams.id]</db:sql>
    </db:select>
    
    <!-- Decrypt data -->
    <dbl:decrypt-json config-ref="DataBlind_Config">
        <dbl:sensitive-fields>creditCard,ssn,email</dbl:sensitive-fields>
        <dbl:encrypted-json>#[payload[0].data]</dbl:encrypted-json>
        <dbl:tweak>#[payload[0].tweak]</dbl:tweak>
    </dbl:decrypt-json>
    
    <!-- Return decrypted data -->
    <ee:transform>
        <ee:message>
            <ee:set-payload><![CDATA[%dw 2.0
output application/json
---
payload]]></ee:set-payload>
        </ee:message>
    </ee:transform>
</flow>
```

### Data Filtering Example

```xml
<flow name="data-filtering">
    <http:listener config-ref="HTTP_Listener_config" path="/filter-data"/>
    
    <!-- Filter sensitive data -->
    <dbl:filter-json config-ref="DataBlind_Config">
        <dbl:sensitive-fields>account.creditCard,ssn,email</dbl:sensitive-fields>
        <dbl:sensitive-json>#[payload]</dbl:sensitive-json>
        <dbl:operation>remove</dbl:operation>
    </dbl:filter-json>
    
    <!-- Return filtered data -->
    <ee:transform>
        <ee:message>
            <ee:set-payload><![CDATA[%dw 2.0
output application/json
---
{
    "status": "success",
    "filtered_data": payload
}]]></ee:set-payload>
        </ee:message>
    </ee:transform>
</flow>
```

## Troubleshooting

### Common Issues

1. **Encryption Key Errors**: Ensure the encryption key is properly configured and accessible
2. **Connection Errors**: Verify API URI and API key configuration for remote operations
3. **Field Mapping Errors**: Check that sensitive field names match exactly in JSON documents
4. **Tweak Value Mismatch**: Ensure the same tweak value is used for encryption and decryption

### Debugging

1. **Enable Debug Logging**: Set log level to DEBUG for detailed operation information
2. **Check Connection Status**: Use the connection validation feature to verify API connectivity
3. **Validate Input Data**: Ensure JSON documents are properly formatted
4. **Monitor Performance**: Use MuleSoft monitoring tools to track operation performance

## Support

For issues and questions related to the DataBlind Connector:

- **Documentation**: Refer to this README and inline code documentation
- **Logs**: Check application logs for detailed error information
- **MuleSoft Community**: Post questions in the MuleSoft Community forums
- **Support**: Contact your MuleSoft support representative

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 4.0.24 | Current | Latest stable release with enhanced error handling and performance improvements |
| 4.0.23 | Previous | Added NLP-based encryption capabilities |
| 4.0.22 | Previous | Improved connection management and validation |

## License

This connector is licensed under the MuleSoft connector license agreement. Please refer to the license file for detailed terms and conditions.
