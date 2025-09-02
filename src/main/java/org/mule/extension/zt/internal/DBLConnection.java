/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */
package org.mule.extension.zt.internal;

import org.mule.runtime.http.api.client.HttpClient;


/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class DBLConnection {

  private final String id;
  private final HttpClient httpClient;
  private final String apiKey;
  private final String apiUri;

  public DBLConnection(String id, HttpClient httpClient, String apiUri, String apiKey) {
    this.id = id;
    this.httpClient = httpClient;
    this.apiKey = apiKey;
    this.apiUri = apiUri;
  }

  public String getId() {
    return id;
  }

  public void invalidate() {
    // do something to invalidate this connection!
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getApiUri() {
    return apiUri;
  }
}
