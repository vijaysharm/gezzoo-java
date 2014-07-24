package com.vijaysharma.gezzoo.service.helpers;

import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.helpers.BoardHelper;
import com.vijaysharma.gezzoo.response.BoardResponse;

public class BoardResourceHelper {
	private final BoardHelper boardHelper;

	public BoardResourceHelper(BoardHelper boardHelper) {
		this.boardHelper = boardHelper;
	}

	public BoardResponse create(Board board) {
		return BoardResponse.from(boardHelper.create(board));
	}
}
