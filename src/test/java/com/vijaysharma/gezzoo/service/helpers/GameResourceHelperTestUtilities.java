package com.vijaysharma.gezzoo.service.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.response.GameResponse;

public class GameResourceHelperTestUtilities {
	public static void checkGame(Board expectedBoard, 
								 Profile expectedUser,
							     Profile expectedOpponent, 
							     Game game) { 
//		assertNotNull(game.getId());
//		assertNotNull(game.getBoard());
//		assertEquals(expectedBoard.getId(), game.getBoard().getId());
//		assertEquals(1, game.getBoard().getCharacters().size());
//		assertEquals(expectedBoard.getCharacters().get(0).getName(), game.getBoard().getCharacters().get(0).getName());
//		assertEquals(expectedBoard.getCharacters().get(0).getImage(), game.getBoard().getCharacters().get(0).getImage());
//		assertEquals(expectedBoard.getCharacters().get(0).getId(), game.getBoard().getCharacters().get(0).getId());
//		assertEquals(false, game.isEnded());
//		assertNotNull(game.getTurn());
//		assertNotNull(expectedUser.getId(), game.getTurn().getId());
//		assertNotNull(expectedUser.getName(), game.getTurn().getName());
//		assertEquals(2, game.getPlayers().size());
//		assertEquals(expectedUser.getId(), game.getPlayers().get(0).getUserId());
//		assertEquals(expectedUser.getId(), game.getPlayers().get(0).getProfile().getId());
//		assertEquals(expectedUser.getName(), game.getPlayers().get(0).getProfile().getName());
//		assertNull(game.getPlayers().get(0).getCharacter());
//		assertEquals(1, game.getPlayers().get(0).getBoard().size());
//		assertEquals(true, game.getPlayers().get(0).getBoard().get(expectedBoard.getCharacters().get(0).getId()));
//		assertEquals(expectedOpponent.getId(), game.getPlayers().get(1).getUserId());
//		assertEquals(expectedOpponent.getId(), game.getPlayers().get(1).getProfile().getId());
//		assertEquals(expectedOpponent.getName(), game.getPlayers().get(1) .getProfile().getName());
//		assertNull(game.getPlayers().get(1).getCharacter());
//		assertEquals(1, game.getPlayers().get(1).getBoard().size());
//		assertEquals(true, game.getPlayers().get(1).getBoard().get(expectedBoard.getCharacters().get(0).getId()));
		
		GameAssertionBuilder.check(game)
			.board(expectedBoard)
			.ended(false)
			.turn(expectedUser)
			.player(expectedBoard, expectedUser, null)
			.player(expectedBoard, expectedOpponent, null);
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
		
		public GameAssertionBuilder ended(boolean ended) {
			assertEquals(ended, game.isEnded());
			return this;
		}
		
		public GameAssertionBuilder turn(Profile turn) {
			assertNotNull(game.getTurn());
			assertNotNull(turn.getId(), game.getTurn().getId());
			assertNotNull(turn.getName(), game.getTurn().getName());			
			return this;
		}
		
		public GameAssertionBuilder player(Board board, Profile profile, Character character) {
			Player player = findPlayerInGame(game, profile);
			Long actualCharacterId = player.getCharacter() == null ? null : player.getCharacter().getId();
			Long characterId = character == null ? null : character.getId();
			
			assertEquals(profile.getId(), player.getUserId());
			assertEquals(profile.getId(), player.getProfile().getId());
			assertEquals(profile.getName(), player.getProfile().getName());
			assertEquals(characterId, actualCharacterId);
			assertEquals(1, player.getBoard().size());
			assertEquals(true, player.getBoard().get(board.getCharacters().get(0).getId()));
			
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
//		assertNotNull(response);
//		assertNotNull(response.get_id());
//		assertEquals(false, response.getEnded());
//		assertEquals(expectedTurn.getId(), response.getTurn());
//		assertEquals(expectedBoard.getName(), response.getBoard().getName());
//		assertEquals(1, response.getBoard().getCharacters().size());
//		assertEquals(expectedUser.getId(), response.getMe().get_id());
//		assertEquals(expectedUser.getName(), response.getMe().getUsername());
//		assertEquals(1, response.getMe().getBoard().size());
//		assertEquals(true, response.getMe().getBoard().get(0).getUp());
//		assertEquals(expectedBoard.getCharacters().get(0).getId().toString(), response.getMe().getBoard().get(0).get_id());
//		assertNull(response.getMe().getCharacter());
//		assertEquals(expectedOpponent.getId(), response.getOpponent().get_id());
//		assertEquals(expectedOpponent.getName(), response.getOpponent() .getUsername());
//		assertNull(response.getOpponent().getCharacter());
//		assertNull(response.getOpponent().getBoard());
		ResponseAssertionBuilder.check(response)
			.ended(false)
			.board(expectedBoard)
			.turn(expectedTurn)
			.me(expectedBoard, expectedUser, null)
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
			assertEquals(expected, response.getEnded());
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
		
		public ResponseAssertionBuilder me(Board board, Profile me, Character character) {
			String characterId = character == null ? null : character.getId().toString();
			
			assertEquals(board.getCharacters().get(0).getId().toString(), response.getMe().getBoard().get(0).get_id());
			assertEquals(board.getCharacters().get(0).getId().toString(), response.getMe().getBoard().get(0).get_id());
			assertEquals(true, response.getMe().getBoard().get(0).getUp());
			assertEquals(me.getId(), response.getMe().get_id());
			assertEquals(me.getName(), response.getMe().getUsername());
			assertEquals(me.getName(), response.getMe().getUsername());
			assertEquals(characterId, response.getMe().getCharacter());
			
			return this;
		}
		
		public ResponseAssertionBuilder opponent(Profile opponent) {
			assertEquals(opponent.getId(), response.getOpponent().get_id());
			assertEquals(opponent.getName(), response.getOpponent() .getUsername());
			assertNull(response.getOpponent().getCharacter());
			assertNull(response.getOpponent().getBoard());
			
			return this;
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
}
