package com.vijaysharma.gezzoo.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Counters {
	public static final String COUNTERS_ID = "counters-id";
	
	@Id
	private String id;
	private long users;
	
	public Counters() {
		this.id = COUNTERS_ID;
		this.users = 0;
	}
	
	public long next() {
		return ++this.users;
	}
}
