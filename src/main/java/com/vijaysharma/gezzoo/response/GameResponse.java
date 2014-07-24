package com.vijaysharma.gezzoo.response;

import java.util.List;

import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;

public class GameResponse {
	public static GameResponse from(Profile user, Game game) {
		GameResponse response = new GameResponse();
		response._id = game.getId().toString();
		response.board = BoardResponse.from(game.getBoard());
		response.ended = game.isEnded();
		response.turn = game.getTurn().getId();
		
		List<Player> players = game.getPlayers();
		Player me = user.getId().equals(players.get(0).getUserId()) ? players.get(0) : players.get(1);
		Player opponent = user.getId().equals(players.get(0).getUserId()) ? players.get(1) : players.get(0);
		
		response.me = PlayerResponse.me(me);
		response.opponent = PlayerResponse.opponent(opponent);
		
		return response;
	}
	
// 	TODO: private String state;
//	TODO: private modified

	private String _id;
	private boolean ended;
	private String turn;
	private BoardResponse board;
	private PlayerResponse opponent;
	private PlayerResponse me;

	public String get_id() {
		return _id;
	}
	
	public BoardResponse getBoard() {
		return board;
	}
	
//	public String getState() {
//		return state;
//	}
	
	public String getTurn() {
		return turn;
	}
	
	public boolean getEnded() {
		return ended;
	}

	public PlayerResponse getMe() {
		return me;
	}
	
	public PlayerResponse getOpponent() {
		return opponent;
	}

	@Override
	public String toString() {
		return "GameResponse [_id=" + _id + ", ended=" + ended + ", turn="
				+ turn + ", board=" + board + ", opponent=" + opponent
				+ ", me=" + me + "]";
	}
}
