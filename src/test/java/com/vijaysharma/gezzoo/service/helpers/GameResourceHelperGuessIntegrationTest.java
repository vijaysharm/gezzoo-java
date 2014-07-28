package com.vijaysharma.gezzoo.service.helpers;

import static com.vijaysharma.gezzoo.database.DatabaseService.db;
import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.findPlayer;
import static com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.loadGameFromResponse;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Guess;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.Question;
import com.vijaysharma.gezzoo.models.Winner;
import com.vijaysharma.gezzoo.models.helpers.BoardHelper;
import com.vijaysharma.gezzoo.models.helpers.GameHelper;
import com.vijaysharma.gezzoo.models.helpers.ProfileHelper;
import com.vijaysharma.gezzoo.response.GameResponse;
import com.vijaysharma.gezzoo.response.PlayerCharacterState;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.GameAssertionBuilder;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelperTestUtilities.ResponseAssertionBuilder;
import com.vijaysharma.gezzoo.utilities.IdFactory;

public class GameResourceHelperGuessIntegrationTest {
	private static final String REPLY_TEXT = "Dumb reply";
	private static final String QUESTION_TEXT = "How are ya?";
	
	private GameResourceHelper gameResourceHelper;
	private ProfileHelper profileHelper;
	private BoardHelper boardHelper;
    private LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private Profile user;
	private Board board;
	private GameResponse gameResponse;
	private List<PlayerCharacterState> playerBoard;
	private Profile opponent;

	private Question question;
	private Character character;
    
	@Before
	public void before() throws Exception {
		helper.setUp();
		profileHelper = new ProfileHelper(db());
		boardHelper = new BoardHelper(db());
		GameHelper gameHelper = new GameHelper(new BoardHelper(db()), db());
		gameResourceHelper = new GameResourceHelper(gameHelper, profileHelper, IdFactory.DEFAULT);
		
		board = new Board(10L);
		board.setName("test board name");
		board.setCharacters(Arrays.asList(
			Character.newCharacter("character name", "character category", "character img"),
			Character.newCharacter("character name 2", "character category", "character img 2")
		));
		boardHelper.create(board);
		user = profileHelper.createUserProfile();
		opponent = profileHelper.createUserProfile();
		
		playerBoard = Lists.newArrayList();
		for ( Character character : board.getCharacters() ) {
			playerBoard.add(PlayerCharacterState.from(character.getId(), true));
		}
		
		List<Profile> profiles = ofy().load().type(Profile.class).list();
		assertEquals(2, profiles.size());
		
		gameResponse = gameResourceHelper.create(user.getId());
		ResponseAssertionBuilder.check(gameResponse)
			.ended(false)
			.board(board)
			.turn(user)
			.me(playerBoard, user, null)
			.opponent(opponent);

		// Sanity Check: Test the contents of the DB
		List<Game> games = ofy().load().type(Game.class).list();
		assertEquals(1, games.size());
		
		Game game = games.get(0);
		GameAssertionBuilder.check(game)
			.board(board)
			.ended(false)
			.turn(user)
			.player(playerBoard, user, null)
			.player(playerBoard, opponent, null);
		
		character = ofy().load().key(Key.create(Character.class, board.getCharacters().get(0).getId())).now();
		
		gameResourceHelper.setCharacter(
			user.getId(), 
			gameResponse.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);
		
		gameResourceHelper.setCharacter(
			opponent.getId(), 
			gameResponse.get_id(), 
			board.getCharacters().get(0).getId().toString()
		);
		
		gameResponse = gameResourceHelper.setQuestion(
			opponent.getId(), 
			gameResponse.get_id(), 
			QUESTION_TEXT,
			playerBoard
		);
		
		question = new Question(null, QUESTION_TEXT);
		
		ResponseAssertionBuilder.check(gameResponse)
			.ended(false)
			.board(board)
			.turn(user)
			.me(playerBoard, opponent, character, question)
			.opponent(user);
		
		game = loadGameFromResponse(gameResponse);
		GameAssertionBuilder.check(game)
			.board(board)
			.ended(false)
			.turn(user)
			.player(playerBoard, user, character)
			.player(playerBoard, opponent, character, question);
		
		Player opponentPlayer = findPlayer(opponent, game);
		question = (Question) opponentPlayer.getActions().get(0);
		
		gameResponse = gameResourceHelper.postReply(
			user.getId(), 
			gameResponse.get_id(), 
			question.getId(),
			REPLY_TEXT
		);
		
		Question expected = new Question(null, QUESTION_TEXT);
		expected.setReply(REPLY_TEXT);
		
		ResponseAssertionBuilder.check(gameResponse)
			.ended(false)
			.board(board)
			.turn(opponent)
			.me(playerBoard, user, character)
			.opponent(opponent, expected);
		
		game = loadGameFromResponse(gameResponse);
		GameAssertionBuilder.check(game)
			.board(board)
			.ended(false)
			.turn(opponent)
			.player(playerBoard, user, character)
			.player(playerBoard, opponent, character, expected);		
	}
	
	@After
	public void after() {
		ofy().clear();
		helper.tearDown();
	}
	
	@Test(expected=NumberFormatException.class)
	public void guess_throws_if_character_id_cannot_be_parsed() throws Exception {
		gameResourceHelper.guess(
			opponent.getId(),
			gameResponse.get_id(), 
			"blah", 
			playerBoard
		);
	}
	
	@Test(expected=NotFoundException.class)
	public void guess_throws_if_character_is_not_found_on_board() throws Exception {
		gameResourceHelper.guess(
			opponent.getId(),
			gameResponse.get_id(), 
			"-1", 
			playerBoard
		);
	}
	
	@Test
	public void guess_saves_guesses_as_actions_and_returns_formatted_response() throws Exception {
		Character guessed = board.getCharacters().get(1);
		
		GameResponse response = gameResourceHelper.guess(
			opponent.getId(),
			gameResponse.get_id(), 
			guessed.getId().toString(), 
			playerBoard
		);
		
		Question expected = new Question(null, QUESTION_TEXT);
		expected.setReply(REPLY_TEXT);
		
		Guess guess = new Guess(null, guessed);
		
		ResponseAssertionBuilder.check(response)
			.ended(false)
			.board(board)
			.turn(user)
			.me(playerBoard, opponent, character, expected, guess)
			.opponent(user);

		Game game = loadGameFromResponse(response);
		GameAssertionBuilder.check(game)
			.board(board)
			.ended(false)
			.turn(user)
			.player(playerBoard, user, character)
			.player(playerBoard, opponent, character, expected, guess);				
	}
	
	@Test
	public void guess_saves_the_winner_when_guess_is_right() throws Exception {
		Character guessed = board.getCharacters().get(0);
		
		GameResponse response = gameResourceHelper.guess(
			opponent.getId(),
			gameResponse.get_id(), 
			guessed.getId().toString(), 
			playerBoard
		);
		
		Question expected = new Question(null, QUESTION_TEXT);
		expected.setReply(REPLY_TEXT);
		
		Guess guess = new Guess(null, guessed);
		Winner winner = new Winner(findPlayer(opponent, loadGameFromResponse(gameResponse)), guess);
		
		ResponseAssertionBuilder.check(response)
			.ended(true, winner)
			.board(board)
			.turn(user)
			.me(playerBoard, opponent, character, expected, guess)
			.opponent(user);

		Game game = loadGameFromResponse(response);
		GameAssertionBuilder.check(game)
			.board(board)
			.ended(true, winner)
			.turn(user)
			.player(playerBoard, user, character)
			.player(playerBoard, opponent, character, expected, guess);				
	}	
}
