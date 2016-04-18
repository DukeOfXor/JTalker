package org.dukeofxor.jtalker.db.dataobjects;

public class BannedClientData {
	
	private String ip;
	private String note;
	
	public BannedClientData(String ip, String note) {
		
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
