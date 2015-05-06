package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import server.data.DataBase;

import com.sun.net.httpserver.*;


public class HttpsTracker {

	private static int port;
	private static boolean debugMode = false;
	private static DataBase dataBase = new DataBase();

	public static void StartTracker() {
		try {
			HttpsServer server = HttpsServer.create(new InetSocketAddress(port), 0); // 0 -> backlog. Maximum number of queued incoming connections to allow on the listening socket.

			// initialise the keystore
			char[] password = "123456".toCharArray();
			KeyStore keyStore = KeyStore.getInstance("JKS");
			FileInputStream certificatesFile = new FileInputStream("localhost.jks");
			keyStore.load(certificatesFile, password);
			certificatesFile.close();

			// setup the key manager factory
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keyStore, password);

			// setup the trust manager factory
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(keyStore);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), null);
			
			//HttpsConfigurator httpsConfigurator = new HttpsConfigurator(sslContext);
			
			HttpsConfigurator httpsConfigurator = new HttpsConfigurator(sslContext)
			{
				public void configure(HttpsParameters params) {
					try	{
						// initialise the SSL context
						SSLContext c = SSLContext.getDefault();
						SSLEngine engine = c.createSSLEngine();
						params.setNeedClientAuth(true);
						params.setWantClientAuth(true);
						params.setCipherSuites(engine.getEnabledCipherSuites());
						params.setProtocols(engine.getEnabledProtocols());

						// get the default parameters
						SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
						params.setSSLParameters(defaultSSLParameters);

						System.out.println(params.getClientAddress().getHostName() + ":" + params.getClientAddress().getPort());
					}
					catch (Exception ex) {
						System.err.println("Failed to create HTTPS port");
					}
				}
			};
			
			server.setHttpsConfigurator(httpsConfigurator);

			MyHttpHandler httpHandler = new MyHttpHandler(dataBase, debugMode);
			server.createContext("/hostslist", httpHandler);
			server.setExecutor(null);
			server.start();		 	
		} catch (IOException e) {
			System.err.println("Error - HttpServer not created!" + e);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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