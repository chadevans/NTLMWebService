# NTLMWebService
Adding the ability to use Apache HttpClient to leverage NTLM authentication for web services. This includes the ability to access Microsoft Dynamics AX and NAV.
# Third Party Libraries Used
Includes the Apache HttpClient version 4.4, with support for NTLMv1, NTLMv2. See [Apache HttpComponents](https://hc.apache.org/httpcomponents-client-4.4.x/index.html)
# Usage
In order to enable support for NTLM on a web service call, you will need to do the following:
1. Add a Consumed Web Service resource for the web service (used for schema generation)
2. Add a Domain-to-XML mapping for the request body, based on the web service from above
3. Add a XML-to-Domain mapping for the response, based on the web service from above
4. Create a microflow that does the following:
  1. Create a request FileDocument
  2. Add an Import XML action to convert domain to xml to the request FileDocument
  3. Create a response FileDocument
  4. Call 'Call_NTLMWebService' microflow with the above + the config object
  5. Add an Export XML action to convert the xml to domain to the response FileDocument
