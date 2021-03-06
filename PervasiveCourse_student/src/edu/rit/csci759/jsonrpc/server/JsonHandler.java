package edu.rit.csci759.jsonrpc.server;

/**
 * Demonstration of the JSON-RPC 2.0 Server framework usage. The request
 * handlers are implemented as static nested classes for convenience, but in 
 * real life applications may be defined as regular classes within their old 
 * source files.
 *
 * @author Vladimir Dzhuvinov
 * @version 2011-03-05
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import edu.rit.csci759.fuzzylogic.MyTipperClass;
import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class JsonHandler {

	static GpioController gpio = null;
	static MyTipperClass m = new MyTipperClass();

	// Implements a handler for an "echo" JSON-RPC method
	public static class EchoHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {

			return new String[] { "echo" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			if (req.getMethod().equals("echo")) {

				// Echo first parameter

				List params = (List) req.getParams();

				Object input = params.get(0);

				return new JSONRPC2Response(input, req.getID());
			} else {

				// Method name not supported

				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	// Implements a handler for "getDate" and "getTime" JSON-RPC methods
	// that return the current date and time

	public static class AmbientHandler implements RequestHandler {

		public String[] handledRequests() {
			return new String[] { "getLight" };
		}

		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			String hostname = "unknown";
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			// Modified by Vishwas Tantry
			if (req.getMethod().equals("getLight")) {
				if (gpio == null) {
					gpio = GpioFactory.getInstance();
					MCP3008ADCReader.initSPI(gpio);
				}

				int adc_ambient = MCP3008ADCReader
						.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1
								.ch());
				// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
				// convert in the range of 1-100
				int ambient = (int) (adc_ambient / 10.24);
				// System.out.println("Temperature:" + temperature + "/100 (" +
				// adc_temperature + "/1024)");

				int adc_temperature = MCP3008ADCReader
						.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH0
								.ch());
				// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
				// convert in the range of 1-100
				int temperature = (int) (adc_temperature / 10.24);
				// System.out.println("Temperature:" + temperature + "/100 (" +
				// adc_temperature + "/1024)");

				return new JSONRPC2Response(hostname + " " + ambient + " "
						+ "temperature" + " " + temperature, req.getID());

			} else {

				// Method name not supported
				System.out.println(req.getMethod());

				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}

		}

	}

	// Implements a handler for "getDate" and "getTime" JSON-RPC methods
	// that return the current date and time
	public static class DateTimeHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {

			return new String[] { "getDate", "getTemp" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			String hostname = "unknown";
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			if (req.getMethod().equals("getDate")) {

				DateFormat df = DateFormat.getDateInstance();

				String date = df.format(new Date());

				return new JSONRPC2Response(hostname + " " + date, req.getID());

			} else if (req.getMethod().equals("getTime")) {

				DateFormat df = DateFormat.getTimeInstance();

				String time = df.format(new Date());

				return new JSONRPC2Response(hostname + " " + time, req.getID());
			}

			else {

				// Method name not supported

				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	// Implements a handler for "addRule" JSON-RPC methods
	// that updates the rule
	public static class AddRuleHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {

			return new String[] { "addRule" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			String hostname = "unknown";
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			if (req.getMethod().equals("addRule")) {

				Map<String, Object> params = req.getNamedParams();
				m.addRule(params.get("temperature").toString(),
						params.get("condition").toString(),
						params.get("ambient").toString(), params.get("blind")
								.toString());
				m.getResults();
				return new JSONRPC2Response("Rule added succesfully",
						req.getID());

			}

			else {

				// Method name not supported

				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	// Implements a handler for "deleteRule" JSON-RPC methods
	// that delete the rule
	public static class DeleteRuleHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {

			return new String[] { "deleteRule" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			String hostname = "unknown";
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			if (req.getMethod().equals("deleteRule")) {
				Map<String, Object> params = req.getNamedParams();
				m.deleteRule(params.get("name").toString());

				return new JSONRPC2Response("Rule deleted succesfully",
						req.getID());

			}

			else {

				// Method name not supported

				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	// Implements a handler for "getRules" JSON-RPC methods
	// that retrieves the rule
	public static class GetRulesHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {

			return new String[] { "getRules" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			String hostname = "unknown";
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			if (req.getMethod().equals("getRules")) {

				return new JSONRPC2Response(m.getResults(), req.getID());

			}

			else {

				// Method name not supported

				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}
}
