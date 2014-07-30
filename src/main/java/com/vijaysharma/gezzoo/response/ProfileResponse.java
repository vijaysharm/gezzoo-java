package com.vijaysharma.gezzoo.response;

import com.vijaysharma.gezzoo.models.Profile;

public class ProfileResponse {
	public static ProfileResponse from(Profile profile) {
		ProfileResponse response = new ProfileResponse();
		response.setName(profile.getName());
		response.setToken(profile.getId());
		response.id = profile.getId();
		
		return response;
	}
	
	private String id;
	private String name;
	private String token;

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "ProfileResponse [name=" + name + ", token=" + token + "]";
	}
}
