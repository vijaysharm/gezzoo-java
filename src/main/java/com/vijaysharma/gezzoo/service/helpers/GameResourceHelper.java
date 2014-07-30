package com.vijaysharma.gezzoo.service.helpers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.vijaysharma.gezzoo.database.Filter.KeyFilter;
import com.vijaysharma.gezzoo.database.Filter.Operator;
import com.vijaysharma.gezzoo.database.QueryBuilder;
import com.vijaysharma.gezzoo.models.Action;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Guess;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.Question;
import com.vijaysharma.gezzoo.models.Winner;
import com.vijaysharma.gezzoo.models.helpers.GameHelper;
import com.vijaysharma.gezzoo.models.helpers.ProfileHelper;
import com.vijaysharma.gezzoo.response.GameResponse;
import com.vijaysharma.gezzoo.response.PlayerCharacterState;
import com.vijaysharma.gezzoo.utilities.IdFactory;

public class GameResourceHelper {
	private final GameHelper gameHelper;
	private final ProfileHelper profileHelper;
	private final IdFactory factory;

	public GameResourceHelper(GameHelper gameHelper, 
							  ProfileHelper profileHelper,
							  IdFactory factory) {
		this.gameHelper = gameHelper;
		this.profileHelper = profileHelper;
		this.factory = factory;
	}

	public GameResponse create(String userId) throws UnauthorizedException, NotFoundException {
		Profile user = profileHelper.loadUserProfile(userId);
		if ( user == null )
			throw new UnauthorizedException("Authorization required");
		
		Collection<Game> games = gameHelper.findByUser(user);
		QueryBuilder<Profile> query = profileHelper.newQuery();
		if ( games.isEmpty() ) {
			query.addKeyFilter(new KeyFilter<String>(Operator.NOT_EQUAL_TO, user.getId()));
		} else {
			for ( Game game : games ) { 
				for ( Player player : game.getPlayers() ) {
					query.addKeyFilter(new KeyFilter<String>(Operator.NOT_EQUAL_TO, player.getUserId()));
				}
			}
		}

		List<Profile> list = profileHelper.query(query.limit(20));
		if ( list.isEmpty() ) {
			throw new NotFoundException("aww, there isnt anyone else you can play with :(");
		}
			
		Profile opponent = list.get(random(0, list.size() - 1));
		return create(user, opponent);
	}
	
	public GameResponse create(String userId, String opponentId) throws UnauthorizedException, NotFoundException {
		Profile user = fetchProfile(userId);
		
		Profile opponent = profileHelper.loadUserProfile(opponentId);
		if ( opponent == null )
			throw new NotFoundException("User [" + opponentId + "] not found");
		
		return create(user, opponent);
	}
	
	public Collection<GameResponse> findAll(String userId) throws UnauthorizedException {
		final Profile user = fetchProfile(userId);
		
		Collection<Game> games = gameHelper.findByUser(user);
		return Collections2.transform(games, new Function<Game, GameResponse>() {
			@Override
			public GameResponse apply(Game game) {
				return GameResponse.from(user, game);
			}
		});
	}

	public GameResponse findOne(String userId, String gameId) throws UnauthorizedException, NotFoundException {
		Profile user = fetchProfile(userId);
		Game game = fetchGame(user, gameId);
		
		return GameResponse.from(user, game);
	}
	
	public GameResponse setCharacter(String userId, String gameId, String cId) throws UnauthorizedException, NotFoundException {
		Long characterId = Long.parseLong(cId);
		Profile user = fetchProfile(userId);
		Game game = fetchGame(user, gameId);
		
		checkGame(game);
		checkTurn(user, game);
		
		Player current = findPlayerInGame(game, user);
		if ( current.getCharacter() != null ) {
			throw new UnauthorizedException("Character is already set");
		}
		
		Character selectedCharacter = findCharacterOnBoard(game.getBoard(), characterId);
		current.setCharacter(selectedCharacter);
		
		Player opponent = findOpponentInGame(game, user);
		if ( opponent.getCharacter() ==  null ) {
			updateTurn(game, opponent);
		}
		
		gameHelper.saveGame(game);
		
		return GameResponse.from(user, game);
	}

	// TODO: Should check if the opponent has any un-responded to questions
	public GameResponse setQuestion(
		String userId, 
		String gameId, 
		String question, 
		List<PlayerCharacterState> playerBoard
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		Profile user = fetchProfile(userId);
		Game game = fetchGame(user, gameId);
		
		checkGame(game);
		checkTurn(user, game);
		checkBoard(game.getBoard(), playerBoard);
		
		Player current = findPlayerInGame(game, user);
		Player opponent = findOpponentInGame(game, user);
		
		checkCharacterSet(current);
		checkCharacterSet(opponent);
		
		if (Strings.isNullOrEmpty(question))
			throw new BadRequestException("Question cannot be null or empty");
		
		current.addAction(new Question(factory.newId(), question));
		updatePlayerBoard(current, playerBoard);
		updateTurn(game, opponent);
		
		gameHelper.saveGame(game);
		
		return GameResponse.from(user, game);
	}

	public GameResponse postReply(
		String userId, 
		String gameId, 
		String questionId, 
		String reply
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		Profile user = fetchProfile(userId);
		Game game = fetchGame(user, gameId);
		
		checkGame(game);
		checkTurn(user, game);
		
		Player current = findPlayerInGame(game, user);
		Player opponent = findOpponentInGame(game, user);
		
		checkCharacterSet(current);
		checkCharacterSet(opponent);
		
		if (Strings.isNullOrEmpty(reply))
			throw new BadRequestException("Reply cannot be null or empty");
		
		Question question = findQuestion(opponent, questionId);
		if (! Strings.isNullOrEmpty(question.getReply()))
			throw new BadRequestException("Reply already given to [" + questionId + "]");
		
		question.setReply(reply);
		
		gameHelper.saveGame(game);
		
		return GameResponse.from(user, game);
	}

	public GameResponse guess(
		String userId, 
		String gameId, 
		String characterId, 
		List<PlayerCharacterState> playerBoard
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		Long cId = Long.parseLong(characterId);
		Profile user = fetchProfile(userId);
		Game game = fetchGame(user, gameId);
		
		checkGame(game);
		checkTurn(user, game);
		checkBoard(game.getBoard(), playerBoard);
		
		Player current = findPlayerInGame(game, user);
		Player opponent = findOpponentInGame(game, user);

		checkCharacterSet(current);
		checkCharacterSet(opponent);
		
		Character guessedCharacter = findCharacterOnBoard(game.getBoard(), cId);
		boolean ended = opponent.getCharacter().getId().equals(guessedCharacter.getId());
		Guess guess = new Guess(factory.newId(), guessedCharacter);
		
		game.setEnded(ended);
		current.addAction(guess);
		updateTurn(game, opponent);
		updatePlayerBoard(current, playerBoard);
		if ( ended ) game.setWinner(new Winner(current, guess)); 
		
		gameHelper.saveGame(game);
		
		return GameResponse.from(user, game);
	}

	public GameResponse saveBoard(
		String userId, 
		String gameId, 
		List<PlayerCharacterState> playerBoard
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		Profile user = fetchProfile(userId);
		Game game = fetchGame(user, gameId);
		
		checkGame(game);
		checkBoard(game.getBoard(), playerBoard);
		
		Player current = findPlayerInGame(game, user);
		Player opponent = findOpponentInGame(game, user);

		checkCharacterSet(current);
		checkCharacterSet(opponent);
		
		updatePlayerBoard(current, playerBoard);
		
		gameHelper.saveGame(game);
		
		return GameResponse.from(user, game);
	}
	
	private void checkGame(Game game) throws UnauthorizedException {
		if ( game.isEnded() )
			throw new UnauthorizedException("Game [" + game.getId() + "] has ended. It can't be modified.");
	}

	private void updateTurn(Game game, Player opponent) throws UnauthorizedException {
		Profile opponentProfile = fetchProfile(opponent.getUserId());
		game.setTurn(opponentProfile);
	}
	
	// TODO: Why not allow for users to just send up only the modified board characters
	private void updatePlayerBoard(Player player, List<PlayerCharacterState> playerBoard) {
		if ( playerBoard == null )
			return;

		Map<Long, Boolean> board = player.getBoard();
		for(PlayerCharacterState state : playerBoard ) {
			if ( board.containsKey(state.getId()) )
				board.put(state.getId(), state.getUp());
		}
	}
	
	private void checkCharacterSet(Player player) throws UnauthorizedException {
		if ( player.getCharacter() == null )
			throw new UnauthorizedException("Character is not yet set");
	}
	
	private void checkTurn(Profile user, Game game) throws UnauthorizedException {
		if ( ! game.getTurn().getId().equals(user.getId()) ) {
			throw new UnauthorizedException("Not your turn!");
		}
	}
	
	// TODO: Why not allow for users to just send up only the modified board characters
	private void checkBoard(Board board, List<PlayerCharacterState> playerBoard) throws BadRequestException {
		if ( playerBoard == null || playerBoard.isEmpty() ) 
			return;

		List<Character> characters = board.getCharacters();
		
		if ( playerBoard.size() != characters.size())
			throw new BadRequestException("Unexpected player board");

		Map<Long, PlayerCharacterState> map = Maps.newHashMap();
		for ( PlayerCharacterState state : playerBoard ) {
			if ( map.containsKey(state.getId()) )
				throw new BadRequestException("Unexpected player board");
			
			map.put(state.getId(), state);
		}
		
		for ( Character character : characters ) {
			map.remove(character.getId());
		}
		
		if ( ! map.isEmpty() )
			throw new BadRequestException("Unexpected player board");
	}
	
	private GameResponse create(Profile user, Profile opponent) {
		if ( user.isAdmin() )
			throw new IllegalArgumentException("Cannot create a game with given user [" + user.getId() + "]");

		if ( opponent.isAdmin() )
			throw new IllegalArgumentException("Cannot create a game with given user [" + user.getId() + "]");
		
		gameHelper.delete(user, opponent);
		Game game = gameHelper.create(user, opponent);

		return GameResponse.from(user, game);
	}
	
	private Question findQuestion(Player opponent, String questionId) throws BadRequestException {
		for ( Action action : opponent.getActions() ) {
			if ( action.getId().equals(questionId) && action instanceof Question ) {
				return (Question) action;
			}
		}
		
		throw new BadRequestException("Question [" + questionId + "] does not exist");
	}
	
	private Character findCharacterOnBoard(Board board, Long characterId) throws NotFoundException {
		for ( Character character : board.getCharacters() ) {
			if ( character.getId().equals(characterId) ) {
				return character;
			}
		}
		
		throw new NotFoundException("Character [" + characterId + "] Not found in board [" + board.getId() + "]");
	}

	private Player findPlayerInGame(Game game, Profile user) {
		for ( Player player : game.getPlayers() ) {
			if (user.getId().equals(player.getUserId())) {
				return player;
			}
		}
		
		throw new IllegalStateException("User [" + user.getId() + "] is not a part of Game [" + game.getId() + "]" );
	}
	
	private Player findOpponentInGame(Game game, Profile user) {
		for ( Player player : game.getPlayers() ) {
			if (! user.getId().equals(player.getUserId())) {
				return player;
			}
		}
		
		throw new IllegalStateException("User [" + user.getId() + "] is not a part of Game [" + game.getId() + "]" );
	}

	private Profile fetchProfile(String userId) throws UnauthorizedException {
		Profile user = profileHelper.loadUserProfile(userId);
		if ( user == null )
			throw new UnauthorizedException("Authorization required");
		
		return user;
	}
	
	private Game fetchGame(Profile user, String gameId) throws NotFoundException {
		Game game = gameHelper.findByUserAndGameId(user, gameId);
		if ( game == null )
			throw new NotFoundException("Game [" + gameId + "] Not found for [" + user.getId() + "]");
		
		return game;
	}

	private static int random(int min, int max) {
		return new Random().nextInt(max - min + 1) + min; 
	}
}
