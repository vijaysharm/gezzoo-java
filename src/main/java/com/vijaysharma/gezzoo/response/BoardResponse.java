package com.vijaysharma.gezzoo.response;

import java.util.ArrayList;
import java.util.List;

import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;

public class BoardResponse {
	public static BoardResponse from(Board board) {
		BoardResponse response = new BoardResponse();
		response.setName(board.getName());
		ArrayList<CharacterResponse> characters = new ArrayList<CharacterResponse>();
		for (Character character : board.getCharacters()) {
			characters.add(CharacterResponse.from(character));
		}
		response.setCharacters(characters);
		
		return response;
	}
	
	private String name;
	private List<CharacterResponse> characters;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<CharacterResponse> getCharacters() {
		return characters;
	}
	
	public void setCharacters(List<CharacterResponse> characters) {
		this.characters = characters;
	}

	@Override
	public String toString() {
		return "BoardResponse [name=" + name + ", characters=" + characters
				+ "]";
	}
}
