package com.vijaysharma.gezzoo.spi;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.vijaysharma.gezzoo.response.PlayerCharacterState;

public class BoardForm {
	private List<PlayerCharacterState> player_board;
	
	public List<PlayerCharacterState> getPlayerBoard() {
		return ImmutableList.copyOf(player_board);
	}
}
