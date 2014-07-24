package com.vijaysharma.gezzoo.models.helpers;

import java.util.HashMap;
import java.util.Map;

import com.vijaysharma.gezzoo.database.DatabaseService;
import com.vijaysharma.gezzoo.database.QueryBuilder;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;

public class BoardHelper {
	private final DatabaseService service;

	public BoardHelper(DatabaseService service) {
		this.service = service;
	}

	public Board create(Board board) {
		Board found = service.load(Board.class, board.getId());
		if ( found != null )
			return found;
		
		for ( Character character : board.getCharacters() ) {
			service.save(character);
		}
		
		service.save(board);
		
		return board;
	}

	public QueryBuilder<Board> newQuery() {
		return service.newQuery(Board.class);
	}

	public Board findOne(QueryBuilder<Board> query) {
		return service.findOne(query);
	}

	public Map<Long, Boolean> playerBoard(Board board) {
		Map<Long, Boolean> b = new HashMap<Long, Boolean>();
		for ( Character character : board.getCharacters() ) {
			b.put(character.getId(), Boolean.TRUE);
		}
		
		return b;
	}
}
