/*
 * Copyright 2025 ZTensor, Inc. All rights reserved.
 * This software is proprietary and confidential. Unauthorized copying, 
 * distribution, or use of this software, via any medium, is strictly prohibited.
 * 
 * This software is licensed for commercial use only. For licensing information,
 * please contact ZTensor, Inc.
 */
package org.mule.extension.zt.internal.extension;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.extension.zt.internal.error.DBLErrorTypes;
import org.mule.extension.zt.internal.config.DBLConfiguration;
import org.mule.runtime.extension.api.annotation.license.RequiresEnterpriseLicense;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.meta.JavaVersion;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "zt")
@Extension(name = "DATA BLIND", vendor = "ZTensor", category = Category.CERTIFIED)
// @RequiresEnterpriseLicense(allowEvaluationLicense = true)
@ErrorTypes(DBLErrorTypes.class)
@JavaVersionSupport({JavaVersion.JAVA_17})
@Configurations(DBLConfiguration.class)
public class DBLExtension {

}
