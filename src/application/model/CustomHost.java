package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CustomHost extends Host {

	private StringProperty address;
	
	public CustomHost() {
		super();
	}
	
	public CustomHost(String domain, String address) {
		super(domain,"",Host.STATUS_OK,"",System.currentTimeMillis()/1000);
		this.address = new SimpleStringProperty(address);
	}
	
	public CustomHost(String domain, String address, int status) {
		this(domain,address);
		setStatus(status);
	}

	public StringProperty addressProperty() {
		return address;
	}
	
	public String getAddress() {
		return address.get();
	}

	public void setIP(StringProperty iP) {
		address = iP;
	}

	@Override
	public String toString() {
		return "CustomHost [getIP()=" + getAddress() + super.toString() + "]";
	}
	
	
}
