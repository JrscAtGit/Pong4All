package server.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataBase {

	private String dbFileName = "dataBase.sav";					// DataBase file name.
	private Data data = new Data();
	
	public Data getData() {
		return data;
	}

	//##################################### Data Base File Loading Routines ############################################

	public Boolean load()
	{
		try
		{
			ObjectInputStream inputStream;
			inputStream = new ObjectInputStream(new FileInputStream(dbFileName));
			data = (Data) inputStream.readObject();
			inputStream.close();

			System.out.println("File: Data Base loaded");
			System.out.println(data.getHostsList().size() + " Host(s) in Data Base");
			return true;
		}
		catch (ClassNotFoundException e) {
			System.err.println("File Load Error: Data Base File corrupted");
			return false;
		}
		catch (FileNotFoundException e)
		{
			System.err.println("File Loading Error: Data Base File not found");
			return false;
		}
		catch (IOException e) {
			System.err.println("File Loading Error: Data Base File read error");
			return false;
		}
	}

	//##################################### Files Saving Routines #####################################################

	public boolean save()
	{
		try
		{
			ObjectOutputStream outputStream;
			outputStream = new ObjectOutputStream(new FileOutputStream(dbFileName));
			outputStream.writeObject(data);
			outputStream.close();
			System.out.println("File: Data Base Saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			System.err.println("File Saving Error: Data Base File not found");
			return false;
		}
		catch (IOException e) {
			System.err.println("File Saving Error: Data Base File write error");
			return false;
		}
	}
}