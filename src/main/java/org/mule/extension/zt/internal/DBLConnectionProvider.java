/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */
package org.mule.extension.zt.internal;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.meta.ExternalLibraryType;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.values.OfValues;
import org.mule.runtime.extension.api.annotation.ExternalLib;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import javax.inject.Inject;

import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.mule.runtime.http.api.domain.entity.ByteArrayHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link ConnectionProvider} if you want a new connection each time something requires one.
 */

public class DBLConnectionProvider implements PoolingConnectionProvider<DBLConnection>, Startable, Stoppable {

  @Parameter
  @Optional
  @Summary("The API base URI for the DataGuard API (https://host/Dev)")
  @DisplayName("API URI")
  private String apiUri;
  public String getApiUri() {
    return apiUri;
  }

  @Parameter
  @Optional
  @Summary("The API Key for the DataGuard API.")
  @DisplayName("DataGuardAPI Key")
  private String apiKey;
  public String getApiKey() {
    return apiKey;
  }

  @Inject
  private HttpService httpService;

  private volatile HttpClient httpClient;

  private final Logger LOGGER = LoggerFactory.getLogger(DBLConnectionProvider.class);

  @Override
  public DBLConnection connect() throws ConnectionException {
    /*
     * The remoteConenctionRequired is true if a valid API URI and API Key is provided. If both are null, 
     * then it is assumed that the remote connection is not required. The method will attempt to call the status
     * URI if the apiUri or apiKey are provided. If apiUri and apiKey are not provided, this method will not attempt 
     * to call the status URI.
     */
    boolean remoteConenctionRequired = false;
    DBLConnection connection = null;
    String response = null;
    HttpResponse httpResponse = null;
    try {
        // Create the HTTP client with a meaningful name for diagnostics
        HttpClientConfiguration.Builder builder = new HttpClientConfiguration.Builder();
        builder.setName("datablind-http-client");
        httpClient = httpService.getClientFactory().create(builder.build());
        connection = new DBLConnection("Test", httpClient, apiUri, apiKey);
        if (apiUri != null || apiKey != null) {
            remoteConenctionRequired = true;
            HttpRequest request = HttpRequest.builder()
             .method("GET")
             .uri(apiUri + "/datacrypt-status")
             .addHeader("Content-Type", "application/json")
             .addHeader("x-api-key", apiKey)
             .build();
            httpClient.start();
            httpResponse = httpClient.send(request);
            response = new String(httpResponse.getEntity().getContent().readAllBytes());
            LOGGER.info("DataGuard API Status: " + response);  
        }
    }
    catch (Exception e) {
        LOGGER.error("Excception, datacrypt-status failed " + e);
        LOGGER.error(e.getStackTrace().toString());
        throw new ConnectionException("Operation datacrypt-status failed due to " , e );
    } 
    if (remoteConenctionRequired) {
       int statusCode = httpResponse.getStatusCode();
       if (statusCode >= 200 && statusCode < 300) {
              // Successful response
              LOGGER.info("Request successful. Status code: " + statusCode);
       } else if (statusCode >= 400 && statusCode < 500) {
              // Client-side error
              LOGGER.info("Client error. Status code: " + statusCode);
              throw new ConnectionException("Operation datacrypt-status failed due to client error. Status code: " + statusCode );
              // Handle the error, perhaps by logging the payload
       } else {
              // Server-side error or other status
              LOGGER.info("Error. Status code: " + statusCode);
              throw new ConnectionException("Operation datacrypt-status failed due to server error. Status code: " + statusCode );
              // Handle as a server error
       }
    }
    return connection;
  }

  @Override
  public void disconnect(DBLConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      LOGGER.error("Error while disconnecting [" + connection.getId() + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(DBLConnection connection) {
    return ConnectionValidationResult.success();
  }

  @Override
  public void start() {
    try {
      httpClient.start();
    } catch (Exception e) {
      LOGGER.error("Error while starting httpClient: " + e.getMessage(), e);
    }
  }

  @Override
  public void stop() {
    try {
      httpClient.stop();
    } catch (Exception e) {
      LOGGER.error("Error while stopping httpClient: " + e.getMessage(), e);
    }
  }
}
