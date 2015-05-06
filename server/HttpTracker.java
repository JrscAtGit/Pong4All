package server;

import java.io.IOException;
import java.net.InetSocketAddress;

import server.data.DataBase;

import com.sun.net.httpserver.*;


public class HttpTracker {

	private static int port;
	private static boolean debugMode = false;
	private static DataBase dataBase = new DataBase();

	public static void StartTracker() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); // 0 -> backlog. Maximum number of queued incoming connections to allow on the listening socket.

			MyHttpHandler httpHandler = new MyHttpHandler(dataBase, debugMode);
			server.createContext("/hostslist", httpHandler);
			server.setExecutor(null);
			server.start();		 	
		} catch (IOException e) {
			System.err.println("Error - HttpServer not created!" + e);
		}	

		System.out.println("Listening...");
	}

	public static void main(String[] args) {

		if(args.length < 1 || args.length > 2) {
			System.out.println("Usage: tracker <port> [-d]");
			System.exit(-1);
		}

		if (args[0].equals("h") || args[0].equals("-h") || args[0].equals("--h")) {
			System.out.println("Usage: traker <port> [-d]");
			System.exit(0);
		}

		try {
			port = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			System.err.println("Tracker Port must be a unique number!");
			System.exit(-1);
		}

		if(port <= 1024 || port > 65535) {
			System.out.println("Tracker port must be between 1025 and 65535");
			System.exit(-1);
		}

		if (args.length > 1 && args[1].equals("-d"))
			debugMode = true;

		dataBase.load();
		dataBase.getData().addHost("host1", "123.1.2.3", 3456, 4, "waiting");
		dataBase.getData().addHost("host2", "123.13.42.33", 3446, 4, "playing");
		dataBase.getData().addHost("host3", "123.51.25.63", 44456, 4, "waiting");
		StartTracker();
	}
}