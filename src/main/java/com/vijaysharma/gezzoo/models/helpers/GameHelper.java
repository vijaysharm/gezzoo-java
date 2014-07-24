package com.vijaysharma.gezzoo.models.helpers;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.vijaysharma.gezzoo.database.DatabaseService;
import com.vijaysharma.gezzoo.database.Filter;
import com.vijaysharma.gezzoo.database.Filter.KeyFilter;
import com.vijaysharma.gezzoo.database.Filter.Operator;
import com.vijaysharma.gezzoo.database.Filter.Property;
import com.vijaysharma.gezzoo.database.QueryBuilder;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Game.GameBuilder;
import com.vijaysharma.gezzoo.models.Profile;

public class GameHelper {
	private final DatabaseService service;
	private final BoardHelper boardHelper;

	public GameHelper(BoardHelper boardHelper, DatabaseService service) {
		this.boardHelper = boardHelper;
		this.service = service;
	}

	public void delete(Profile user, Profile opponent) {
		QueryBuilder<Game> query = service.newQuery(Game.class);
		query.addFilter(
			new Filter(new Property("players.userId"), Operator.EQUALS, user.getId()),
			new Filter(new Property("players.userId"), Operator.EQUALS, opponent.getId())
		);
		
		Collection<Long> ids = Collections2.transform(service.find(query), new Function<Game, Long>() {
			@Override
			public Long apply(Game input) {
				return input.getId();
			}
		});
		
		service.delete(Game.class, ids);
	}
	
	public Game create(Profile player, Profile opponent) {
		Board board = boardHelper.findOne(boardHelper.newQuery().limit(1));
		
		GameBuilder builder = new GameBuilder(service)
			.board(board)
			.addPlayer(player, boardHelper.playerBoard(board))
			.addPlayer(opponent, boardHelper.playerBoard(board))
			.turn(player)
			.ended(false);
			
		Game game = builder.build();
		saveGame(game);

		return game;
	}
	
	public Collection<Game> findByUser(Profile user) {
		QueryBuilder<Game> query = service.newQuery(Game.class)
			.addFilter(
				new Filter(new Property("players.userId"), Operator.EQUALS, user.getId())
			);
		
		return service.find(query);
	}
	
	public Game findByUserAndGameId(Profile user, String gameId) {
		QueryBuilder<Game> query = service.newQuery(Game.class)
			.addFilter(				
				new Filter(new Property("players.userId"), Operator.EQUALS, user.getId())
			).addKeyFilter(
				new KeyFilter<Long>(Operator.EQUALS, Long.parseLong(gameId))
			);
		
		return service.findOne(query);
	}

	public void saveGame(Game game) {
		service.save(game);
	}
}
