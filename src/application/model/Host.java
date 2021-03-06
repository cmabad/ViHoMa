package application.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a blocked Host entity. 
 * @attribute domain the domain name of the host
 * @attribute category the category to which the host belongs (only CATEGORY_VIHOMA
 * available)
 * @attribute status each domain can be active (default) or deleted. If active, it
 * will be persisted to the hosts file.
 * @attribute updatedAt the unix time in which the host was added
 * @attribute comment additional information
 * @attribute STATUS_ACTIVE 1
 * @attribute STATUS_DELETED 0
 * @attribute CATEGORY_VIHOMA -1
 * @attribute CATEGORY_STEVENBLACK_FAKENEWS 1 
 * @attribute CATEGORY_STEVENBLACK_GAMBLING 2
 * @attribute CATEGORY_STEVENBLACK_PORN 4
 * @attribute CATEGORY_STEVENBLACK_SOCIAL 8
 */
public class Host {

	private StringProperty domain;
	private IntegerProperty category;
	private IntegerProperty status;
	private LongProperty updatedAt;
	private StringProperty comment;
	
	public final static int STATUS_ACTIVE = 1;
	public final static int STATUS_DELETED = 0;
	
	public final static int CATEGORY_VIHOMA = -1;
	public final static int CATEGORY_STEVENBLACK_BASICS = 0;
	public final static int CATEGORY_STEVENBLACK_FAKENEWS = 1;
	public final static int CATEGORY_STEVENBLACK_GAMBLING = 2;
	public final static int CATEGORY_STEVENBLACK_PORN = 4;
	public final static int CATEGORY_STEVENBLACK_SOCIAL = 8;
	
	public Host() {
		
	}
	
	public Host(String domain) {
		this.domain = new SimpleStringProperty(domain.trim());
		this.category = new SimpleIntegerProperty(CATEGORY_VIHOMA);
		this.status = new SimpleIntegerProperty(Host.STATUS_ACTIVE);
		this.updatedAt = new SimpleLongProperty(System.currentTimeMillis()/1000);
		this.comment = new SimpleStringProperty("Blocked by user");
	}
	
	public Host(String domain, Integer category) {
		this(domain);
		this.category = new SimpleIntegerProperty(category);
	}
	
	public Host(String domain, Integer category, int status, String comment, long utime) {
		this(domain,category);
		this.status = new SimpleIntegerProperty(status);
		this.updatedAt = new SimpleLongProperty(utime);
		this.comment = new SimpleStringProperty(comment);
	}

	public StringProperty domainProperty() {
		return domain;
	}
	
	public String getDomain() {
		return domain.get();
	}
	
	public void setDomain(String domain) {
		this.domain.set(domain);
	}
	
	public IntegerProperty categoryProperty() {
		return category;
	}
	
	public Integer getCategory() {
		return category.get();
	}
	
	public void setCategory(Integer category) {
		this.category.set(category);
	}

	public IntegerProperty statusProperty() {
		return status;
	}
	
	public Integer getStatus() {
		return status.get();
	}

	public void setStatus(Integer status) {
		this.status.set(status);
	}
	
	public BooleanProperty activeProperty() {
		return new SimpleBooleanProperty((status.get() & 1) == 1);
	}
	
	public Boolean isActive() {
		return (status.get() & 1) == 1;
	}

	public void setActive(Boolean active) {
		this.status.set((active? 1:0));
	}

	public LongProperty UpdatedAtProperty() {
		return updatedAt;
	}
	
	public Long getUpdatedAt() {
		return updatedAt.get();
	}

	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt.set(updatedAt);
	}

	public StringProperty commentProperty() {
		return comment;
	}
	
	public String getComment() {
		return comment.get();
	}

	public void setComment(String comment) {
		this.comment.set(comment);
	}

	@Override
	public String toString() {
		return "Host [getDomain()=" + getDomain()
				+ ", getStatus()=" + getStatus() 
				+ ", getComment()=" + getComment() + ", getUpdatedAt()="
				+ getUpdatedAt() + "]";
	}
	
}
