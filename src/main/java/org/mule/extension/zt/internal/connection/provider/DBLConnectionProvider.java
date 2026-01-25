/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */
package org.mule.extension.zt.internal.connection.provider;

import org.mule.extension.zt.internal.connection.DBLConnection;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.RefName;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicReference;

import org.mule.sdk.api.annotation.ExternalLib;
import org.mule.sdk.api.meta.ExternalLibraryType;

import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.client.HttpRequestOptions;
import org.mule.runtime.http.api.client.proxy.ProxyConfig;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular implementation uses {@link CachedConnectionProvider} which lazily creates and
 * caches connections. This is required for connection providers that create HttpClient instances.
 */

@ExternalLib(name = "DataCrypt Library",
description = "A library that provides Datacrypt encryption and decryption functionality",
nameRegexpMatcher = "(.*)\\.datacrypt\\.jar",
requiredClassName = "com.ztensor.datacrypt.DataCrypt",
coordinates = "com.ztensor:datacrypt:3.0.5",
type = ExternalLibraryType.JAR)

public class DBLConnectionProvider implements CachedConnectionProvider<DBLConnection>, Startable, Stoppable {

  @RefName
  private String configName;

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

  @Parameter
  @Optional(defaultValue = "30000")
  @Summary("HTTP Request Timeout in milliseconds")
  @DisplayName("Request Timeout")
  private Integer apiRequestTimeout;
  public Integer getApiRequestTimeout() {
    return apiRequestTimeout;
  }

  @Parameter
  @Optional
  @Summary("Proxy server host name")
  @DisplayName("Proxy Host")
  @Placement(tab = "Proxy")
  private String proxyHost;
  public String getProxyHost() {
    return proxyHost;
  }

  @Parameter
  @Optional(defaultValue = "8080")
  @Summary("Proxy server port")
  @DisplayName("Proxy Port")
  @Placement(tab = "Proxy")
  private Integer proxyPort;
  public Integer getProxyPort() {
    return proxyPort;
  }

  @Parameter
  @Optional
  @Summary("Proxy server username for authentication")
  @DisplayName("Proxy Username")
  @Placement(tab = "Proxy")
  private String proxyUsername;
  public String getProxyUsername() {
    return proxyUsername;
  }

  @Parameter
  @Optional
  @Password
  @Summary("Proxy server password for authentication")
  @DisplayName("Proxy Password")
  @Placement(tab = "Proxy")
  private String proxyPassword;
  public String getProxyPassword() {
    return proxyPassword;
  }

  @Inject
  private HttpService httpService;

  private final AtomicReference<HttpClient> httpClientReference = new AtomicReference<>();

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
        HttpClient localHttpClient = httpClientReference.get();
        if (localHttpClient == null) {
            synchronized (this) {
                localHttpClient = httpClientReference.get();
                if (localHttpClient == null) {
                    // Create the HTTP client with a name based on the configuration name
                    HttpClientConfiguration.Builder builder = new HttpClientConfiguration.Builder();
                    builder.setName(configName + "-http-client");
                    
                    // Configure proxy if provided
                    if (proxyHost != null && !proxyHost.trim().isEmpty()) {
                        // Create a ProxyConfig implementation
                        final String finalProxyHost = proxyHost;
                        final Integer finalProxyPort = proxyPort;
                        final String finalProxyUsername = proxyUsername;
                        final String finalProxyPassword = proxyPassword;
                        
                        ProxyConfig proxyConfig = new ProxyConfig() {
                            @Override
                            public String getHost() {
                                return finalProxyHost;
                            }
                            
                            @Override
                            public int getPort() {
                                return finalProxyPort;
                            }
                            
                            @Override
                            public String getUsername() {
                                return finalProxyUsername;
                            }
                            
                            @Override
                            public String getPassword() {
                                return finalProxyPassword;
                            }
                            
                            @Override
                            public String getNonProxyHosts() {
                                return null;  // No non-proxy hosts configured
                            }
                        };
                        
                        builder.setProxyConfig(proxyConfig);
                        if (proxyUsername != null && !proxyUsername.trim().isEmpty()) {
                            LOGGER.info("Proxy configured with authentication: " + proxyHost + ":" + proxyPort);
                        } else {
                            LOGGER.info("Proxy configured: " + proxyHost + ":" + proxyPort);
                        }
                    }
                    
                    localHttpClient = httpService.getClientFactory().create(builder.build());
                    localHttpClient.start();
                    httpClientReference.set(localHttpClient);
                }
            }
        }
        
        connection = new DBLConnection("Test", localHttpClient, apiUri, apiKey, apiRequestTimeout);
        if (apiUri != null || apiKey != null) {
            remoteConenctionRequired = true;
            HttpRequestOptions requestOptions = HttpRequestOptions.builder()
                .responseTimeout(apiRequestTimeout)
                .build();
            HttpRequest request = HttpRequest.builder()
             .method("GET")
             .uri(apiUri + "/datacrypt-status")
             .addHeader("Content-Type", "application/json")
             .addHeader("x-api-key", apiKey)
             .build();
            httpResponse = localHttpClient.send(request, requestOptions);
            response = new String(httpResponse.getEntity().getContent().readAllBytes());
            LOGGER.info("DataGuard API Status: " + response);  
        }
    }
    catch (Exception e) {
        LOGGER.error("Excception, datacrypt-status failed " + e);
        LOGGER.error(Arrays.toString(e.getStackTrace()));
        throw new ConnectionException("Operation datacrypt-status failed due to " , e );
    } 
    if (remoteConenctionRequired && httpResponse != null) {
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
    // HttpClient is started in the connect() method where it's created
    // This method is called before connect() in the lifecycle, so nothing to do here
    LOGGER.debug("DBLConnectionProvider started");
  }

  @Override
  public void stop() {
    try {
      HttpClient localHttpClient = httpClientReference.getAndSet(null);
      if (localHttpClient != null) {
        localHttpClient.stop();
      }
    } catch (Exception e) {
      LOGGER.error("Error while stopping httpClient: " + e.getMessage(), e);
    }
  }
}
