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
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
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
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeoutException;

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
coordinates = "com.ztensor:datacrypt:3.0.8",
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
  @Summary("HTTP Request Timeout (in milliseconds)")
  @DisplayName("Request Timeout")
  @ConfigOverride
  private Integer apiRequestTimeout;
  public Integer getApiRequestTimeout() {
    return apiRequestTimeout;
  }

  @Parameter
  @Optional(defaultValue = "MILLISECONDS")
  @Summary("API Request Timeout Unit")
  @DisplayName("Request Timeout Unit")
  @ConfigOverride
  private TimeUnit apiRequestTimeoutUnit = TimeUnit.MILLISECONDS;
  public TimeUnit getApiRequestTimeoutUnit() {
    return apiRequestTimeoutUnit;
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

  private HttpService httpService;

  public DBLConnectionProvider() {
  }

  @Inject
  public void setHttpService(HttpService httpService) {
    this.httpService = httpService;
  }

  private final AtomicReference<HttpClient> httpClientReference = new AtomicReference<>();

  private static final Logger logger = LoggerFactory.getLogger(DBLConnectionProvider.class);

  @Override
  public DBLConnection connect() throws ConnectionException {
    try {
      HttpClient client = getOrCreateHttpClient();
      DBLConnection connection = new DBLConnection("Test", client, apiUri, apiKey, apiRequestTimeout, apiRequestTimeoutUnit);
      HttpResponse statusResponse = callDatacryptStatusIfNeeded(client);
      validateStatusResponse(statusResponse);
      return connection;
    } catch (ConnectionException e) {
      throw e;
    } catch (Exception e) {
      throw new ConnectionException("Operation datacrypt-status failed due to " , e);
    }
  }

  private HttpClient getOrCreateHttpClient() {

    if (httpService == null) {
      throw new IllegalStateException(
          "HttpService was not injected by Mule runtime. " +
          "This indicates an invalid connector initialization."
      );
    }
    HttpClient existing = httpClientReference.get();
    if (existing != null) {
      return existing;
    }
    synchronized (this) {
      existing = httpClientReference.get();
      if (existing != null) {
        return existing;
      }
      HttpClientConfiguration.Builder builder = new HttpClientConfiguration.Builder();
      builder.setName(configName + "-http-client");
      if (isProxyConfigured()) {
        builder.setProxyConfig(createProxyConfig());
        logProxyConfig();
      }
      HttpClient client = httpService.getClientFactory().create(builder.build());
      client.start();
      httpClientReference.set(client);
      return client;
    }
  }

  private boolean isProxyConfigured() {
    return proxyHost != null && !proxyHost.trim().isEmpty();
  }

  private ProxyConfig createProxyConfig() {
    final String host = proxyHost;
    final Integer port = proxyPort;
    final String username = proxyUsername;
    final String password = proxyPassword;
    return new ProxyConfig() {
      @Override
      public String getHost() { return host; }
      @Override
      public int getPort() { return port != null ? port : 8080; }
      @Override
      public String getUsername() { return username; }
      @Override
      public String getPassword() { return password; }
      @Override
      public String getNonProxyHosts() { return null; }
    };
  }

  private void logProxyConfig() {
    if (proxyUsername != null && !proxyUsername.trim().isEmpty()) {
      logger.info("Proxy configured with authentication: {} : {} " , proxyHost , proxyPort);
    } else {
      logger.info("Proxy configured: {} : {} " , proxyHost , proxyPort);
    }
  }

  private HttpResponse callDatacryptStatusIfNeeded(HttpClient client) throws IOException, TimeoutException {
    if (apiUri == null || apiKey == null) {
      return null;
    }
    int timeoutMs = (int) TimeUnit.MILLISECONDS.convert(apiRequestTimeout, apiRequestTimeoutUnit);
    HttpRequestOptions requestOptions = HttpRequestOptions.builder().responseTimeout(timeoutMs).build();
    HttpRequest request = HttpRequest.builder()
        .method("GET")
        .uri(apiUri + "/datacrypt-status")
        .addHeader("Content-Type", "application/json")
        .addHeader("x-api-key", apiKey)
        .build();
    HttpResponse response = client.send(request, requestOptions);
    String body = new String(response.getEntity().getContent().readAllBytes());
    logger.info("DataGuard API Status: {} " , body);
    return response;
  }

  private void validateStatusResponse(HttpResponse response) throws ConnectionException {
    if (response == null) {
      return;
    }
    int statusCode = response.getStatusCode();
    if (statusCode >= 200 && statusCode < 300) {
      logger.info("Request successful. Status code: {} " , statusCode);
      return;
    }
    if (statusCode >= 400 && statusCode < 500) {
      throw new ConnectionException("Operation datacrypt-status failed due to client error. Status code: " + statusCode);
    }
    throw new ConnectionException("Operation datacrypt-status failed due to server error. Status code: " + statusCode);
  }

  @Override
  public void disconnect(DBLConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      logger.error("Error while disconnecting [" + connection.id() + "]: " + e.getMessage(), e);
      logger.error("Error stack trace: {} " , Arrays.toString(e.getStackTrace()));
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
    logger.debug("DBLConnectionProvider started");
  }

  @Override
  public void stop() {
    try {
      HttpClient localHttpClient = httpClientReference.getAndSet(null);
      if (localHttpClient != null) {
        localHttpClient.stop();
      }
    } catch (Exception e) {
      logger.error("Error while stopping httpClient: " + e.getMessage(), e);
    }
  }
}