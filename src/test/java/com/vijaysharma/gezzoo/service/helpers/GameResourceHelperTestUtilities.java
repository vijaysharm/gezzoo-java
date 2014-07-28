package com.vijaysharma.gezzoo.service.helpers;

import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.vijaysharma.gezzoo.models.Action;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Guess;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.Question;
import com.vijaysharma.gezzoo.models.Winner;
import com.vijaysharma.gezzoo.response.ActionResponse;
import com.vijaysharma.gezzoo.response.GameResponse;
import com.vijaysharma.gezzoo.response.PlayerCharacterState;

public class GameResourceHelperTestUtilities {
	public static void checkGame(Board expectedBoard, 
								 Profile expectedUser,
							     Profile expectedOpponent, 
							     Game game) { 
		List<PlayerCharacterState> playerBoard = Lists.newArrayList(
			PlayerCharacterState.from(expectedBoard.getCharacters().get(0).getId(), true)
		);		
		GameAssertionBuilder.check(game)
			.board(expectedBoard)
			.ended(false)
			.turn(expectedUser)
			.player(playerBoard, expectedUser, null)
			.player(playerBoard, expectedOpponent, null);
	}

	public static class GameAssertionBuilder {
		private final Game game;

		public static GameAssertionBuilder check(Game game) {
			return new GameAssertionBuilder(game);
		}
		
		private GameAssertionBuilder(Game game) {
			this.game = game;
			assertNotNull(game.getId());
			assertNotNull(game.getBoard());
			assertEquals(2, game.getPlayers().size());
		}
		
		public GameAssertionBuilder board(Board board) {
			assertEquals(board.getCharacters().size(), game.getBoard().getCharacters().size());
			assertEquals(board.getId(), game.getBoard().getId());
			assertEquals(board.getCharacters().get(0).getName(), game.getBoard().getCharacters().get(0).getName());
			assertEquals(board.getCharacters().get(0).getImage(), game.getBoard().getCharacters().get(0).getImage());
			assertEquals(board.getCharacters().get(0).getId(), game.getBoard().getCharacters().get(0).getId());
			return this;
		}
		
		public GameAssertionBuilder ended(boolean ended, Winner winner) {
			assertEquals(ended, game.isEnded());
			if ( winner == null ) {
				assertNull(game.getWinner());
			} else {
				assertEquals(
					winner.getGuess().getCharacter().getId(), 
					game.getWinner().getGuess().getCharacter().getId()
				);
				
				assertEquals(
					winner.getPlayer().getUserId(), 
					game.getWinner().getPlayer().getUserId()
				);
			}
				
			return this;
		}
		
		public GameAssertionBuilder ended(boolean ended) {
			return this.ended(ended, null);
		}
		
		public GameAssertionBuilder turn(Profile turn) {
			assertNotNull(game.getTurn());
			assertNotNull(turn.getId(), game.getTurn().getId());
			assertNotNull(turn.getName(), game.getTurn().getName());			
			return this;
		}
		
		public GameAssertionBuilder player(
			List<PlayerCharacterState> board,
			Profile profile, 
			Character character,
			Action...actions
		) {
			Player player = findPlayerInGame(game, profile);
			Long actualCharacterId = player.getCharacter() == null ? null : player.getCharacter().getId();
			Long characterId = character == null ? null : character.getId();
			List<Action> acts = player.getActions();
			
			assertEquals(profile.getId(), player.getUserId());
			assertEquals(profile.getId(), player.getProfile().getId());
			assertEquals(profile.getName(), player.getProfile().getName());
			assertEquals(characterId, actualCharacterId);
			
			assertEquals(board.size(), player.getBoard().size());
			for ( int index = 0; index < board.size(); index++ ) {
				PlayerCharacterState expectedState = board.get(index);
				assertEquals(true, player.getBoard().containsKey(expectedState.getId()));
				assertEquals(expectedState.getUp(), player.getBoard().get(expectedState.getId()));
			}
			
			if ( actions == null ) {
				assertEquals(0, acts.size());
				return this;
			}
			
			assertEquals(actions.length, acts.size());
			for ( int index = 0; index < actions.length; index++ ) {
				Action expectedAction = actions[index];
				Action actualAction = acts.get(index);
				
				assertEquals(expectedAction.getClass(), actualAction.getClass());
				if ( Question.class.equals(expectedAction.getClass()) ) {
					Question expected = (Question) expectedAction;
					Question actual = (Question) actualAction;
					assertEquals(expected.getQuestion(), actual.getQuestion());
					assertEquals(expected.getReply(), actual.getReply());
				} else if ( Guess.class.equals(expectedAction.getClass()) ) {
					Guess expected = (Guess) expectedAction;
					Guess actual = (Guess) actualAction;
					assertEquals(expected.getCharacter().getId(), actual.getCharacter().getId());
					assertEquals(expected.getCharacter().getImage(), actual.getCharacter().getImage());
					assertEquals(expected.getCharacter().getName(), actual.getCharacter().getName());
				}
			}
			
			return this;
		}
		
		private Player findPlayerInGame(Game game, Profile profile) {
			for (Player player : game.getPlayers() ) {
				if (player.getUserId().equals(profile.getId()))
					return player;
			}
			
			throw new IllegalArgumentException();
		}
	}
	
	public static void checkResponse(Board expectedBoard,
									 Profile expectedUser, 
									 Profile expectedOpponent,
									 Profile expectedTurn,
									 GameResponse response) {
		List<PlayerCharacterState> playerBoard = Lists.newArrayList(
			PlayerCharacterState.from(expectedBoard.getCharacters().get(0).getId(), true)
		);
		
		ResponseAssertionBuilder.check(response)
			.ended(false)
			.board(expectedBoard)
			.turn(expectedTurn)
			.me(playerBoard, expectedUser, null)
			.opponent(expectedOpponent);
	}

	public static class ResponseAssertionBuilder {
		private final GameResponse response;
		
		public static ResponseAssertionBuilder check(GameResponse response) {
			return new ResponseAssertionBuilder(response);
		}
		
		private ResponseAssertionBuilder(GameResponse response) {
			this.response = response;
			assertNotNull(response);
			assertNotNull(response.get_id());
		}
		
		public ResponseAssertionBuilder ended(boolean expected) {
			return this.ended(expected, null);
		}
		
		public ResponseAssertionBuilder ended(boolean expected, Winner winner) {
			assertEquals(expected, response.getEnded());
			if ( winner == null ) {
				assertNull(response.getWinner());
			} else {
				assertNotNull(response.getWinner().getActionid());
				assertEquals(winner.getPlayer().getUserId(), response.getWinner().getBy());
			}
			
			return this;
		}
		
		public ResponseAssertionBuilder turn(Profile turn) {
			assertEquals(turn.getId(), response.getTurn());
			return this;
		}
		
		public ResponseAssertionBuilder board(Board board) {
			assertEquals(board.getCharacters().size(), response.getBoard().getCharacters().size());
			assertEquals(board.getName(), response.getBoard().getName());
			return this;
		}
		
		public ResponseAssertionBuilder me(List<PlayerCharacterState> board, Profile me, Character character, Action...actions) {
			String characterId = character == null ? null : character.getId().toString();
			
			assertEquals(board.size(), response.getMe().getBoard().size());
			for ( int index = 0; index < board.size(); index++ ) {
				PlayerCharacterState expectedState = board.get(index);
				PlayerCharacterState actualState = response.getMe().getBoard().get(index);

				assertEquals(expectedState.getId(), actualState.getId());
				assertEquals(expectedState.getUp(), actualState.getUp());
			}
			
			assertEquals(me.getId(), response.getMe().get_id());
			assertEquals(me.getName(), response.getMe().getUsername());
			assertEquals(me.getName(), response.getMe().getUsername());
			assertEquals(characterId, response.getMe().getCharacter());
			checkActions(me, actions, response.getMe().getActions());
			
			return this;
		}
		
		public ResponseAssertionBuilder opponent(Profile opponent, Action...actions) {
			assertEquals(opponent.getId(), response.getOpponent().get_id());
			assertEquals(opponent.getName(), response.getOpponent() .getUsername());
			assertNull(response.getOpponent().getCharacter());
			assertNull(response.getOpponent().getBoard());
			checkActions(opponent, actions, response.getOpponent().getActions());
			
			return this;
		}
		
		private void checkActions(Profile user, Action[] actions, List<ActionResponse> response) {
			if ( actions == null ) {
				assertEquals(0, response.size());
				return;
			}
			
			assertEquals(actions.length, response.size());
			for ( int index = 0; index < actions.length; index++ ) {
				Action expectedAction = actions[index];
				ActionResponse actualAction = response.get(index);

				assertEquals(user.getId(), actualAction.getBy());
				assertNotNull(actualAction.get_id());
				if ( Question.class.equals(expectedAction.getClass())) {
					Question question = (Question) expectedAction;
					assertEquals(question.getQuestion(), actualAction.getValue());
					assertEquals("question", actualAction.getAction());
					if ( question.getReply() == null )
						assertNull(actualAction.getReply());
					else
						assertEquals(question.getReply(), actualAction.getReply().getValue());
				} else if ( Guess.class.equals(expectedAction.getClass())) {
					Guess guess = (Guess) expectedAction;
					assertEquals("guess", actualAction.getAction());
					assertEquals(guess.getCharacter().getId().toString(), actualAction.getValue());
				}
			}
		}
	}
	
	public static Player findPlayer(Profile user, Game game) {
		for (Player player : game.getPlayers()) {
			if (player.getUserId().equals(user.getId())) {
				return player;
			}
		}
		
		throw new IllegalArgumentException("No players match opponents");
	}
	
	public static Profile findOpponent(Profile user, Game game, Profile... opponents) {
		for (Player player : game.getPlayers()) {
			if (player.getUserId().equals(user.getId())) {
				continue;
			}

			for (Profile profile : opponents) {
				if (player.getUserId().equals(profile.getId())) {
					return profile;
				}
			}
		}

		throw new IllegalArgumentException("No players match opponents");
	}
	
	public static Game loadGameFromResponse(GameResponse response) {
		return ofy().load().key(Key.create(Game.class, Long.parseLong(response.get_id()))).now();
	}
}
