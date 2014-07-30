package com.vijaysharma.gezzoo.spi.forms;

import java.util.List;

import com.vijaysharma.gezzoo.response.PlayerCharacterState;

public class GuessForm {
	private String token;
	private String gameId; 
	private String characterId;
	private List<PlayerCharacterState> board;
	
	public String getToken() {
		return token;
	}
	
	public String getGameId() {
		return gameId;
	}
	
	public String getCharacterId() {
		return characterId;
	}
	
	public List<PlayerCharacterState> getBoard() {
		return board;
	}
}
