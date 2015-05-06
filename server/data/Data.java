package server.data;

import java.io.Serializable;
import java.util.ArrayList;


public class Data implements Serializable {

	private ArrayList <GameHost> hostsList;					// List of Games Hosts.

	// Constructors ------------------------------------------------------

	public Data() {
		hostsList = new ArrayList<GameHost>();
	}

	public Data(ArrayList<GameHost> hostsList) {
		super();
		this.hostsList = hostsList;
	}

	// Getters ----------------------------------------------------------

	public ArrayList <GameHost> getHostsList() {
		return hostsList;
	}

	// Setters ----------------------------------------------------------

	public void setHostsList(ArrayList <GameHost> hostsList) {
		this.hostsList = hostsList;
	}

	// Adders ----------------------------------------------------------

	public void addHost(GameHost newHost)
	{
		hostsList.add(newHost);
	}

	public int addHost(String name, String address, int port, int maxSlots, String status)
	{
		int i = 0;
		for( ; i < hostsList.size(); i++) {
			if(i < hostsList.get(i).getID())
				break;
		}
		GameHost newHost = new GameHost(i, name, address, port, maxSlots, status);
		hostsList.add(i, newHost);
		return i;
	}

	// Remover ----------------------------------------------------------

	public boolean removeHost(String address, int port)
	{
		for(int i=0; i<hostsList.size(); i++) {
			if(hostsList.get(i).getAddress().equals(address) && hostsList.get(i).getPort() == port) {
				hostsList.remove(i);
				return true;
			}
		}
		return false;
	}

	public boolean removeHost(int id)
	{
		for(int i=0; i<hostsList.size(); i++) {
			if(hostsList.get(i).getID() == id) {
				hostsList.remove(i);
				return true;
			}
		}
		return false;
	}

	// Updater ----------------------------------------------------------

	public boolean updateHost(int id, String name, String address, int port, int usedSlots, int maxSlots, String status) {
		GameHost host = findHostByID(id);
		if(host != null) {
			host.setName(name);
			host.setAddress(address);
			host.setPort(port);
			host.setUsedSlots(usedSlots);
			host.setMaxSlots(maxSlots);
			host.setStatus(status);
			return true;
		}
		return false;
	}

	// Finders ----------------------------------------------------------

	public GameHost findHostByID(int hostID) {
		for(int i=0; i<hostsList.size(); i++) {
			if(hostsList.get(i).getID() == hostID) {
				return hostsList.get(i);
			}
		}
		return null;
	}

	public GameHost findHostByName(String name) {
		for(int i=0; i<hostsList.size(); i++) {
			if(hostsList.get(i).getName().equals(name)) {
				return hostsList.get(i);
			}
		}
		return null;
	}

	public GameHost findHostByAddrPort(String address, int port) {
		for(int i=0; i<hostsList.size(); i++) {
			if(hostsList.get(i).getAddress().equals(address) && hostsList.get(i).getPort() == port) {
				return hostsList.get(i);
			}
		}
		return null;
	}

	// Display Accessors ----------------------------------------------------------

	public String displayAllHostsAsHTML() {
		String result = "<table border=\"1\">"
				+ "<tr><th>hostID</th> <th>hostName</th> <th>hostAddress</th> <th>hostPort</th> <th>usedSlots</th> <th>maxSlots</th>"
				+ "<th>status</th> <th>startTime</th> <th>lastUpdateTime</th></tr>";

		for(int i=0; i < hostsList.size(); i++) {
			result = result.concat("<tr>" + hostsList.get(i).showInfoAsHTML() + "</tr>");
		}

		result = result.concat("</table>");

		return result;
	}

	public void displayAllHosts() {
		for(int i=0; i < hostsList.size(); i++) {
			System.out.println(hostsList.get(i).showInfo());
		}

		System.out.println("\nTotal hosts: " + hostsList.size());
	}
}