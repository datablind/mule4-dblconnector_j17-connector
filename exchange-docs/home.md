# DataBlind Connector for Java 17

The DataBlind Connector provides secure data encryption and decryption capabilities for API used by the business critical **Agentic AI** applications. This connector integrates with the ZTensor DataBlind encryption framework to secure your APIs utilized by your **Agents** by providing field-level encryption, decryption, and data filtering operations.

## Overview

DataBlind is a high-performance encryption framework designed to protect sensitive data while maintaining its format and usability. The DataBlind Connector for MuleSoft allows you to seamlessly integrate your APIs with **Agents** while maintaining the data security.

### Key Capabilities

* **JSON Field Encryption**: Encrypt specific fields within JSON documents using various algorithms.
* **JSON Field Decryption**: Decrypt previously encrypted JSON fields to restore original values.
* **NLP-Based Encryption**: Use AI/Natural Language Processing to automatically identify and protect sensitive fields (PII/PHI) without manual mapping.
* **Data Filtering**: Reduce JSON data by removing or retaining specific fields based on sensitivity.
* **Token Management**: Generate and manage time-limited override tokens for authorized data access.

## Concept

![Concept](https://raw.githubusercontent.com/datablind/mule4-dblconnector_j17-connector/dev/assets/data-blind-concept-diagram.png)

## Main Features

### Format-Preserving Encryption (FPE)
Encrypt data such as credit card numbers, SSNs, and names while preserving the original format. This allows encrypted data to be stored in existing database schemas and processed by legacy systems without modification.

### AI-Powered PII Discovery
Leverage integrated NLP capabilities to automatically scan JSON payloads for sensitive information. The connector can detect and protect data based on context and patterns, significantly reducing manual configuration effort.

### Secure Data Reduction
Easily implement data masking and filtering policies to ensure that only the necessary information is shared with downstream systems, minimizing the risk of data exposure.

### Requirements

| Component | Version |
| --- | --- |
| Mule Runtime | 4.6.0 or later |
| Java | 17 |
| DataBlind Library | 3.0.8 |

## Resources
* [Release Notes](https://github.com/datablind/mule4-dblconnector_j17-connector/blob/dev/doc/release-notes.md)
* [Technical Reference](https://github.com/datablind/mule4-dblconnector_j17-connector/blob/dev/doc/technical-reference.md)
* [User Manual](https://github.com/datablind/mule4-dblconnector_j17-connector/blob/dev/doc/user-manual.md)
* [Demo Application](https://github.com/datablind/dblconnector_j17_demo)

## About ZTensor
ZTensor provides advanced data protection solutions that empower organizations to secure their most sensitive **Agentic AI** applications without compromising operational efficiency.

For support or inquiries, please contact us at [mulesoftconnector@ztensor.com](mailto:mulesoftconnector@ztensor.com).
