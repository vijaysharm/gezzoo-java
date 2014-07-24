package com.vijaysharma.gezzoo.models;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Stringify;
import com.vijaysharma.gezzoo.utilities.LongStringifier;

@Entity
public class Player {
	@Id 
	private Long id;
	
	@Index 
	private String userId;
	
	@Load 
	private Ref<Profile> userProfile;

	@Load 
	private Ref<Character> character;
	
	@Stringify(LongStringifier.class) 
	private Map<Long, Boolean> board = new HashMap<Long, Boolean>();
	
	// TODO: Actions
	
	private Player() {}
	
	public String getUserId() {
		return userId;
	}
	
	public Profile getProfile() {
		return userProfile.get();
	}

	public Map<Long, Boolean> getBoard() {
		return board;
	}
	
	public Character getCharacter() {
		if ( character == null )
			return null;
		
		return character.get();
	}
	
	public void setCharacter(Character character) {
		this.character = Ref.create(character);
	}
	
	@Override
	public String toString() {
		return "Player [id=" + id + ", userId=" + userId + ", board=" + board
				+ ", getProfile()=" + getProfile() + ", getBoard()="
				+ getBoard() + ", getCharacter() " + getCharacter() + "]";
	}

	public static class PlayerBuilder {
		private Player player;
		
		public PlayerBuilder(Profile profile) {
			player = new Player();
			player.userId = profile.getId();
			player.userProfile = Ref.create(profile);
		}
		
		public PlayerBuilder setBoard(Map<Long, Boolean> board) {
			player.board.clear();
			player.board.putAll(board);

			return this;
		}
		
		public Player build() {
			return player;
		}
	}
}
