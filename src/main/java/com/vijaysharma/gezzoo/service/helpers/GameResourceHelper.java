package com.vijaysharma.gezzoo.service.helpers;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.vijaysharma.gezzoo.database.Filter.KeyFilter;
import com.vijaysharma.gezzoo.database.Filter.Operator;
import com.vijaysharma.gezzoo.database.QueryBuilder;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.helpers.GameHelper;
import com.vijaysharma.gezzoo.models.helpers.ProfileHelper;
import com.vijaysharma.gezzoo.response.GameResponse;

public class GameResourceHelper {
	private final GameHelper gameHelper;
	private final ProfileHelper profileHelper;

	public GameResourceHelper(GameHelper gameHelper, ProfileHelper profileHelper) {
		this.gameHelper = gameHelper;
		this.profileHelper = profileHelper;
	}

	public GameResponse create(String id) throws UnauthorizedException, NotFoundException {
		Profile user = profileHelper.loadUserProfile(id);
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
	
	private GameResponse create(Profile user, Profile opponent) {
		if ( user.isAdmin() )
			throw new IllegalArgumentException("Cannot create a game with given user [" + user.getId() + "]");

		if ( opponent.isAdmin() )
			throw new IllegalArgumentException("Cannot create a game with given user [" + user.getId() + "]");
		
		gameHelper.delete(user, opponent);
		Game game = gameHelper.create(user, opponent);

		return GameResponse.from(user, game);
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
		
		if ( ! game.getTurn().getId().equals(user.getId()) ) {
			throw new UnauthorizedException("Not your turn!");
		}
		
		Player current = findPlayerInGame(game, user);
		if ( current.getCharacter() != null ) {
			throw new UnauthorizedException("Character is already set");
		}
		
		Character selectedCharacter = findCharacterOnBoard(game.getBoard(), characterId);
		current.setCharacter(selectedCharacter);
		
		Player opponent = findOpponentInGame(game, user);
		if ( opponent.getCharacter() ==  null ) {
			Profile opponentProfile = fetchProfile(opponent.getUserId());
			game.setTurn(opponentProfile);
		}
		
		gameHelper.saveGame(game);
		
		return GameResponse.from(user, game);
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
