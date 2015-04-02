// This file was generated by Mendix Business Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package webservice_ntlm.actions;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * 
 */
public class NTLM_WebService extends CustomJavaAction<Boolean>
{
	private IMendixObject __Request;
	private system.proxies.FileDocument Request;
	private IMendixObject __Response;
	private system.proxies.FileDocument Response;
	private String SoapUrl;
	private String SoapAction;
	private IMendixObject __Config;
	private webservice_ntlm.proxies.NTLM_Config Config;

	public NTLM_WebService(IContext context, IMendixObject Request, IMendixObject Response, String SoapUrl, String SoapAction, IMendixObject Config)
	{
		super(context);
		this.__Request = Request;
		this.__Response = Response;
		this.SoapUrl = SoapUrl;
		this.SoapAction = SoapAction;
		this.__Config = Config;
	}

	@Override
	public Boolean executeAction() throws Exception
	{
		this.Request = __Request == null ? null : system.proxies.FileDocument.initialize(getContext(), __Request);

		this.Response = __Response == null ? null : system.proxies.FileDocument.initialize(getContext(), __Response);

		this.Config = __Config == null ? null : webservice_ntlm.proxies.NTLM_Config.initialize(getContext(), __Config);

		// BEGIN USER CODE
		URL wsUrl = new URL(SoapUrl);
        
        String host = wsUrl.getHost();
        int port = wsUrl.getPort();
        String protocol = wsUrl.getProtocol();
        String file = wsUrl.getFile();
        
        HttpHost target = new HttpHost(host, port, protocol);
        
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		//CloseableHttpClient httpClient = HttpClients.createSystem(); // used to interact with proxy settings

		String workstationName = "";
		if (Config.getUseWorkstation()) {
			workstationName = InetAddress.getLocalHost().getHostName();
		}
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
		    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, "ntlm"), 
		    new NTCredentials(Config.getUsername(), Config.getPassword(), 
		    		workstationName, Config.getDomain()));
		
		AuthCache authCache = new BasicAuthCache();
		NTLMScheme ntlmScheme = new NTLMScheme();
		authCache.put(target, ntlmScheme);

		// Add AuthCache to the execution context
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setCredentialsProvider(credsProvider);
		localContext.setAuthCache(authCache);
		
        HttpPost httppost = new HttpPost(file);
        httppost.setHeader("Accept-Encoding", "gzip,deflate");
        httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
        httppost.setHeader("SOAPAction", SoapAction);
        
        String data = generateBody(getContext(), Request);
         
        httppost.setEntity(new StringEntity(data));
        HttpResponse callResponse = httpClient.execute(target, httppost, localContext);
        HttpEntity callResult = callResponse.getEntity();
        
        String rawXml = EntityUtils.toString(callResult);

        if (callResponse.getStatusLine().getStatusCode() == 200)
        {
        	retrieveBody(getContext(), Response, rawXml);
        }
        		
        // This ensures the connection gets released back to the manager
        EntityUtils.consume(callResult);
        
        return true;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public String toString()
	{
		return "NTLM_WebService";
	}

	// BEGIN EXTRA CODE
	public static Document loadXMLFromString(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    
	    return builder.parse(is);
	}
	
	public static String generateBody(IContext context, system.proxies.FileDocument RequestXml) throws Exception
	{
		if (RequestXml == null)
			return null;
		InputStream f = Core.getFileDocumentContent(context, RequestXml.getMendixObject());
		String data = IOUtils.toString(f);
		
		Document doc_body = loadXMLFromString(data);
		
		String env = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body></soapenv:Body>"
				+ "</soapenv:Envelope>";
		
		Document doc = loadXMLFromString(env);

		NodeList nodesToCopy = doc_body.getChildNodes();
		
		Node bodyElement = doc.getDocumentElement().getLastChild();
		
		for(int i = 0; i < nodesToCopy.getLength(); i++) {
		    // Create a duplicate node and transfer ownership of the
		    // new node into the destination document
		    Node newNode = doc.importNode(nodesToCopy.item(i), true);
		    // Make the new node an actual item in the target document
		    bodyElement.appendChild(newNode);
		}
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(new DOMSource(doc), result);
		
		return result.getWriter().toString();
	}
	
	public static void retrieveBody(IContext context, system.proxies.FileDocument ResponseXml, 
			String rawResponse) throws Exception
	{
		Document doc = loadXMLFromString(rawResponse);
		Node content = doc.getDocumentElement().getLastChild().getFirstChild();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(new DOMSource(content), result);

		String response = result.getWriter().toString();
		
		if (ResponseXml == null)
			throw new IllegalArgumentException("Destination file is null");
		if (response == null)
			throw new IllegalArgumentException("Value to write is null");
		Core.storeFileDocumentContent(context, ResponseXml.getMendixObject(), IOUtils.toInputStream(response));
	}
	// END EXTRA CODE
}
