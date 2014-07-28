package com.vijaysharma.gezzoo.response;

import java.util.Map.Entry;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;

public class PlayerCharacterState {

	public static PlayerCharacterState from(Entry<Long, Boolean> entry) {
		return from(entry.getKey(), entry.getValue());
	}
	
	public static PlayerCharacterState from(Long id, boolean up) {
		PlayerCharacterState response = new PlayerCharacterState();
		response._id = id;
		response.up = up;
		
		return response;
	}
	
	private Long _id;
	private boolean up;
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Long getId() {
		return _id;
	}
	
	public String get_id() {
		return _id.toString();
	}
	
	public boolean getUp() {
		return up;
	}

	@Override
	public String toString() {
		return "PlayerCharacterStateResponse [_id=" + _id + ", up=" + up + "]";
	}
}
