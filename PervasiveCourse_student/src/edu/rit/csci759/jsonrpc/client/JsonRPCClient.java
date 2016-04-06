package edu.rit.csci759.jsonrpc.client;

//The Client sessions package
import java.net.MalformedURLException;
//For creating URLs
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
//The JSON Smart package for JSON encoding/decoding (optional)



public class JsonRPCClient {


	public static void main(String[] args) throws JSONRPC2SessionException, InterruptedException {


		// Creating a new session to a JSON-RPC 2.0 web service at a specified URL

		// The JSON-RPC 2.0 server URL
		URL serverURL = null;

		try {
			serverURL = new URL("http://10.10.10.103:8080");

		} catch (MalformedURLException e) {
		// handle exception...
		}

		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


		// Once the client session object is created, you can use to send a series
		// of JSON-RPC 2.0 requests and notifications to it.

		// Sending an example "getTime" request:
		// Construct new request
		//Modified by Vishwas Tantry
		String method = "getTime";
		String method1="getTemp";
		String method2="getLight";
		
		
		//method to add rule
		String method3="addRule";
		Map <String,Object> input=new HashMap<String,Object>();
		input.put("temperature", "hot");
		input.put("condition", "AND");
		input.put("ambient","dim");
		input.put("blind", "open");
		
		
		
		//method to delete rule
		String method4="deleteRule";	
		Map <String,Object> input1=new HashMap<String,Object>();
		input1.put("name", "2");
		
		
			int requestID = 0;
		JSONRPC2Request request=null;
		// Send request
		JSONRPC2Response response = null;
		
		
			
			
		//request = new JSONRPC2Request("getRules", requestID);
		//request=new JSONRPC2Request(method3,input,requestID);
		request=new JSONRPC2Request(method4,input1,requestID);
		
		response = mySession.send(request);	
	


		// Print response result / error
		
		
		if (response.indicatesSuccess())
			System.out.println(response.getResult());
		else
			System.out.println(response.getError().getMessage());
	
		
		}



		
	
	
}