package application.model;

import application.util.WebUtil;
import application.util.properties.Settings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a CustomHost entity. It extends the Host class. It takes
 * the value of Host.CATEGORY_VIHOMA as default category.
 * @attribute address the IP address of the CustomHost
 */
public class CustomHost extends Host {

	private StringProperty address;
	
	public CustomHost() {
		super();
	}
	
	/**
	 * initializes a CustomHost object.
	 * @param domain the domain name of the custom host
	 * @param address the IP address of the custom host
	 * @throws IllegalArgumentException if the address is 0.0.0.0 or does not 
	 * follow neither the IPv4 or IPv6 address formats
	 */
	public CustomHost(String domain, String address) throws IllegalArgumentException {
		super(domain,Host.CATEGORY_VIHOMA,Host.STATUS_ACTIVE,"",System.currentTimeMillis()/1000);
		if ("0.0.0.0".equals(domain))
			throw new IllegalArgumentException(Settings.get("wrongCustomHostDomain"));
		setAddress(new SimpleStringProperty(address.trim()));
	}
	
	/**
	 * 
	 * @param domain the domain name of the custom host
	 * @param address the IP address of the custom host
	 * @param status
	 * @throws IllegalArgumentException if the address is 0.0.0.0 or does not
	 * follow neither the IPv4 or IPv6 address formats
	 */
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

	/**
	 * sets the address of the CustomHost
	 * @param iP the IP address the CustomHost will have
	 * @throws IllegalArgumentException if the iP address does not follow 
	 * neither the IPv4 or IPv6 address formats
	 */
	public void setAddress(StringProperty iP) throws IllegalArgumentException{
		if (WebUtil.checkIpValidity(iP.get()))
			address = iP;
		else
			throw new IllegalArgumentException(Settings.get("wrongCustomHostIP"));
	}

	@Override
	public String toString() {
		return "CustomHost [getAddress()=" + getAddress() + super.toString() + "]";
	}
	
	
}
