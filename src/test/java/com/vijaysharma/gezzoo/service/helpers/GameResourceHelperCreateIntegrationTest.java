package com.vijaysharma.gezzoo.service.helpers;

import static com.vijaysharma.gezzoo.database.DatabaseService.db;
import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.checkGame;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.helpers.BoardHelper;
import com.vijaysharma.gezzoo.models.helpers.GameHelper;
import com.vijaysharma.gezzoo.models.helpers.ProfileHelper;
import com.vijaysharma.gezzoo.response.GameResponse;
import com.vijaysharma.gezzoo.response.GameResponse.GameState;
import com.vijaysharma.gezzoo.response.PlayerCharacterState;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.ResponseAssertionBuilder;
import com.vijaysharma.gezzoo.utilities.IdFactory;

public class GameResourceHelperCreateIntegrationTest {
	private GameResourceHelper gameResourceHelper;
	private ProfileHelper profileHelper;
	private BoardHelper boardHelper;
	
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
	@Before
	public void before() {
		helper.setUp();
		profileHelper = new ProfileHelper(db());
		boardHelper = new BoardHelper(db());
		GameHelper gameHelper = new GameHelper(new BoardHelper(db()), db());
		gameResourceHelper = new GameResourceHelper(gameHelper, profileHelper, IdFactory.DEFAULT);
	}
	
	@After
	public void after() {
		ofy().clear();
		helper.tearDown();
	}
	
	@Test(expected=UnauthorizedException.class)
	public void create_throws_when_Profile_not_found() throws UnauthorizedException, NotFoundException {
		gameResourceHelper.create("blah");
	}
	
	@Test(expected=NotFoundException.class)
	public void create_throws_when_no_opponents_found() throws UnauthorizedException, NotFoundException {
		Profile profile = profileHelper.createUserProfile();
		gameResourceHelper.create(profile.getId());
	}
	
	@Test
	public void create_stores_game_in_db_and_returns_formatted_response() throws UnauthorizedException, NotFoundException {
		Board board = new Board(10L);
		board.setName("test board name");
		board.setCharacters(Arrays.asList(Character.newCharacter("character name", "character category", "character img")));
		boardHelper.create(board);
		
		List<PlayerCharacterState> playerBoard = Lists.newArrayList(
			PlayerCharacterState.from(board.getCharacters().get(0).getId(), true)
		);
		
		Profile opponent = profileHelper.createUserProfile();
		Profile user = profileHelper.createUserProfile();
		GameResponse response = gameResourceHelper.create(user.getId());
		List<Game> games = ofy().load().type(Game.class).list();
		
		assertEquals(1, games.size());
		ResponseAssertionBuilder.check(response)
			.state(GameState.USER_CHARACTER_SELECT)
			.ended(false)
			.board(board)
			.turn(user)
			.me(playerBoard, user, null)
			.opponent(opponent);
		checkGame(board, user, opponent, games.get(0));
	}

	@Test
	public void create_deletes_previous_games_stores_game_in_db_and_returns_formatted_response() throws UnauthorizedException, NotFoundException {
		Board board = new Board(10L);
		board.setName("test board name");
		board.setCharacters(Arrays.asList(Character.newCharacter("character name", "character category", "character img")));
		boardHelper.create(board);
		Profile opponent = profileHelper.createUserProfile();
		Profile user = profileHelper.createUserProfile();
		
		List<PlayerCharacterState> playerBoard = Lists.newArrayList(
			PlayerCharacterState.from(board.getCharacters().get(0).getId(), true)
		);
	
		GameResponse response = gameResourceHelper.create(user.getId());
		List<Game> games = ofy().load().type(Game.class).list();
		
		assertEquals(1, games.size());
		ResponseAssertionBuilder.check(response)
			.state(GameState.USER_CHARACTER_SELECT)
			.ended(false)
			.board(board)
			.turn(user)
			.me(playerBoard, user, null)
			.opponent(opponent);
		checkGame(board, user, opponent, games.get(0));
		
		response = gameResourceHelper.create(user.getId(), opponent.getId());
		games = ofy().load().type(Game.class).list();
		
		assertEquals(1, games.size());
		ResponseAssertionBuilder.check(response)
			.state(GameState.USER_CHARACTER_SELECT)
			.ended(false)
			.board(board)
			.turn(user)
			.me(playerBoard, user, null)
			.opponent(opponent);
		
		checkGame(board, user, opponent, games.get(0));
		
		// paranoia. Checking if nothing else was deleted
		List<Board> boards = ofy().load().type(Board.class).list();
		assertEquals(1, boards.size());
		
		List<Profile> profiles = ofy().load().type(Profile.class).list();
		assertEquals(2, profiles.size());
	}
}
