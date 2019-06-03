package application.model;

import application.util.WebUtil;
import application.util.properties.Settings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CustomHost extends Host {

	private StringProperty address;
	
	public CustomHost() {
		super();
	}
	
	public CustomHost(String domain, String address) throws IllegalArgumentException {
		super(domain,Host.CATEGORY_VIHOMA,Host.STATUS_OK,"",System.currentTimeMillis()/1000);
		if ("0.0.0.0".equals(domain))
			throw new IllegalArgumentException(Settings.get("wrongCustomHostDomain"));
		setIP(new SimpleStringProperty(address.trim()));
	}
	
	public CustomHost(String domain, String address, int status) throws IllegalArgumentException  {
		this(domain,address);
		setStatus(status);
	}

	public StringProperty addressProperty() {
		return address;
	}
	
	public String getAddress() {
		return address.get();
	}

	public void setIP(StringProperty iP){
		if (WebUtil.checkIpValidity(iP.get()))
			address = iP;
		else
			throw new IllegalArgumentException(Settings.get("wrongCustomHostIP"));
	}

	@Override
	public String toString() {
		return "CustomHost [getIP()=" + getAddress() + super.toString() + "]";
	}
	
	
}
