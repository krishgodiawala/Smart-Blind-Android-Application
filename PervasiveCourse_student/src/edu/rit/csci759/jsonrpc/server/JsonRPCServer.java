package edu.rit.csci759.jsonrpc.server;

//The JSON-RPC 2.0 Base classes that define the 
//JSON-RPC 2.0 protocol messages
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TimerTask;

import java.util.Timer;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
//The JSON-RPC 2.0 server framework package
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;

import edu.rit.csci759.fuzzylogic.MyTipperClass;
import edu.rit.csci759.jsonrpc.server.JsonHandler.AmbientHandler;
import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class JsonRPCServer implements Runnable {
	// ServerSocket listener;
	// ServerSocket server;
	/**
	 * The port that the server listens on.
	 */
	private static final int PORT = 8080;
	private static final int TEMPPORT = 8090;
	static GpioController gpio = null;
	 static GpioPinDigitalOutput greenPin =null;
	 static GpioPinDigitalOutput yellowPin =null ;
	 static GpioPinDigitalOutput redPin  =null ;

	public JsonRPCServer() {
		if (gpio == null) {
			gpio = GpioFactory.getInstance();
			MCP3008ADCReader.initSPI(gpio);
			greenPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "green", PinState.LOW);
			 yellowPin  = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "yellow",  PinState.LOW);
			 redPin   = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "red",   PinState.LOW);
		}
	}

	// private static int reqCount=0;

	/**
	 * A handler thread class. Handlers are spawned from the listening loop and
	 * are responsible for a dealing with a single client and broadcasting its
	 * messages.
	 */
	private static class Handler extends Thread {
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private Dispatcher dispatcher;

		// private int local_count;

		/**
		 * Constructs a handler thread, squirreling away the socket. All the
		 * interesting work is done in the run method.
		 */
		public Handler(Socket socket) {
			this.socket = socket;

			// Create a new JSON-RPC 2.0 request dispatcher
			this.dispatcher = new Dispatcher();

			// Register the "echo", "getDate" and "getTime","getRules","addRules" handlers with it
			dispatcher.register(new JsonHandler.EchoHandler());
			dispatcher.register(new JsonHandler.DateTimeHandler());
			dispatcher.register(new JsonHandler.AmbientHandler());
			dispatcher.register(new JsonHandler.AddRuleHandler());
			dispatcher.register(new JsonHandler.DeleteRuleHandler());
			dispatcher.register(new JsonHandler.GetRulesHandler());

		}

		/**
		 * Services this thread's client by repeatedly requesting a screen name
		 * until a unique one has been submitted, then acknowledges the name and
		 * registers the output stream for the client in a global set, then
		 * repeatedly gets inputs and broadcasts them.
		 */
		public void run() {
			try {
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				// read request
				String line;
				line = in.readLine();
				// System.out.println(line);
				StringBuilder raw = new StringBuilder();
				raw.append("" + line);
				boolean isPost = line.startsWith("POST");
				int contentLength = 0;
				while (!(line = in.readLine()).equals("")) {
					// System.out.println(line);
					raw.append('\n' + line);
					if (isPost) {
						final String contentHeader = "Content-Length: ";
						if (line.startsWith(contentHeader)) {
							contentLength = Integer.parseInt(line
									.substring(contentHeader.length()));
						}
					}
				}

				StringBuilder body = new StringBuilder();
				if (isPost) {
					int c = 0;
					for (int i = 0; i < contentLength; i++) {
						c = in.read();
						body.append((char) c);
					}
				}

				System.out.println(body.toString());
				JSONRPC2Request request = JSONRPC2Request
						.parse(body.toString());
				JSONRPC2Response resp = dispatcher.process(request, null);

				// send response
				out.write("HTTP/1.1 200 OK\r\n");
				out.write("Content-Type: application/json\r\n");
				out.write("\r\n");
				out.write(resp.toJSONString());
				// do not in.close();

				out.flush();
				out.close();
				socket.close();
			} catch (IOException e) {
				System.out.println(e);
			} catch (JSONRPC2ParseException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();

				} catch (IOException e) {
				}
			}

		}

	}

	public static void main(String[] args) throws Exception {

		System.out.println("The server is running.");

		ServerSocket listener = new ServerSocket(PORT);

		// ServerSocket server = new ServerSocket(TEMPPORT);

		// new Thread(new JsonRPCServer(), "JSON").start();
		new Thread(new JsonRPCServer()).start();

		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} finally {

		}
	}


	private void TimeHandlerMethd() {
		ServerSocket server = null;
		try {
			System.out.println("Before Temp");
			server = new ServerSocket(TEMPPORT);
			System.out.println("Created");

			// Timer timer = new Timer();
			while (true) {
				Socket s = server.accept();
				new Thread(new ReadTemperature(s)).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void JsonHandleMethod() {
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(PORT);
			while (true) {
				new Handler(listener.accept()).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	//class to read the temperature and ambient

	static class ReadTemperature extends Thread {

		private PrintWriter out;
		int old_Temperature;
		int old_Ambient;
		Socket serv;
		JsonHandler json;

		public ReadTemperature(Socket sock) {

			old_Temperature = Integer.MAX_VALUE;
			this.serv = sock;
			old_Ambient = Integer.MAX_VALUE;
			System.out.println("Inside constructor");
			try {
				out = new PrintWriter(serv.getOutputStream(), true);
			} catch (IOException e) {
				System.out.println("Connection down");
				e.printStackTrace();
			}

		}

		public void run() {
			try {
				while (true) {
					int adc_temperature = (int) (MCP3008ADCReader
							.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH0
									.ch()) / 10.24);

					int adc_ambient = (int) (MCP3008ADCReader
							.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1
									.ch()) / 10.24);


					//if temperature and ambient difference greater than 2 send update
					if (Math.abs(old_Temperature - adc_temperature) >= 2
							|| Math.abs(old_Ambient - adc_ambient) >= 2) {

						try {
							out.println(adc_temperature
									+ " "
									+ adc_ambient
									+ " "
									+ json.m.fuzzLogic(adc_temperature,
											adc_ambient));

							//if close then blink red led
							if(json.m.fuzzLogic(adc_temperature, adc_ambient).equalsIgnoreCase("close")) {
								System.out.println("Blink red led");
							 yellowPin.low();
							 greenPin.low();
								redPin.high();
								//if halfopen then blink yellow led
							}else if(json.m.fuzzLogic(adc_temperature, adc_ambient).equalsIgnoreCase("halfOpen")) {
								yellowPin.high();
								 greenPin.low();
									redPin.low();
							}else {
								yellowPin.low();
								 greenPin.high();
									redPin.low();
							}
						} catch (Exception qw) {
							System.out.println("Inner Exception");
						}
					}
					old_Temperature = adc_temperature;
					old_Ambient = adc_ambient;
					// System.out.println(old_Ambient);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.out.println("In excetion");
			} finally {
				try {
					System.out.println("Connection down finally");
					serv.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public void run() {
		TimeHandlerMethd();

	}

}