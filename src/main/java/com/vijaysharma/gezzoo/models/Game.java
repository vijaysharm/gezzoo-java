package com.vijaysharma.gezzoo.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnSave;
import com.vijaysharma.gezzoo.database.DatabaseService;
import com.vijaysharma.gezzoo.models.Player.PlayerBuilder;

@Entity
public class Game {
	@Id private Long id;
	@Load private Ref<Board> board;
	@Load private Ref<Profile> turn;
	@Index private List<Player> players = Lists.newArrayList();
	@Index private boolean ended;
	private Date modified;
	private Winner winner;
	
	public Long getId() {
		return id;
	}
	
	public Board getBoard() {
		return board.get();
	}
	
	public Profile getTurn() {
		return turn.get();
	}
	
	public List<Player> getPlayers() {
		return ImmutableList.copyOf(players);
	}
	
	public Winner getWinner() {
		return winner;
	}
	
	public void setWinner(Winner winner) {
		this.winner = winner;
	}

//	public Map<String, Player> getPlayerMap() {
//		return Maps.uniqueIndex(players, new Function<Player, String>() {
//			@Override
//			public String apply(Player player) {
//				return player.getUserId();
//			}
//		});
//	}
	
	public void setEnded(boolean ended) {
		this.ended = ended;
	}
	
	public boolean isEnded() {
		return ended;
	}
	
	public void setTurn(Profile turn) {
		this.turn = Ref.create(turn);
	}
	
	@OnSave
	void onSave() {
		modified = new Date();
	}

	@Override
	public String toString() {
		return "Game [id=" + id + ", players=" + players + ", ended=" + ended
				+ ", getBoard()=" + getBoard() + ", getTurn()=" + getTurn()
				+ ", modified=" + modified + "]";
	}

	public static class GameBuilder {
		private final Game game;
		private final DatabaseService service;
		
		public GameBuilder(DatabaseService service) {
			this.service = service;
			this.game = new Game();
		}
		
		public GameBuilder board(Board board) {
			game.board = Ref.create(board);
			return this;
		}
		
		public GameBuilder turn(Profile player) {
			game.turn = Ref.create(player);
			return this;
		}
		
		public GameBuilder addPlayer(Profile profile, Map<Long, Boolean> board) {
			if (game.players.size() >= 2)
				throw new IllegalArgumentException("Can't have more than 2 players per game");

			Player p = new PlayerBuilder(profile).setBoard(board).build();
			game.players.add(p);
			service.save(p);

			return this;
		}
		
		public GameBuilder ended(boolean ended) {
			game.ended = ended;
			return this;
		}
		
		public Game build() {
			return game;
		}
	}
}
