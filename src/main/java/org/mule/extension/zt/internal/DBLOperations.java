package org.mule.extension.zt.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;
import org.mule.runtime.extension.api.annotation.values.OfValues;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.values.OfValues;


import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.api.meta.ExpressionSupport;
import com.ztensor.datacrypt.*;
import com.ztensor.util.json.*;

import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.mule.runtime.http.api.domain.entity.ByteArrayHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class DBLOperations {

  private final Logger LOGGER = LoggerFactory.getLogger(DBLOperations.class);

  private static String versionTag = "v3.0.0";
	
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EncryptJson")
  public String encryptJson(@Config DBLConfiguration configuration,
		  @DisplayName("Sensitive Fields") @Expression(ExpressionSupport.SUPPORTED) String sensitiveFields,
		  @Content @DisplayName("Sensitive JSON") @Expression(ExpressionSupport.SUPPORTED) String sensitiveJson,
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
    String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
	LOGGER.info(versionTag + " DataBlind EncryptJson" );    	

    try {    
        KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", configuration.getEncryptionKey().getBytes());
    	JsonDataCrypt jsonDataCrypt = new JsonDataCrypt(kc);
    	response = jsonDataCrypt.transform( "Encrypt", tweak, sensitiveJson, sensitiveFields, overRideToken, passPhrase);
    }
    catch (Exception e) {
    	LOGGER.error("Excception, encryptJson failed " + e);
        //response = "{ 'Success' : 'false', 'error' : " + e + "}";
    	LOGGER.error(e.getStackTrace().toString());
    	throw new ModuleException("Operation encryptJson failed due to " + e , DBLErrorProvider.DATACRYPT_ERROR);

    }
    return response;
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("EncryptJsonUsingNLP")
  public String encryptJsonUsingNLP(@Connection DBLConnection connection, @Config DBLConfiguration configuration,
		  @Content @DisplayName("Sensitive JSON") @Expression(ExpressionSupport.SUPPORTED) String sensitiveJson,
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
	String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
	LOGGER.info(versionTag + " DataBlind EncryptJsonUsingNLP" );    	
    try {    
       
        String jsonPayload = String.format(
        	    "{\n" +
        	    "  \"key\": \"%s\",\n" +
        	    "  \"tweak\": \"%s\",\n" +
        	    "  \"data\": %s,\n" +
        	    "  \"overRideToken\": \"%s\",\n" +
        	    "  \"overRidePassPhrase\": \"%s\"\n" +
        	    "}", configuration.getEncryptionKey(), tweak, sensitiveJson, overRideToken, passPhrase);
    	
        // Create Mule HTTP client
/*          HttpClientConfiguration clientConfig = new HttpClientConfiguration.Builder()
            .setName("datablind-http-client")
            .build();
        HttpClient httpClient = httpService.getClientFactory().create(clientConfig);
		httpClient.start();
*/        
        // Build HTTP request using the correct Mule API
        HttpRequest request = HttpRequest.builder()
            .method("POST")
            .uri(connection.getApiUri() + "/datacrypt-nlp")
            .addHeader("Content-Type", "application/json")
            .addHeader("x-api-key", connection.getApiKey())
            .entity(new ByteArrayHttpEntity(jsonPayload.getBytes()))
            .build();

        // Execute request
        HttpResponse httpResponse = connection.getHttpClient().send(request);
        response = new String(httpResponse.getEntity().getContent().readAllBytes());
    }
    catch (Exception e) {
    	LOGGER.error("Excception, encryptJsonUsingNLP failed " + e);
    	LOGGER.error(e.getStackTrace().toString());
    	throw new ModuleException("Operation encryptJsonUsingNLP failed due to " + e , DBLErrorProvider.DATACRYPT_ERROR);
    }
    return response;
  }

  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("reduceJson")
  public String filterJson(@Config DBLConfiguration configuration,
		  @DisplayName("Sensitive Fields") @Expression(ExpressionSupport.SUPPORTED) String sensitiveFields,
		  @Content @DisplayName("Sensitive JSON") @Expression(ExpressionSupport.SUPPORTED) String sensitiveJson,
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
	String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
	LOGGER.info(versionTag + " DataBlind ReduceJson" );    	
    try {    
        KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", configuration.getEncryptionKey().getBytes());
        JsonDataCrypt jsonDataCrypt = new JsonDataCrypt(kc);
    	response = jsonDataCrypt.reduceJson( operation, sensitiveJson, sensitiveFields, overRideToken, passPhrase);
    }
    catch (Exception e) {
    	LOGGER.error("Excception, filterJson failed " + e);
    	LOGGER.error(e.getStackTrace().toString());
    	throw new ModuleException("Operation filterJson failed due to " + e , DBLErrorProvider.DATACRYPT_ERROR);
    }
    return response;
  }

 @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("DecryptJson")
  public String decryptJson(@Config DBLConfiguration configuration,
		  @DisplayName("Sensitive Fields") @Expression(ExpressionSupport.SUPPORTED) String sensitiveFields,
		  @Content @DisplayName("Encrypted JSON") @Expression(ExpressionSupport.SUPPORTED) String encryptedJson,
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
	String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
	LOGGER.info(versionTag + " DataBlind DecryptJson" );    	
    try {  
        KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", configuration.getEncryptionKey().getBytes());
    	JsonDataCrypt jsonDataCrypt = new JsonDataCrypt(kc);
    	response = jsonDataCrypt.transform( "Decrypt", tweak, encryptedJson, sensitiveFields, overRideToken, passPhrase);
    }
    catch (Exception e) {
    	LOGGER.error("Excception, decryptJson failed " + e);
    	LOGGER.error(e.getStackTrace().toString());
    	throw new ModuleException("Operation decryptJson failed due to " + e , DBLErrorProvider.DATACRYPT_ERROR);
    }
    return response;
  }
 @MediaType(value = ANY, strict = false)
 @Alias("OverrideToken")
 public String overrideToken(@Config DBLConfiguration configuration,
		  @DisplayName("Passphrase") @Expression(ExpressionSupport.SUPPORTED) String passPhrase,
		  @DisplayName("Expiration Seconds") @Expression(ExpressionSupport.SUPPORTED) Integer expirationSecs) {
   String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
   LOGGER.info(versionTag + " DataBlind OverrideToken" );    	
   try {    
       KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", configuration.getEncryptionKey().getBytes());
       HmacToken HmacToken = new HmacToken();
   	   response = HmacToken.generateToken( kc, passPhrase, expirationSecs.intValue());
   }
   catch (Exception e) {
	LOGGER.error("Excception, overrideToken failed " + e);
   	LOGGER.error(e.getStackTrace().toString());
	throw new ModuleException("Operation overrideToken failed due to " + e , DBLErrorProvider.DATACRYPT_ERROR);
   }
   return response;
 }
 @MediaType(value = ANY, strict = false)
 @Alias("OverrideTokenWithNewKey")
 public String overrideTokenWithNewKey(
		  @DisplayName("Key") @Expression(ExpressionSupport.SUPPORTED) String key,
		  @DisplayName("Passphrase") @Expression(ExpressionSupport.SUPPORTED) String passPhrase,
		  @DisplayName("Expiration Seconds") @Expression(ExpressionSupport.SUPPORTED) Integer expirationSecs) {
   String response = "{ 'Success' : 'false', 'error' : 'Undefined' }";
   LOGGER.info(versionTag + " DataBlind OverrideTokenWithNewKey" );    	
   try {    
       KeyContext kc = new KeyContext("CipherWorks", "Admin", "1.0", key.getBytes());
       HmacToken HmacToken = new HmacToken();
   	   response = HmacToken.generateToken( kc, passPhrase, expirationSecs.intValue());
   }
   catch (Exception e) {
	LOGGER.error("Excception, overrideTokenWithNewKey failed " + e);
   	LOGGER.error(e.getStackTrace().toString());
	throw new ModuleException("Operation overrideTokenWithNewKey failed due to " + e , DBLErrorProvider.DATACRYPT_ERROR);
   }
   return response;
 }

}
