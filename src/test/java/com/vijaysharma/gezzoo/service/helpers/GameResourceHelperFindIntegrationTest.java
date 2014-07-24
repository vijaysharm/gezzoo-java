package com.vijaysharma.gezzoo.service.helpers;

import static com.vijaysharma.gezzoo.database.DatabaseService.db;
import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.checkGame;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.checkResponse;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.findOpponent;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

public class GameResourceHelperFindIntegrationTest {
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
    
	@Before
	public void before() throws Exception {
		helper.setUp();
		profileHelper = new ProfileHelper(db());
		boardHelper = new BoardHelper(db());
		GameHelper gameHelper = new GameHelper(new BoardHelper(db()), db());
		gameResourceHelper = new GameResourceHelper(gameHelper, profileHelper);
		
		board = new Board(10L);
		board.setName("test board name");
		board.setCharacters(Arrays.asList(Character.newCharacter("character name", "character category", "character img")));
		boardHelper.create(board);
		user1 = profileHelper.createUserProfile();
		user2 = profileHelper.createUserProfile();
		user3 = profileHelper.createUserProfile();
		List<Profile> profiles = ofy().load().type(Profile.class).list();
		assertEquals(3, profiles.size());
		
		response1 = gameResourceHelper.create(user1.getId());
		Profile opponent = response1.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		checkResponse(board, user1, opponent, user1, response1);
		
		response2 = gameResourceHelper.create(user1.getId());
		opponent = response2.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		checkResponse(board, user1, opponent, user1, response2);
		
		// Sanity Check: Test the contents of the DB
		List<Game> games = ofy().load().type(Game.class).list();
		assertEquals(2, games.size());
		
		Game game = games.get(0);
		opponent = findOpponent(user1, game, user2, user3);
		checkGame(board, user1, opponent, game);

		game = games.get(1);
		opponent = findOpponent(user1, game, user2, user3);
		checkGame(board, user1, opponent, game);
	}
	
	@After
	public void after() {
		ofy().clear();
		helper.tearDown();
	}
	
	@Test
	public void findAll_returns_all_games_for_a_given_user() throws Exception {
		// Test the actual query method
		List<GameResponse> gameResponses = Lists.newArrayList(gameResourceHelper.findAll(user1.getId()));
		assertEquals(2, gameResponses.size());

		GameResponse response = gameResponses.get(0);
		Profile opponent = response.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		checkResponse(board, user1, opponent, user1, response);
		
		response = gameResponses.get(1);
		opponent = response.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		checkResponse(board, user1, opponent, user1, response);
	}

	@Test
	public void findOne_returns_a_formatted_response_given_a_user_and_game_id() throws Exception {
		// Test the actual query method
		GameResponse response = gameResourceHelper.findOne(user1.getId(), response1.get_id());
		Profile opponent = response.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		checkResponse(board, user1, opponent, user1, response);
		
		response = gameResourceHelper.findOne(user1.getId(), response2.get_id());
		opponent = response.getOpponent().get_id().equals(user2.getId()) ? user2 : user3;
		checkResponse(board, user1, opponent, user1, response);
	}
}
