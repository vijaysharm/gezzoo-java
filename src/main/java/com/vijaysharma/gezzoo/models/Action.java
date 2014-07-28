package com.vijaysharma.gezzoo.models;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Action {
	@Id private String id;
	private Date modified;
	
	protected Action() {};
	protected Action(String id) { this.id = id; }
	
	public String getId() {
		return id;
	}
	
	public Date getModified() {
		return modified;
	}
	
	void onSave() {
		modified = new Date();
	}
}
