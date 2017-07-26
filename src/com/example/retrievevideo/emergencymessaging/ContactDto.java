package com.example.retrievevideo.emergencymessaging;

/**
 * Created by m.susmitha on 7/5/15.
 */
public class ContactDto {
	long id;
	String  name;
	String number;
	int groupid;
	String emailId;
	
	

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	@Override
	public String toString() {
		return "ContactDto{" +
				"id=" + id +
				", name='" + name + '\'' +
				", number='" + number + '\'' +
				", groupid=" + groupid +
				'}';
	}
}
