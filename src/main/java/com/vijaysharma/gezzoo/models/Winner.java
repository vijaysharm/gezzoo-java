package com.vijaysharma.gezzoo.models;

import com.googlecode.objectify.Ref;

public class Winner {
	private Ref<Player> player;
	private Guess guess;
	
	public Winner(Player player, Guess guess) {
		this.guess = guess;
		this.player = Ref.create(player);
	}
	
	public Player getPlayer() {
		return player.get();
	}
	
	public Guess getGuess() {
		return guess;
	}
}
