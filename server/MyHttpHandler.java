package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.data.DataBase;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class MyHttpHandler implements HttpHandler {

	private DataBase dataBase;
	private boolean debugMode;
	private String hostAddress;
	private int hostPort;

	public MyHttpHandler(DataBase dataBase, boolean debugMode) {
		this.dataBase = dataBase;
		this.debugMode = debugMode;
	}

	public void handle(HttpExchange httpExchange) {

		hostAddress = httpExchange.getRemoteAddress().getHostName();
		hostPort = httpExchange.getRemoteAddress().getPort();

		String method = httpExchange.getRequestMethod();
		String query = httpExchange.getRequestURI().getQuery();
		String requestBody = readRequestBody(httpExchange);
		String responseMsg = "error";

		if(debugMode) {
			System.out.println("Processing request from " + hostAddress + ":" + hostPort);
			System.out.println(method + " " + query);
		}

		if ( method.equals("GET") )
			responseMsg = processGET(query);
		else if ( method.equals("PUT") )
			responseMsg = processPUT(query, requestBody);
		else if ( method.equals("POST") )
			responseMsg = processPOST(query, requestBody);
		else if ( method.equals("DELETE") )
			responseMsg = processDELETE(query);
		else
			System.err.println("Unknown method: " + method);

		sendReply(httpExchange, responseMsg);	
	}

	public String readRequestBody(HttpExchange httpExchange) {
		String inputLine;
		String requestBody = new String();

		BufferedReader in = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));

		try {
			while ((inputLine = in.readLine()) != null) {
				requestBody = requestBody.concat(inputLine);
				if(debugMode)
					System.out.println(inputLine);
			}
			in.close();
			return requestBody;
		} catch (IOException e) {
			System.err.println("Error - readRequestBody: ");
			e.printStackTrace();
		}
		return null;
	}

	public void sendReply(HttpExchange httpExchange, String responseMsg) {
		try {
			httpExchange.getResponseHeaders().set("Content-Type","text/html");
			httpExchange.sendResponseHeaders(200, responseMsg.length());

			OutputStream outputStream = httpExchange.getResponseBody();
			outputStream.write(responseMsg.getBytes());
			outputStream.close();
		} catch (IOException e) {
			System.err.println("Error - sendReply: ");
			e.printStackTrace();
		}
	}

	// ============================  Methods Processors ============================================

	public String processGET(String query) {
		String responseMsg = "error";
		int activeHosts = dataBase.getData().getHostsList().size();

		if(query == null)
			if(activeHosts > 0)
				responseMsg = "<html> <head> <title>Pong4All</title> </head> <body> <h1>Pong4All</h1> <h3>" + activeHosts + " active host(s)</h3> <a href=\"../hostslist?activehosts\">View Host(s)</a> </body> </html>";
			else
				responseMsg = "<html> <head> <title>Pong4All</title> </head> <body> <h1>Pong4All</h1> <h3>No active hosts</h3> </body> </html>";
		else if(query.contains("activehosts") && activeHosts > 0)
			if(query.contains("Type=XML"))
				responseMsg = "";
			else if(query.contains("Type=JSON"))
				responseMsg = "";
			else
				responseMsg = "<html> <head> <title>Pong4All HostsList</title> </head> <body> <h1>Pong4All</h1>"
						+ dataBase.getData().displayAllHostsAsHTML()
						+ "</body> </html>";
		else if(query.contains("activehosts") && activeHosts == 0)
			if(query.contains("Type=XML"))
				responseMsg = "";
			else if(query.contains("Type=JSON"))
				responseMsg = "";
			else
				responseMsg = "<html> <head> <title>Pong4All</title> </head> <body> <h1>Pong4All</h1> <h3>No active hosts</h3> </body> </html>";
		else
			responseMsg = "<html> <head> <title>Pong4All</title> </head> <body> <h1>Pong4All</h1> <h3>" + activeHosts + " active host(s)</h3> <a href=\"../hostslist?activehosts\">View Host(s)</a> </body> </html>";

		return responseMsg;
	}

	public String processPUT(String query, String requestBody) {
		boolean valid = true;
		String responseMsg = "error";
		int newHostID = -1;

		try {
			JSONObject json = (JSONObject)new JSONParser().parse(requestBody);
			JSONObject host = (JSONObject) json.get("host");
			String name = (String) host.get("name");
			int port = Integer.parseInt(host.get("port").toString());
			int maxSlots = Integer.parseInt(host.get("maxSlots").toString());
			
			//int maxSlots = Integer.parseInt(maxSlotsStr);
			newHostID = dataBase.getData().addHost(name, hostAddress, port, maxSlots, "waiting");
		} catch(NumberFormatException e) {
			valid = false;
			System.err.println("NumberFormatException");
		} catch (ParseException e) {
			valid = false;
			System.err.println("ParseException");
		}

		if(query == null)
			if(valid)
				responseMsg = "<html> <head> <title>New Host Created!</title> </head> <body> <h1>Created!</h1> <div>" + newHostID + "</div> </body> </html>";
			else
				responseMsg = "<html> <head> <title>Invalid deleted!</title> </head> <body> <h1>Invalid deleted request!</h1> </body> </html>";				
		else if(query.contains("Type=XML"))
			if(valid)
				responseMsg = "";
			else
				responseMsg = "";
		else if(query.contains("Type=JSON"))
			if(valid)
				responseMsg = "{\"success\"{\"ID\":" + newHostID + "}}";
			else
				responseMsg = "{\"error\"{\"ID\":-1}}";
		return responseMsg;
	}

	public String processPOST(String query, String requestBody) {
		boolean success = false;
		boolean valid = true;
		String responseMsg = "error";
		String[] hostInfo = new String[7];
		hostInfo = requestBody.split(";");

		try {
			int hostID = Integer.parseInt(hostInfo[0]);
			int usedSlots = Integer.parseInt(hostInfo[4]);
			int maxSlots = Integer.parseInt(hostInfo[5]);
			if(dataBase.getData().updateHost(hostID, hostInfo[1], hostAddress, hostPort, usedSlots, maxSlots, hostInfo[6]))
				success = true;
		} catch(NumberFormatException e) {
			valid = false;
		}

		if(query == null)
			if(valid && success)
				responseMsg = "<html> <head> <title>Host updated!</title> </head> <body> <h1>Success</h1> <h3>Host updated!</h3> </body> </html>";
			else if(valid)
				responseMsg = "<html> <head> <title>No Update!</title> </head> <body> <h1>Error</h1> <h3>Host not found!</h3> </body> </html>";
			else
				responseMsg = "<html> <head> <title>Invalid update!</title> </head> <body> <h1>Error</h1> <h3>Invalid update request!</h3> </body> </html>";	
		else if(query.contains("Type=XML"))
			if(valid && success)
				responseMsg = "";
			else if(valid)
				responseMsg = "";
			else
				responseMsg = "";
		else if(query.contains("Type=JSON"))
			if(valid && success)
				responseMsg = "";
			else if(valid)
				responseMsg = "";
			else
				responseMsg = "";
		return responseMsg;
	}

	public String processDELETE(String query) {
		boolean success = false;
		boolean valid = true;
		String responseMsg = "error";
		String[] hostInfo = new String[2];

		if(query == null)
			return "<html> <head> <title>No delete!</title> </head> <body> <h1>Error</h1> <h3>Nothing to delete!</h3> </body> </html>";

		hostInfo = query.split("&");

		try {
			int hostID = Integer.parseInt(hostInfo[0]);
			if(dataBase.getData().removeHost(hostID))
				success = true;
		} catch(NumberFormatException e) {
			valid = false;
		}

		if(query.contains("Type=XML"))
			if(valid && success)
				responseMsg = "";
			else if(valid)
				responseMsg = "";
			else
				responseMsg = "";
		else if(query.contains("Type=JSON"))
			if(valid && success)
				responseMsg = "";
			else if(valid)
				responseMsg = "";
			else
				responseMsg = "";
		else {
			if(valid && success)
				responseMsg = "<html> <head> <title>Host deleted!</title> </head> <body> <h1>Success</h1> <h3>Host deleted!</h3> </body> </html>";
			else if(valid)
				responseMsg = "<html> <head> <title>No delete!</title> </head> <body> <h1>Error</h1> <h3>Host not found!</h3> </body> </html>";
			else
				responseMsg = "<html> <head> <title>Invalid deleted!</title> </head> <body> <h1>Error</h1> <h3>Invalid deleted request!</h3> </body> </html>";
		}

		return responseMsg;
	}
}