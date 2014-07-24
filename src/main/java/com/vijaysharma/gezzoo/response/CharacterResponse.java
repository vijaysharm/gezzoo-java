package com.vijaysharma.gezzoo.response;

import com.vijaysharma.gezzoo.models.Character;

public class CharacterResponse {
	public static CharacterResponse from(Character character) {
		CharacterResponse response = new CharacterResponse();
		response.set_id(character.getId().toString());
		response.setImg(character.getImage());
		response.setName(character.getName());
		
		return response;
	}
	
	private String _id;
	private String name;
	private String img;
	
	public String get_id() {
		return _id;
	}
	
	public void set_id(String id) {
		this._id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getImg() {
		return img;
	}
	
	public void setImg(String img) {
		this.img = img;
	}
	
	@Override
	public String toString() {
		return "CharacterResponse [_id=" + _id + ", name=" + name + ", img="
				+ img + "]";
	}
}
