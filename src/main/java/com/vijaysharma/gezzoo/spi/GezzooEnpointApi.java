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
import com.vijaysharma.gezzoo.response.GameResponse;
import com.vijaysharma.gezzoo.response.ProfileResponse;
import com.vijaysharma.gezzoo.service.helpers.GameResourceHelper;
import com.vijaysharma.gezzoo.service.helpers.ProfileResourceHelper;
import com.vijaysharma.gezzoo.spi.forms.AskForm;
import com.vijaysharma.gezzoo.spi.forms.GuessForm;
import com.vijaysharma.gezzoo.spi.forms.ReplyForm;
import com.vijaysharma.gezzoo.spi.forms.SaveForm;
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
	@ApiMethod(
		name = "createBoard", 
		path = "board", 
		httpMethod = HttpMethod.POST
	)
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
		@Named("token") String id
	) {
		return createProfileHelper().login(id);
	}

	@ApiMethod(
		name = "newGame", 
		path = "games", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse newGame(
		@Named("token") String id
	) throws UnauthorizedException, NotFoundException {
		return createGameHelper().create(id);
	}

	@ApiMethod(
		name = "newGameWith", 
		path = "gamesWith", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse newGameWith(
		@Named("token") String userId,
		@Named("opponent") String opponentId
	) throws UnauthorizedException, NotFoundException {
		return createGameHelper().create(userId, opponentId);
	}

	@ApiMethod(
		name = "getGames", 
		path = "games", 
		httpMethod = HttpMethod.GET
	)
	public Collection<GameResponse> getGames(
		@Named("token") String id
	) throws UnauthorizedException {
		return createGameHelper().findAll(id);
	}
	
	@ApiMethod(
		name = "getGame", 
		path = "games/{gameId}", 
		httpMethod = HttpMethod.GET
	)
	public GameResponse getGame(
		@Named("token") String id, 
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
		@Named("token") String id, 
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
		SaveForm form
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().saveBoard(form.getToken(), form.getGameId(), form.getBoard());
	}

	@ApiMethod(
		name = "askQuestion", 
		path = "games/{gameId}/question", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse askQuestion(
		AskForm form
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().setQuestion(form.getToken(), form.getGameId(), form.getQuestion(), form.getBoard());
	}
	
	@ApiMethod(
		name = "postReply", 
		path = "games/{gameId}/reply", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse postReply(
		ReplyForm form
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().postReply(form.getToken(), form.getGameId(), form.getQuestionId(), form.getReply());
	}
	
	@ApiMethod(
		name = "guess", 
		path = "games/{gameId}/guess", 
		httpMethod = HttpMethod.POST
	)
	public GameResponse guess(
		GuessForm form
	) throws UnauthorizedException, NotFoundException, BadRequestException {
		return createGameHelper().guess(form.getToken(), form.getGameId(), form.getCharacterId(), form.getBoard());
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
