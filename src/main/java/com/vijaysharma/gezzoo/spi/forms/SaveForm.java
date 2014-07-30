package com.vijaysharma.gezzoo.spi.forms;

import java.util.List;

import com.vijaysharma.gezzoo.response.PlayerCharacterState;

public class SaveForm {
	private String token;
	private String gameId;
	private List<PlayerCharacterState> board;
	
	public String getToken() {
		return token;
	}
	public String getGameId() {
		return gameId;
	}
	public List<PlayerCharacterState> getBoard() {
		return board;
	}
}
