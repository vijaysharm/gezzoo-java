package com.vijaysharma.gezzoo.service.helpers;

import static com.vijaysharma.gezzoo.database.DatabaseService.db;
import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.findOpponent;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.Question;
import com.vijaysharma.gezzoo.models.helpers.BoardHelper;
import com.vijaysharma.gezzoo.models.helpers.GameHelper;
import com.vijaysharma.gezzoo.models.helpers.ProfileHelper;
import com.vijaysharma.gezzoo.response.GameResponse;
import com.vijaysharma.gezzoo.response.GameResponse.GameState;
import com.vijaysharma.gezzoo.response.PlayerCharacterState;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.GameAssertionBuilder;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.ResponseAssertionBuilder;
import com.vijaysharma.gezzoo.utilities.IdFactory;

public class GameResourceHelperAskQuestionIntegrationTest {
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
	public void setQuestion_throws_when_game_ended() throws Exception {
		Game game = loadGameFromResponse(response1);
		game.setEnded(true);
		ofy().save().entity(game).now();
		
		gameResourceHelper.setQuestion(
			user1.getId(), 
			response1.get_id(), 
			"Dumb question",
			playerBoard
		);
	}
	
	@Test(expected=BadRequestException.class)
	public void setQuestion_throws_if_player_board_doesnt_match_expected_board() throws Exception {
		playerBoard = Lists.newArrayList(
			PlayerCharacterState.from(new Long("-1"), true)
		);
		
		gameResourceHelper.setQuestion(
			user1.getId(), 
			response1.get_id(), 
			"Dumb question",
			playerBoard
		);
	}
	
	@Test(expected=UnauthorizedException.class)
	public void setQuestion_throws_if_its_not_the_users_turn() throws Exception {
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		gameResourceHelper.setQuestion(
			opponent.getId(), 
			response1.get_id(), 
			"Dumb question",
			playerBoard
		);
	}
	
	@Test(expected=UnauthorizedException.class)
	public void setQuestion_throws_when_player_has_no_character_set() throws Exception {
		gameResourceHelper.setQuestion(
			user1.getId(), 
			response1.get_id(), 
			"Dumb question",
			playerBoard
		);
	}
	
	@Test(expected=UnauthorizedException.class)
	public void setQuestion_throws_when_opponent_has_no_character_set() throws Exception {
		GameResponse response = gameResourceHelper.setCharacter(
			user1.getId(), 
			response1.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);
		
		Game game = loadGameFromResponse(response);
		game.setTurn(user1);
		ofy().save().entity(game).now();
		
		gameResourceHelper.setQuestion(
			user1.getId(), 
			response1.get_id(), 
			"Dumb question",
			playerBoard
		);
	}
	
	@Test(expected=BadRequestException.class)
	public void setQuestion_throws_question_is_empty() throws Exception {
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		gameResourceHelper.setCharacter(
			user1.getId(), 
			response1.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);
		
		gameResourceHelper.setCharacter(
			opponent.getId(), 
			response1.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);
		
		gameResourceHelper.setQuestion(
			opponent.getId(), 
			response1.get_id(), 
			"",
			playerBoard
		);
	}
	
	@Test
	public void setQuestion_saves_action_and_updated_board_then_returns_successful_response() throws Exception {
		Character character = ofy().load().key(Key.create(Character.class, board.getCharacters().get(0).getId())).now();
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		List<PlayerCharacterState> updatedBoard = Lists.newArrayList(
			PlayerCharacterState.from(board.getCharacters().get(0).getId(), false)
		);
		
		gameResourceHelper.setCharacter(
			user1.getId(), 
			response1.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);
		
		gameResourceHelper.setCharacter(
			opponent.getId(), 
			response1.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);
		
		GameResponse response = gameResourceHelper.setQuestion(
			opponent.getId(), 
			response1.get_id(), 
			"How are ya?",
			updatedBoard
		);
		
		Question question = new Question(null, "How are ya?");
		
		ResponseAssertionBuilder.check(response)
			.state(GameState.READ_ONLY)
			.ended(false)
			.board(board)
			.turn(user1)
			.me(updatedBoard, opponent, character, question)
			.opponent(user1);
		
		Game game = loadGameFromResponse(response);
		GameAssertionBuilder.check(game)
			.board(board)
			.ended(false)
			.turn(user1)
			.player(playerBoard, user1, character)
			.player(updatedBoard, opponent, character, question);
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
