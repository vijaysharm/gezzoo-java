package com.vijaysharma.gezzoo.response;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Player;

public class PlayerResponse {
	public static PlayerResponse me(Player player) {
		Character character = player.getCharacter();
		PlayerResponse response = new PlayerResponse();
		response._id = player.getUserId();
		response.username = player.getProfile().getName();
		response.character = character == null ? null : character.getId().toString(); 
		response.board = Lists.newArrayList();
		for ( Entry<Long, Boolean> entry : player.getBoard().entrySet() ) {
			response.board.add(PlayerCharacterStateResponse.from(entry));
		}
		
		return response;
	}
	
	public static PlayerResponse opponent(Player player) {
		PlayerResponse response = new PlayerResponse();
		response._id = player.getUserId();
		response.username = player.getProfile().getName();
		
		return response;
	}
	
	private String _id;
	private String username;
	private String character;
	private List<PlayerCharacterStateResponse> board;
	// TODO: Actions
	
	public String get_id() {
		return _id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public List<PlayerCharacterStateResponse> getBoard() {
		return board;
	}
	
	public String getCharacter() {
		return character;
	}
	
	@Override
	public String toString() {
		return "PlayerResponse [_id=" + _id + ", username=" + username
				+ ", character=" + character + ", board=" + board + "]";
	}
}
