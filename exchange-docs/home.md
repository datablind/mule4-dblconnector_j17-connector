# DataBlind Connector for Java 17

The DataBlind Connector provides secure data encryption and decryption capabilities for API used by the business critical **Agentic AI** applications. This connector integrates with the ZTensor DataBlind encryption framework to secure your APIs utilized by your **Agents** by providing field-level encryption, decryption, and data filtering operations.

## Overview

DataBlind is a high-performance encryption framework designed to protect sensitive data while maintaining its format and usability. The DataBlind Connector for MuleSoft allows you to seamlessly integrate your APIs with **Agents** while maintaining the data security.

### Key Capabilities

* **JSON Field Encryption**: Encrypt specific fields within JSON documents retrieved by the **AI Agents**.
* **JSON Field Decryption**: Decrypt previously encrypted JSON fields to restore original values.
* **NLP-Based Encryption**: Use AI/Natural Language Processing to automatically identify and protect sensitive fields (PII/PHI) without manual mapping.
* **Data Filtering**: Reduce JSON data by removing or retaining specific fields based on **Agent AI** sensitivity.
* **Token Management**: Generate and manage time-limited override tokens for authorized data access.


## Main Features

### Format-Preserving Encryption (FPE)
Encrypt data such as credit card numbers, SSNs, and names while preserving the original format. This allows encrypted data to be shared with your **AI Agents** without Agents requiring modification.

### AI-Powered PII Discovery
Leverage integrated NLP capabilities to automatically scan JSON payloads for sensitive information. The connector can detect and protect data based on context and patterns, significantly reducing manual configuration effort.

### Secure Data Reduction
Easily implement data masking and filtering policies to ensure that only the necessary information is shared with the **AI Agents**, minimizing the risk of data exposure.

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

## About MuleSoft Certified Connectors:
MuleSoft Certified connectors are developed by MuleSoft’s partners and developer community and subsequently reviewed and certified by MuleSoft. For these connectors, MuleSoft will take initial calls from customers and isolate the issue for resolution. MuleSoft disclaims any additional support obligation for such MuleSoft Certified Connector. For support of MuleSoft Certified Connectors, customers should contact the MuleSoft partner that created the connector. By installing this connector, you consent to MuleSoft sharing your contact information with the developer of this connector so that you can receive more information about it directly from the developer.

## About ZTensor
ZTensor provides advanced data protection solutions that empower organizations to secure their most sensitive **Agentic AI** applications without compromising operational efficiency. For more information about us, visit https://www.ztensor.com/

For support or inquiries, please contact us at [mulesoftconnector@ztensor.com](mailto:mulesoftconnector@ztensor.com).
