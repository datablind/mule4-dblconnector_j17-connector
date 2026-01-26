/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */
package org.mule.extension.zt.internal.operations;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;
import org.mule.runtime.extension.api.annotation.values.OfValues;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.execution.Execution;


import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import java.util.Arrays;


import com.ztensor.datacrypt.DataCrypt;
import com.ztensor.util.json.JsonDataCrypt;

import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.client.HttpRequestOptions;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.mule.runtime.http.api.domain.entity.ByteArrayHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.extension.zt.api.DBLErrors;
import org.mule.extension.zt.internal.error.provider.DBLErrorProvider;
import org.mule.extension.zt.internal.configuration.DBLConfiguration;
import org.mule.extension.zt.internal.connection.DBLConnection;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
/**
 * Container class for DataBlind connector operations.
 * 
 * <p>This class provides operations for secure data encryption, decryption, and filtering
 * using the DataBlind encryption framework. All public methods in this class are exposed
 * as MuleSoft connector operations.</p>
 * 
 * <p>The operations support both local encryption using the DataBlind library and remote
 * processing through the DataBlind API service.</p>
 * 
 * <p>Key features include:</p>
 * <ul>
 *   <li>Field-level JSON encryption and decryption</li>
 *   <li>Natural Language Processing (NLP) based automatic field detection</li>
 *   <li>JSON data filtering and masking</li>
 *   <li>Override token generation and management</li>
 * </ul>
 * 
 * @version 3.0.0
 * @since 4.0.24
 * @see DBLConfiguration
 * @see DBLConnection
 * @see DBLErrorProvider
 */
@ErrorTypes(DBLErrors.class)
public class DBLOperations {

  private static final Logger LOGGER = LoggerFactory.getLogger(DBLOperations.class);

  private static String versionTag = "v3.0.0";

  private static String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";

  /**
   * Encrypts specified fields within a JSON document using the configured encryption key.
   * 
   * <p>This operation performs field-level encryption on JSON data, allowing selective
   * encryption of sensitive information while preserving the structure of the document.
   * The encryption uses the DataBlind framework with AES encryption and supports
   * additional security features like tweak values and override tokens.</p>
   * 
   * <p>On success, the operation returns a JSON response with the encrypted data in the response payload.</p>
   * 
   * <p> On failure, the operation throws ModuleException with exception DATACRYPT_ERROR</p>
   * 
   * @param configuration The DataBlind configuration containing the encryption key
   * @param sensitiveFields Json containing sensitive fields along with their data types (e.g., "creditCard,ssn,email")
   * @param sensitiveJson The JSON document containing fields to be encrypted
   * @param tweak A unique value used in the encryption process for additional security
   * @param overRideToken Optional override token, allows an authorized user to retrieve the clear data (default: "NOTOKEN")
   * @param passPhrase Optional passphrase, allows an authorized user to retrieve the clear data (default: "NOPASSPHRASE")
   * @return InputStream containing the encrypted and protected values as JSON
   * @throws ModuleException if encryption fails due to invalid parameters, key issues, or processing errors
   * @see DBLConfiguration#getEncryptionKey()
   */
  @Execution(ExecutionType.CPU_INTENSIVE)
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EncryptJson")
  @Throws(DBLErrorProvider.class)
  public InputStream encryptJson(@Config DBLConfiguration configuration,
		  @DisplayName("Sensitive Fields") @Expression(ExpressionSupport.SUPPORTED) String sensitiveFields,
		  @Content @DisplayName("Sensitive JSON") InputStream sensitiveJson,
		  @DisplayName("Tweak") @Expression(ExpressionSupport.SUPPORTED) String tweak, 
  		  @DisplayName("OverRide Token") 
  		  @Expression(ExpressionSupport.SUPPORTED) 
  		  @Optional(defaultValue = "NOTOKEN")
  		  @Placement(order = 1, tab="Advanced") String overRideToken, 
  		  @DisplayName("Pass Phrase") 
  	  	  @Expression(ExpressionSupport.SUPPORTED) 
  		  @Password 
  		  @Optional(defaultValue = "NOPASSPHRASE")
  		  @Placement(order = 2, tab="Advanced") String passPhrase) {
    //String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
	LOGGER.info(versionTag + " DataBlind EncryptJson" );    	

    try {    
        String sensitiveJsonString = new String(sensitiveJson.readAllBytes(), StandardCharsets.UTF_8);
        KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", configuration.getEncryptionKey().getBytes());
    	JsonDataCrypt jsonDataCrypt = new JsonDataCrypt(kc);
    	response = jsonDataCrypt.transform( "Encrypt", tweak, sensitiveJsonString, sensitiveFields, overRideToken, passPhrase);
    }
    catch (Exception e) {
    	LOGGER.error("Excception, encryptJson failed " + e);
    	LOGGER.error(Arrays.toString(e.getStackTrace()));
    	throw new ModuleException("ERR_101: Operation encryptJson failed due to " + e , DBLErrors.DATACRYPT_ERROR);

    }
    return new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Encrypts JSON fields automatically using Natural Language Processing (NLP) to identify sensitive data.
   * 
   * <p>This operation leverages the DataBlind API service to automatically detect and encrypt
   * sensitive fields within JSON documents using NLP algorithms. The service analyzes field names
   * and content patterns to identify potentially sensitive information without requiring explicit
   * field specification.</p>
   * 
   * <p>On success, the operation returns a JSON response with the automatically encrypted data in the response payload.</p>
   * 
   * <p>On failure, the operation throws ModuleException with exception DATACRYPT_ERROR</p>
   * 
   * @param connection The DataBlind connection providing API access
   * @param configuration The DataBlind configuration containing the encryption key
   * @param sensitiveJson The JSON document to be processed for automatic field detection and encryption
   * @param tweak A unique value used in the encryption process for additional security
   * @param overRideToken Optional override token, allows an authorized user to retrieve the clear data (default: "NOTOKEN")
   * @param passPhrase Optional passphrase, allows an authorized user to retrieve the clear data (default: "NOPASSPHRASE")
   * @return InputStream containing the automatically encrypted and protected values as JSON
   * @throws ModuleException if encryption fails due to API communication issues, invalid parameters, or processing errors
   * @see DBLConnection#getApiUri()
   * @see DBLConnection#getApiKey()
   * @see DBLConnection#getHttpClient()
   */
  @Execution(ExecutionType.BLOCKING)
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EncryptJsonUsingNLP")
  @Throws(DBLErrorProvider.class)
  public InputStream encryptJsonUsingNLP(@Connection DBLConnection connection, @Config DBLConfiguration configuration,
		  @Content @DisplayName("Sensitive JSON") InputStream sensitiveJson,
		  @DisplayName("Tweak") @Expression(ExpressionSupport.SUPPORTED) String tweak, 
  		  @DisplayName("OverRide Token") 
  		  @Expression(ExpressionSupport.SUPPORTED) 
  		  @Optional(defaultValue = "NOTOKEN")
  		  @Placement(order = 1, tab="Advanced") String overRideToken, 
  		  @DisplayName("Pass Phrase") 
  	  	  @Expression(ExpressionSupport.SUPPORTED) 
  		  @Password 
  		  @Optional(defaultValue = "NOPASSPHRASE")
  		  @Placement(order = 2, tab="Advanced") String passPhrase) {
	//String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
	LOGGER.info(versionTag + " DataBlind EncryptJsonUsingNLP" );    	
    try {    
        String sensitiveJsonString = new String(sensitiveJson.readAllBytes(), StandardCharsets.UTF_8);
        String jsonPayload = String.format(
        	    "{\n" +
        	    "  \"key\": \"%s\",\n" +
        	    "  \"tweak\": \"%s\",\n" +
        	    "  \"data\": %s,\n" +
        	    "  \"overRideToken\": \"%s\",\n" +
        	    "  \"overRidePassPhrase\": \"%s\"\n" +
        	    "}", configuration.getEncryptionKey(), tweak, sensitiveJsonString, overRideToken, passPhrase);
    	
        // Build HTTP request using the configured timeout
        HttpRequestOptions requestOptions = HttpRequestOptions.builder()
            .responseTimeout(connection.getApiRequestTimeout())
            .build();
        HttpRequest request = HttpRequest.builder()
            .method("POST")
            .uri(connection.getApiUri() + "/datacrypt-nlp")
            .addHeader("Content-Type", "application/json")
            .addHeader("x-api-key", connection.getApiKey())
            .entity(new ByteArrayHttpEntity(jsonPayload.getBytes()))
            .build();

            
        // Execute request
        HttpResponse httpResponse = connection.getHttpClient().send(request, requestOptions);
        response = new String(httpResponse.getEntity().getContent().readAllBytes());
        if (httpResponse.getStatusCode() != 200) {
            throw new Exception("Error Response " + response );
        }
    }
    catch (Exception e) {
    	LOGGER.error("Excception, encryptJsonUsingNLP failed " + e);
    	LOGGER.error(Arrays.toString(e.getStackTrace()));
    	throw new ModuleException("ERR_102: Operation encryptJsonUsingNLP failed due to " + e , DBLErrors.DATACRYPT_ERROR);
    }
    return new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Reduces JSON data by filtering out sensitive information.
   * 
   * <p>This operation provides data reduction capabilities by applying various filtering operations
   * to sensitive fields within JSON documents. Supported operations include remove and retain. 
   * Use remove operation to remove the sensitive fields from the JSON document. Use retain operation to 
   * retain the nonsensitive fields in the JSON document. </p>
   * 
   * <p>On success, the operation returns a JSON response with the filtered data in the response payload.</p>
   * 
   * <p>On failure, the operation throws ModuleException with exception DATACRYPT_ERROR</p>
   * 
   * @param configuration The DataBlind configuration containing the encryption key
   * @param sensitiveFields String containing comma separated sensitive fields (e.g., "accounnt.creditCard,ssn,email")
   * @param sensitiveJson The JSON document containing fields to be filtered
   * @param operation The filtering operation to perform ("remove" or "retain")
   * @param overRideToken Optional override token, allows an authorized user to retrieve all data (default: "NOTOKEN")
   * @param passPhrase Optional passphrase, allows an authorized user to retrieve all data (default: "NOPASSPHRASE")
   * @return InputStream containing the filtered JSON data
   * @throws ModuleException if filtering fails due to invalid parameters, operation issues, or processing errors
   * @see DBLConfiguration#getEncryptionKey()
   */
  @Execution(ExecutionType.CPU_INTENSIVE)
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("reduceJson")
  @Throws(DBLErrorProvider.class)
  public InputStream filterJson(@Config DBLConfiguration configuration,
		  @DisplayName("Sensitive Fields") @Expression(ExpressionSupport.SUPPORTED) String sensitiveFields,
		  @Content @DisplayName("Sensitive JSON") InputStream sensitiveJson,
		  @DisplayName("Operation") @Expression(ExpressionSupport.SUPPORTED) String operation,
  		  @DisplayName("OverRide Token") 
  		  @Expression(ExpressionSupport.SUPPORTED) 
  		  @Optional(defaultValue = "NOTOKEN")
  		  @Placement(order = 1, tab="Advanced") String overRideToken, 
  		  @DisplayName("Pass Phrase") 
  	  	  @Expression(ExpressionSupport.SUPPORTED) 
  		  @Password 
  		  @Optional(defaultValue = "NOPASSPHRASE")
  		  @Placement(order = 2, tab="Advanced") String passPhrase) {
	//String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
	LOGGER.info(versionTag + " DataBlind ReduceJson" );    	
    try {    
        String sensitiveJsonString = new String(sensitiveJson.readAllBytes(), StandardCharsets.UTF_8);
        KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", configuration.getEncryptionKey().getBytes());
        JsonDataCrypt jsonDataCrypt = new JsonDataCrypt(kc);
    	response = jsonDataCrypt.reduceJson( operation, sensitiveJsonString, sensitiveFields, overRideToken, passPhrase);
    }
    catch (Exception e) {
    	LOGGER.error("Excception, filterJson failed " + e);
    	LOGGER.error(Arrays.toString(e.getStackTrace()));
    	throw new ModuleException("ERR_103: Operation filterJson failed due to " + e , DBLErrors.DATACRYPT_ERROR);
    }
    return new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Decrypts previously encrypted fields within a JSON document.
   * 
   * <p>This operation reverses the encryption process performed by the encryptJson operation,
   * restoring the original plaintext values of encrypted fields. The operation requires
   * the same encryption key, tweak value, and optional parameters that were used during
   * the original encryption process.</p>
   * 
   * <p>On success, the operation returns a JSON response with the decrypted data in the response payload.</p>
   * 
   * <p>On failure, the operation throws ModuleException with exception DATACRYPT_ERROR</p>
   * 
   * @param configuration The DataBlind configuration containing the encryption key (must match the key used for encryption)
   * @param sensitiveFields Json containing sensitive fields along with their data types (must match fields used during encryption)
   * @param encryptedJson The JSON document containing encrypted fields to be decrypted
   * @param tweak The tweak value used during encryption (must match exactly)
   * @param overRideToken Optional override token, allows an authorized user to retrieve the clear data (default: "NOTOKEN")
   * @param passPhrase Optional passphrase, allows an authorized user to retrieve the clear data (default: "NOPASSPHRASE")
   * @return InputStream containing the decrypted and clear values as JSON
   * @throws ModuleException if decryption fails due to key mismatch, invalid parameters, or processing errors
   * @see DBLConfiguration#getEncryptionKey()
   * @see #encryptJson(DBLConfiguration, String, InputStream, String, String, String)
   */
  @Execution(ExecutionType.CPU_INTENSIVE)
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("DecryptJson")
  @Throws(DBLErrorProvider.class)
  public InputStream decryptJson(@Config DBLConfiguration configuration,
		  @DisplayName("Sensitive Fields") @Expression(ExpressionSupport.SUPPORTED) String sensitiveFields,
		  @Content @DisplayName("Encrypted JSON") InputStream encryptedJson,
		  @DisplayName("Tweak") @Expression(ExpressionSupport.SUPPORTED) String tweak, 
		  @DisplayName("OverRide Token") 
		  @Expression(ExpressionSupport.SUPPORTED) 
		  @Optional(defaultValue = "NOTOKEN")
		  @Placement(order = 1, tab="Advanced") String overRideToken, 
		  @DisplayName("Pass Phrase") 
	  	  @Expression(ExpressionSupport.SUPPORTED) 
		  @Password 
		  @Optional(defaultValue = "NOPASSPHRASE")
		  @Placement(order = 2, tab="Advanced") String passPhrase) {
	//String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
	LOGGER.info(versionTag + " DataBlind DecryptJson" );    	
    try {  
        String encryptedJsonString = new String(encryptedJson.readAllBytes(), StandardCharsets.UTF_8);
        KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", configuration.getEncryptionKey().getBytes());
    	JsonDataCrypt jsonDataCrypt = new JsonDataCrypt(kc);
    	response = jsonDataCrypt.transform( "Decrypt", tweak, encryptedJsonString, sensitiveFields, overRideToken, passPhrase);
    }
    catch (Exception e) {
    	LOGGER.error("Excception, decryptJson failed " + e);
    	LOGGER.error(Arrays.toString(e.getStackTrace()));
    	throw new ModuleException("ERR_104: Operation decryptJson failed due to " + e , DBLErrors.DATACRYPT_ERROR);
    }
    return new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generates an override token for users requiring authorization to access to all oroginal data.
   * 
   * <p>This operation creates a time-limited HMAC token that can be used to override
   * certain security restrictions or provide additional authorization for sensitive
   * operations. The token is generated using the configured encryption key and includes
   * an expiration time to ensure security.</p>
   * 
   * <p>On success, the operation returns a JSON response with the generated token in the response payload.</p>
   * 
   * <p>On failure, the operation throws ModuleException with exception DATACRYPT_ERROR</p>
   * 
   * @param configuration The DataBlind configuration containing the encryption key used for token generation
   * @param passPhrase The passphrase used to generate the token
   * @param expirationSecs The number of seconds until the token expires 
   * @return InputStream containing the generated token as JSON
   * @throws ModuleException if token generation fails due to invalid parameters, key issues, or processing errors
   * @see DBLConfiguration#getEncryptionKey()
   * @see #encryptJson(DBLConfiguration, String, InputStream, String, String, String)
   * @see #decryptJson(DBLConfiguration, String, InputStream, String, String, String)
   */
 @Execution(ExecutionType.CPU_INTENSIVE)
 @MediaType(value = ANY, strict = false)
 @Alias("OverrideToken")
 @Throws(DBLErrorProvider.class)
 public InputStream overrideToken(@Config DBLConfiguration configuration,
		  @DisplayName("Passphrase") @Expression(ExpressionSupport.SUPPORTED) String passPhrase,
		  @DisplayName("Expiration Seconds") @Expression(ExpressionSupport.SUPPORTED) Integer expirationSecs) {
   //String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
   LOGGER.info(versionTag + " DataBlind OverrideToken" );    	
   try {    
           /*
        Verify if the key is 16 chars long. If not, throw an exception.
        */
        if (configuration.getEncryptionKey().length() != 16) {
            throw new Exception("key length must be 16 chars");
           }
    
       KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", configuration.getEncryptionKey().getBytes());
       HmacToken HmacToken = new HmacToken();
   	   response = HmacToken.generateToken( kc, passPhrase, expirationSecs.intValue());
   }
   catch (Exception e) {
	LOGGER.error("Excception, overrideToken failed " + e);
   	LOGGER.error(Arrays.toString(e.getStackTrace()));
	throw new ModuleException("ERR_105: Operation overrideToken failed due to " + e , DBLErrors.DATACRYPT_ERROR);
   }
   return new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
 }

  /**
   * Generates an override token using a new encryption key.
   * 
   * <p>This operation creates a time-limited HMAC token using a specified encryption key
   * rather than the configuration's default key. This allows for dynamic key management
   * and the creation of tokens with different security contexts.</p>
   * 
   * <p>On success, the operation returns a JSON response with the generated token in the response payload.</p>
   * 
   * <p>On failure, the operation throws ModuleException with exception DATACRYPT_ERROR</p>
   * 
   * @param key The encryption key to use for token generation (must be valid and accessible)
   * @param passPhrase The passphrase used to generate the token
   * @param expirationSecs The number of seconds until the token expires (must be positive)
   * @return InputStream containing the generated token as JSON
   * @throws ModuleException if token generation fails due to invalid key, parameters, or processing errors
   * @see #overrideToken(DBLConfiguration, String, Integer)
   */
 @Execution(ExecutionType.CPU_INTENSIVE)
 @MediaType(value = ANY, strict = false)
 @Alias("OverrideTokenWithNewKey")
 @Throws(DBLErrorProvider.class)
 public InputStream overrideTokenWithNewKey(
		  @DisplayName("Key") @Expression(ExpressionSupport.SUPPORTED) String key,
		  @DisplayName("Passphrase") @Expression(ExpressionSupport.SUPPORTED) String passPhrase,
		  @DisplayName("Expiration Seconds") @Expression(ExpressionSupport.SUPPORTED) Integer expirationSecs) {
   //String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
   LOGGER.info(versionTag + " DataBlind OverrideTokenWithNewKey" );    	
   try {    
       /*
        Verify if the key is 16 chars long. If not, throw an exception.
        */
       if (key.length() != 16) {
        throw new Exception("key length must be 16 chars");
       }
       KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", key.getBytes());
       HmacToken HmacToken = new HmacToken();
   	   response = HmacToken.generateToken( kc, passPhrase, expirationSecs.intValue());
   }
   catch (Exception e) {
	LOGGER.error("Excception, overrideTokenWithNewKey failed " + e);
   	LOGGER.error(Arrays.toString(e.getStackTrace()));
	throw new ModuleException("ERR_106: Operation overrideTokenWithNewKey failed due to " + e , DBLErrors.DATACRYPT_ERROR);
   }
   return new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
 }

}
