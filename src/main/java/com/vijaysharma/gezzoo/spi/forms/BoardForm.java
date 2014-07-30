package com.vijaysharma.gezzoo.spi.forms;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.vijaysharma.gezzoo.response.PlayerCharacterState;

public class BoardForm {
	private List<PlayerCharacterState> player_board = Lists.newArrayList();
	
	public List<PlayerCharacterState> getPlayerBoard() {
		return ImmutableList.copyOf(player_board);
	}
}
