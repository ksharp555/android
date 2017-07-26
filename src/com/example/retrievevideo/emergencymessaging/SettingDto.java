package com.example.retrievevideo.emergencymessaging;

/**
 * Created by m.susmitha on 7/5/15.
 */
public class SettingDto {
	int id;
	String key;
	String value;
	String key_reg_done="key_reg_done";
	String key_reg_done_yes="true";
	String key_reg_done_no="false";


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SettingDto{" +
				"id=" + id +
				", key='" + key + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
