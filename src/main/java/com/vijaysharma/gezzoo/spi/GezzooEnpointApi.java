package com.vijaysharma.gezzoo.spi;

import static com.vijaysharma.gezzoo.database.DatabaseService.db;

import java.util.Collection;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.vijaysharma.gezzoo.Constants;
import com.vijaysharma.gezzoo.models.helpers.BoardHelper;
import com.vijaysharma.gezzoo.models.helpers.GameHelper;
import com.vijaysharma.gezzoo.models.helpers.ProfileHelper;
import com.vijaysharma.gezzoo.response.BoardResponse;
import com.vijaysharma.gezzoo.response.GameResponse;
import com.vijaysharma.gezzoo.response.ProfileResponse;
import com.vijaysharma.gezzoo.service.helpers.BoardResourceHelper;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelper;
import com.vijaysharma.gezzoo.service.helpers.ProfileResourceHelper;
import com.vijaysharma.gezzoo.utilities.Data;
import com.vijaysharma.gezzoo.utilities.IdFactory;

@Api(
    name = "gezzoo",
    version = "v1",
    scopes = { 
        Constants.EMAIL_SCOPE
    },
    clientIds = {
        Constants.WEB_CLIENT_ID,
        Constants.ANDROID_CLIENT_ID,
        Constants.API_EXPLORER_CLIENT_ID
    },
    audiences = {
        Constants.ANDROID_AUDIENCE
    },
    description = "Gezzoo Api"
)
public class GezzooEnpointApi {
	@ApiMethod(name = "createBoard", path = "board", httpMethod = HttpMethod.POST)
	public BoardResponse createBoard() {
		return new BoardResourceHelper(new BoardHelper(db())).create(Data.createBoard());
	}
	
	@ApiMethod(
		name = "createProfile", 
		path = "create", 
		httpMethod = HttpMethod.POST
	)
	public ProfileResponse createProfile() {
		return createProfileHelper().login(null);
	}
	
	@ApiMethod(
		name = "login", 
		path = "login", 
		httpMethod = HttpMethod.POST
	)
	public ProfileResponse login(
		@Named("id") String id
	) {
		return createProfileHelper().login(id);
	}

	@ApiMethod(
		name = "newGame", 
		path = "games", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse newGame(
		@Named("id") String id
	) throws UnauthorizedException, NotFoundException {
		return createGameHelper().create(id);
	}

	@ApiMethod(
		name = "getGames", 
		path = "games", 
		httpMethod = HttpMethod.GET
	)
	public Collection<GameResponse> getGames(
		@Named("id") String id
	) throws UnauthorizedException {
		return createGameHelper().findAll(id);
	}
	
	@ApiMethod(
		name = "getGame", 
		path = "games/{gameId}", 
		httpMethod = HttpMethod.GET
	)
	public GameResponse getGame(
		@Named("id") String id, 
		@Named("gameId") String gameId
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().findOne(id, gameId);
	}

	@ApiMethod(
		name = "setCharacter", 
		path = "games/{gameId}/character", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse setCharacter(
		@Named("id") String id, 
		@Named("gameId") String gameId, 
		@Named("character") String characterId
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().setCharacter(id, gameId, characterId);
	}
	
	@ApiMethod(
		name = "saveBoard", 
		path = "games/{gameId}/board", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse saveBoard(
		@Named("id") String id, 
		@Named("gameId") String gameId, 
		BoardForm player_board
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().saveBoard(id, gameId, player_board.getPlayerBoard());
	}

	@ApiMethod(
		name = "askQuestion", 
		path = "games/{gameId}/question", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse askQuestion(
		@Named("id") String id, 
		@Named("gameId") String gameId, 
		@Named("question") String question,
		BoardForm player_board
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().setQuestion(id, gameId, question, player_board.getPlayerBoard());
	}
	
	@ApiMethod(
		name = "postReply", 
		path = "games/{gameId}/reply", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse postReply(
		@Named("id") String id, 
		@Named("gameId") String gameId, 
		@Named("questionId") String questionId,
		@Named("reply") String reply
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().postReply(id, gameId, questionId, reply);
	}
	
	@ApiMethod(
		name = "guess", 
		path = "games/{gameId}/guess", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse guess(
		@Named("id") String id, 
		@Named("gameId") String gameId, 
		@Named("character") String characterId,
		BoardForm player_board
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().guess(id, gameId, characterId, player_board.getPlayerBoard());
	}
	
	private ProfileResourceHelper createProfileHelper() {
		return new ProfileResourceHelper(new ProfileHelper(db()));
	}

	private GameResourceHelper createGameHelper() {
		return new GameResourceHelper(
			new GameHelper(new BoardHelper(db()), db()),
			new ProfileHelper(db()),
			IdFactory.DEFAULT
		);
	}	
}
