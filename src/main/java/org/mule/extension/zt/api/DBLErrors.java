/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */
package org.mule.extension.zt.api;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public enum DBLErrors implements ErrorTypeDefinition<DBLErrors> {
    INVALID_PARAMETER,
    TIME_OUT,
    NOT_ALLOWED,
    DATACRYPT_ERROR
}
