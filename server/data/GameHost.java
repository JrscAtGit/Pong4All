package server.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GameHost implements Serializable {

	private int id = -1;									// Host ID.
	private String name = new String();						// Host name.
	private String address = new String();					// Public IP address.
	private int port = -1;									// Public port.
	private int usedSlots = -1;								// Players in the game.
	private int maxSlots = -1;								// Max allowed players.
	private String status = new String("");					// Host status.
	private Date startTime = new Date();					// Host registration time.
	private Date lastUpdateTime = new Date();				// Last host update time.
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


	// Constructors    --------------------------------------------------------

	public GameHost(int id, String name, String address, int port, int maxSlots, String status)
	{
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
		this.usedSlots = 1;
		this.maxSlots = maxSlots;
		this.status = status;
		this.startTime = new Date();
		this.lastUpdateTime = new Date();
	}

	// Setters    ---------------------------------------------------------

	public void setID(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUsedSlots(int usedSlots) {
		this.usedSlots = usedSlots;
	}

	public void setMaxSlots(int maxSlots) {
		this.maxSlots = maxSlots;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}	

	// Getters    ---------------------------------------------------------

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getStatus() {
		return status;
	}

	public int getUsedSlots() {
		return usedSlots;
	}

	public int getMaxSlots() {
		return maxSlots;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	// Display Accessors ----------------------------------------------------------

	public String showInfoAsHTML() {
		return "<td>" + id + "</td><td>" + name + "</td><td>" + address + "</td><td>" + port + "</td><td>"
				+ usedSlots + "</td><td>" + maxSlots + "</td><td>" + status + "</td><td>" + dateFormat.format(startTime) + "</td><td>" + dateFormat.format(lastUpdateTime) + "</td>";
	}
	
	public String showInfo() {
		return id + " - " + name + " " + address + ":" + port + " " + usedSlots + "//" + maxSlots + " " + status + " " + dateFormat.format(startTime) + " " + dateFormat.format(lastUpdateTime);
	}
}