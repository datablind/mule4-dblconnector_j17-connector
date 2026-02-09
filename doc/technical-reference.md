# DataBlind Connector Technical Reference

## Requirements

| Component         | Version        |
| ----------------- | -------------- |
| Mule Runtime      | 4.6.0 or later |
| Java              | 17             |
| DataBlind Library | 3.0.8          |

## Configurations

### Default Configuration

#### Parameters

| Name                   | Type                                         | Description                                                            | Default Value | Required |
| ---------------------- | -------------------------------------------- | ---------------------------------------------------------------------- | ------------- | -------- |
| **NLP API Connection** | [DataBlind Connection](#connection-types)    | The connection parameters to provide to this configuration.            | No            |          |
| **Encryption Key**     | String                                       | The encryption key used for data encryption and decryption operations. | ✓             |          |

#### Connection Types

##### DataBlind Connection

A connection provider that manages HTTP client connections for AI enabled remote DataBlind API operations.

###### Parameters

| Name                 | Type     | Description                                                    | Default Value | Required |
| -------------------- | -------- | -------------------------------------------------------------- | ------------- | -------- |
| **NLP API URI**      | String   | The base URI for the DataBlind API (e.g., https://host/Dev).   | No            |          |
| **NLP API Key**      | String   | The API key for authentication with the DataBlind API.         | No            |          |
| **API Timeout**      | Integer  | HTTP Request Timeout (in milliseconds)                         | 30000         | No       |
| **API Timeout Unit** | TimeUnit | API Request Timeout Unit                                       | MILLISECONDS  | No       |

## Operations

### EncryptJson
Encrypts specified fields within a JSON document using the configured encryption key.

#### Parameters

| Name                 | Type   | Description                                                                   | Default Value  | Required |
| -------------------- | ------ | ----------------------------------------------------------------------------- | -------------- | -------- |
| **Sensitive Fields** | String | Json containing sensitive fields along with their data types.                 | ✓              |          |
| **Sensitive JSON**   | Binary | The JSON document containing fields to be encrypted                           | ✓              |          |
| **Tweak**            | String | A unique value used in the encryption process for additional security         | ✓              |          |
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
| **Sensitive JSON** | Binary | The JSON document to be processed for automatic field detection and encryption | ✓              |          |
| **Tweak**          | String | A unique value used in the encryption process for additional security          | ✓              |          |
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
| **Sensitive Fields** | String | String containing comma separated sensitive fields (e.g., "account.creditCard,ssn,email") | ✓              |          |
| **Sensitive JSON**   | Binary | The JSON document containing fields to be filtered                                        | ✓              |          |
| **Operation**        | String | The filtering operation to perform ("remove" or "retain")                                 | ✓              |          |
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
| **Passphrase**         | String  | The passphrase used to generate the token     | ✓             |          |
| **Expiration Seconds** | Integer | The number of seconds until the token expires | ✓             |          |

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
| **Key**                | String  | The new encryption key to use for token generation | ✓             |          |
| **Passphrase**         | String  | The passphrase used to generate the token          | ✓             |          |
| **Expiration Seconds** | Integer | The number of seconds until the token expires      | ✓             |          |

#### Example
```xml
<zt:override-token-with-new-key
    key="1234567890123456" passPhrase="my-secure-passphrase" expirationSecs="3600" />
```

## Error Handling

The DataBlind Connector provides comprehensive error handling with the following error types:

- **DATACRYPT_ERROR**: General encryption/decryption operation errors
- **INVALID_PARAMETER**: Input validation errors
- **TIME_OUT**: Connection-related timeout errors
- **NOT_ALLOWED**: Authorization or policy violations
- **CONNECTIVITY**: Connection-related errors when using remote API

### Error Response Format
```json
{
  "Success": "false",
  "error": "Error description"
}
```

### Supported Data Types

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
