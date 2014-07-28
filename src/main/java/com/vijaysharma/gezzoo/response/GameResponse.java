package com.vijaysharma.gezzoo.response;

import java.util.Date;
import java.util.List;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.common.base.Strings;
import com.vijaysharma.gezzoo.models.Action;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Guess;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.Question;

public class GameResponse {
	public static GameResponse from(Profile user, Game game) {
		GameResponse response = new GameResponse();
		response._id = game.getId().toString();
		response.board = BoardResponse.from(game.getBoard());
		response.ended = game.isEnded();
		response.turn = game.getTurn().getId();
		
		Player me = getMe(user, game);
		Player opponent = getOpponent(user, game);
		
		response.me = PlayerResponse.me(me);
		response.opponent = PlayerResponse.opponent(opponent);
		response.winner = WinnerResponse.from(game.getWinner());
		response.modified = game.getModified();
		response.state = state(user, game);
		
		return response;
	}
	
	public static enum GameState {
		READ_ONLY("read-only"),
		USER_CHARACTER_SELECT("user-select-character"),
		USER_ACTION("user-action"),
		USER_REPLY("user-reply");
		
		private final String state;
		private GameState(String state) {
			this.state = state;
		}
		
		public String getState() {
			return state;
		}
	}
	
	private static GameState state(Profile user, Game game) {
		if (! game.getTurn().getId().equals(user.getId()))
			return GameState.READ_ONLY; //"read-only";
		
		if (game.isEnded())
			return GameState.READ_ONLY; //"read-only";
		
		Player me = getMe(user, game);
		if ( me.getCharacter() == null )
			return GameState.USER_CHARACTER_SELECT; //"user-select-character";
		
		Player opponent = getOpponent(user, game);
		List<Action> opponentActions = opponent.getActions();
		if (opponentActions.isEmpty())
			return GameState.USER_ACTION; // "user-action";
		
		Action action = opponentActions.get(opponentActions.size()-1);
		if (action instanceof Guess)
			return GameState.USER_ACTION; //"user-action";
		
		if (action instanceof Question) {
			Question question = (Question) action;
			if (Strings.isNullOrEmpty(question.getReply()))
				return GameState.USER_REPLY; //"user-reply";
			else
				return GameState.USER_ACTION; //"user-action";
		}
		
		throw new RuntimeException("Unknown game state");
	}
	
	private static Player getMe(Profile user, Game game) {
		List<Player> players = game.getPlayers();
		return user.getId().equals(players.get(0).getUserId()) ? players.get(0) : players.get(1);
	}
	
	private static Player getOpponent(Profile user, Game game) {
		List<Player> players = game.getPlayers();
		return user.getId().equals(players.get(0).getUserId()) ? players.get(1) : players.get(0);
	}
	
	private String _id;
	private Date modified;
	private boolean ended;
	private String turn;
	private BoardResponse board;
	private PlayerResponse opponent;
	private PlayerResponse me;
	private WinnerResponse winner;
 	private GameState state;
	
	public String get_id() {
		return _id;
	}
	
	public BoardResponse getBoard() {
		return board;
	}
	
	public String getTurn() {
		return turn;
	}
	
	public WinnerResponse getWinner() {
		return winner;
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

	public Date getModified() {
		return modified;
	}
	
	public String getState() {
		return state.getState();
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public GameState getGameState() {
		return state;
	}
	
	@Override
	public String toString() {
		return "GameResponse [_id=" + _id + ", ended=" + ended + ", turn="
				+ turn + ", board=" + board + ", opponent=" + opponent + ", winner=" + winner
				+ ", me=" + me + ", state=" + state + ", modified=" + modified + "]";
	}
}
