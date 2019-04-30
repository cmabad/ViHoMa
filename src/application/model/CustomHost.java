package application.model;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import application.util.properties.Settings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CustomHost extends Host {

	private StringProperty address;
	
	public CustomHost() {
		super();
	}
	
	public CustomHost(String domain, String address)  {
		super(domain,"",Host.STATUS_OK,"",System.currentTimeMillis()/1000);
//		this.address = ;
		setIP(new SimpleStringProperty(address));
	}
	
	public CustomHost(String domain, String address, int status)  {
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
		List<String> regex = new ArrayList<String>();
//		IPV4_REGEX
		regex.add("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");
//		IPV6_HEX4DECCOMPRESSED_REGEX
		regex.add("\\\\A(25[0-5]|2[0-4]\\\\d|[0-1]?\\\\d?\\\\d)(\\\\.(25[0-5]|2[0-4]\\\\d|[0-1]?\\\\d?\\\\d)){3}\\\\z");
//		IPV6_6HEX4DEC_REGEX
		regex.add("\\A((?:[0-9A-Fa-f]{1,4}:){6,6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");
//		IPV6_HEXCOMPRESSED_REGEX
		regex.add("\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)\\z");
//		IPV6_REGEX
		regex.add("\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z");
		
		for (String reg : regex)
			if (Pattern.matches(reg, iP.get())) {
				address = iP;
				return;
			}
	
		throw new IllegalArgumentException(Settings.get("wrongCustomHostIP"));
	}

	@Override
	public String toString() {
		return "CustomHost [getIP()=" + getAddress() + super.toString() + "]";
	}
	
	
}
