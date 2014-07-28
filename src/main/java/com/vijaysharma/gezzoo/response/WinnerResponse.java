package com.vijaysharma.gezzoo.response;

import com.vijaysharma.gezzoo.models.Winner;

public class WinnerResponse {
	public static WinnerResponse from(Winner winner) {
		if ( winner == null )
			return null;
		
		WinnerResponse response = new WinnerResponse();
		response.by = winner.getPlayer().getUserId();
		response.actionId = winner.getGuess().getId();
		
		return response;
	}

	private String by;
	private String actionId;
	
	public String getBy() {
		return by;
	}
	
	public String getActionid() {
		return actionId;
	}
}
