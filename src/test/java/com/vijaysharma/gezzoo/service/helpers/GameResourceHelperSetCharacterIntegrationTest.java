package com.vijaysharma.gezzoo.service.helpers;

import static com.vijaysharma.gezzoo.database.DatabaseService.db;
import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.findOpponent;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.findPlayer;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.helpers.BoardHelper;
import com.vijaysharma.gezzoo.models.helpers.GameHelper;
import com.vijaysharma.gezzoo.models.helpers.ProfileHelper;
import com.vijaysharma.gezzoo.response.GameResponse;
import com.vijaysharma.gezzoo.response.GameResponse.GameState;
import com.vijaysharma.gezzoo.response.PlayerCharacterState;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.GameAssertionBuilder;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.ResponseAssertionBuilder;
import com.vijaysharma.gezzoo.utilities.IdFactory;

public class GameResourceHelperSetCharacterIntegrationTest {
	private GameResourceHelper gameResourceHelper;
	private ProfileHelper profileHelper;
	private BoardHelper boardHelper;
    private LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private Profile user1;
	private Profile user2;
	private Profile user3;
	private Board board;
	private GameResponse response1;
	private GameResponse response2;
	private List<PlayerCharacterState> playerBoard;
	
	@Before
	public void before() throws Exception {
		helper.setUp();
		profileHelper = new ProfileHelper(db());
		boardHelper = new BoardHelper(db());
		GameHelper gameHelper = new GameHelper(new BoardHelper(db()), db());
		gameResourceHelper = new GameResourceHelper(gameHelper, profileHelper, IdFactory.DEFAULT);
		
		board = new Board(10L);
		board.setName("test board name");
		board.setCharacters(Arrays.asList(Character.newCharacter("character name", "character category", "character img")));
		boardHelper.create(board);
		user1 = profileHelper.createUserProfile();
		user2 = profileHelper.createUserProfile();
		user3 = profileHelper.createUserProfile();
		
		playerBoard = Lists.newArrayList(
			PlayerCharacterState.from(board.getCharacters().get(0).getId(), true)
		);
	
		List<Profile> profiles = ofy().load().type(Profile.class).list();
		assertEquals(3, profiles.size());
		
		response1 = gameResourceHelper.create(user1.getId());
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		checkResponse(board, user1, playerBoard, opponent, user1, response1);
		
		response2 = gameResourceHelper.create(user1.getId());
		opponent = response2.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		checkResponse(board, user1, playerBoard, opponent, user1, response2);
		
		// Sanity Check: Test the contents of the DB
		List<Game> games = ofy().load().type(Game.class).list();
		assertEquals(2, games.size());
		
		Game game = games.get(0);
		opponent = findOpponent(user1, game, user2, user3);
		checkGame(board, user1, playerBoard, opponent, playerBoard, game);

		game = games.get(1);
		opponent = findOpponent(user1, game, user2, user3);
		checkGame(board, user1, playerBoard, opponent, playerBoard, game);
	}
	
	@After
	public void after() {
		ofy().clear();
		helper.tearDown();
	}

	@Test(expected=UnauthorizedException.class)
	public void setCharacter_throws_if_its_not_the_users_turn() throws Exception {
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		gameResourceHelper.setCharacter(opponent.getId(), response1.get_id(), board.getCharacters().get(0).getId().toString());
	}
	
	@Test(expected=NumberFormatException.class)
	public void setCharacter_throws_if_character_id_is_not_long() throws Exception {
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		gameResourceHelper.setCharacter(opponent.getId(), response1.get_id(), "blah");
	}
	
	@Test(expected=UnauthorizedException.class)
	public void setCharacter_throws_if_character_js_already_set() throws Exception {
		Game game = loadGameFromResponse(response1);
		Character character = ofy().load().key(Key.create(Character.class, board.getCharacters().get(0).getId())).now();
		Player player = findPlayer(user1, game);
		player.setCharacter(character);
		ofy().save().entity(game).now();
		
		gameResourceHelper.setCharacter(
			user1.getId(), 
			response1.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);
	}
	
	@Test
	public void setCharacter_saves_selection_updates_turn_when_opponent_has_no_character() throws Exception {
		Character character = ofy().load().key(Key.create(Character.class, board.getCharacters().get(0).getId())).now();
		
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		GameResponse response = gameResourceHelper.setCharacter(
			user1.getId(), 
			response1.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);

		ResponseAssertionBuilder.check(response)
			.state(GameState.READ_ONLY)
			.board(board)
			.ended(false)
			.turn(opponent)
			.me(playerBoard, user1, character)
			.opponent(opponent);
		
		Game game = loadGameFromResponse(response);
		GameAssertionBuilder.check(game)
			.board(board)
			.ended(false)
			.turn(opponent)
			.player(playerBoard, user1, character)
			.player(playerBoard, opponent, null);
	}

	@Test
	public void setCharacter_saves_selection_but_doesnt_update_turn_when_opponent_has_character() throws Exception {
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		Character character = ofy().load().key(Key.create(Character.class, board.getCharacters().get(0).getId())).now();
		Game game = loadGameFromResponse(response1);
		Player player = findPlayer(opponent, game);
		player.setCharacter(character);
		ofy().save().entity(game).now();
		
		GameResponse response = gameResourceHelper.setCharacter(
			user1.getId(), 
			response1.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);

		ResponseAssertionBuilder.check(response)
			.state(GameState.USER_ACTION)
			.board(board)
			.ended(false)
			.turn(user1)
			.me(playerBoard, user1, character)
			.opponent(opponent);
		
		game = loadGameFromResponse(response);
		GameAssertionBuilder.check(game)
			.board(board)
			.ended(false)
			.turn(user1)
			.player(playerBoard, user1, character)
			.player(playerBoard, opponent, character);
	}
	
	private static Game loadGameFromResponse(GameResponse response) {
		return ofy().load().key(Key.create(Game.class, Long.parseLong(response.get_id()))).now();
	}
	
	public static void checkGame(Board expectedBoard, 
								 Profile expectedUser,
								 List<PlayerCharacterState> userBoard,
							     Profile expectedOpponent, 
							     List<PlayerCharacterState> opponentBoard,
							     Game game) {
		GameAssertionBuilder.check(game)
			.board(expectedBoard)
			.ended(false)
			.turn(expectedUser)
			.player(userBoard, expectedUser, null)
			.player(opponentBoard, expectedOpponent, null);
	}
	
	public static void checkResponse(Board expectedBoard,
									 Profile expectedUser, 
									 List<PlayerCharacterState> playerBoard,
									 Profile expectedOpponent,
									 Profile expectedTurn,
									 GameResponse response) {
		ResponseAssertionBuilder.check(response)
			.state(GameState.USER_CHARACTER_SELECT)
			.ended(false)
			.board(expectedBoard)
			.turn(expectedTurn)
			.me(playerBoard, expectedUser, null)
			.opponent(expectedOpponent);
	}
}
