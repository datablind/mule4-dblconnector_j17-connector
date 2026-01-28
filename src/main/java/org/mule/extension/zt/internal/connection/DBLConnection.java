/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */

package org.mule.extension.zt.internal.connection;
import org.mule.runtime.extension.api.annotation.param.ConfigOverride;
import org.mule.runtime.http.api.client.HttpClient;
import java.util.concurrent.TimeUnit;

/**
 * This record represents an extension connection.
 */
public record DBLConnection(
    String id,
    HttpClient httpClient,
    String apiUri,
    String apiKey,
    @ConfigOverride Integer apiRequestTimeout,
    @ConfigOverride TimeUnit apiRequestTimeoutUnit
) {
    public DBLConnection() {
        this(null, null, null, null, null, null);
    }

    public void invalidate() {
        // do something to invalidate this connection!
    }
}
