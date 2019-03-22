package application.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Host {

	private StringProperty domain;
	private StringProperty category;
	private IntegerProperty status;
	private LongProperty updatedAt;
	private StringProperty comment;
	
	public final static int STATUS_OK = 1;
	public final static int STATUS_DELETED = 0;
	public final static int STATUS_APPROVED = 2;
	
	public Host() {
		
	}
	
	public Host(String domain, String category) {
		this.domain = new SimpleStringProperty(domain);
		this.category = new SimpleStringProperty(category);
		this.status = new SimpleIntegerProperty(1);
		this.updatedAt = new SimpleLongProperty(System.currentTimeMillis()/1000);
		this.comment = new SimpleStringProperty("Blocked by user");
	}
	
	public Host(String domain, String category, int status, String comment, long utime) {
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

	public StringProperty categoryProperty() {
		return category;
	}
	
	public String getCategory() {
		return category.get();
	}

	public void setCategory(String category) {
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
		return "Host [getDomain()=" + getDomain() + ", getCategory()=" 
				+ getCategory() + ", getStatus()=" + getStatus() 
				+ ", getComment()=" + getComment() + ", getUpdatedAt()="
				+ getUpdatedAt() + "]";
	}
	
}
