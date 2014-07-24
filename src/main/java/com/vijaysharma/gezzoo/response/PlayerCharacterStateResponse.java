package com.vijaysharma.gezzoo.response;

import java.util.Map.Entry;

public class PlayerCharacterStateResponse {

	public static PlayerCharacterStateResponse from(Entry<Long, Boolean> entry) {
		PlayerCharacterStateResponse response = new PlayerCharacterStateResponse();
		response._id = entry.getKey().toString();
		response.up = entry.getValue();

		return response;
	}
	
	private String _id;
	private boolean up;
	
	public String get_id() {
		return _id;
	}
	
	public boolean getUp() {
		return up;
	}

	@Override
	public String toString() {
		return "PlayerCharacterStateResponse [_id=" + _id + ", up=" + up + "]";
	}
}
