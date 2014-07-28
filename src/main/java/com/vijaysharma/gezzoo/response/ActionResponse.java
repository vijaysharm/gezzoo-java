package com.vijaysharma.gezzoo.response;

import java.util.Date;

import com.vijaysharma.gezzoo.models.Action;
import com.vijaysharma.gezzoo.models.Guess;
import com.vijaysharma.gezzoo.models.Question;

public class ActionResponse {

	public static ActionResponse from(String userId, Action action) {
		ActionResponse response = new ActionResponse();
		response._id = action.getId();
		response.by = userId;
		response.modified = action.getModified();
		
		if (Question.class.equals(action.getClass())) {
			Question question = (Question) action;
			response.action = "question";
			response.value = question.getQuestion();
			if ( question.getReply() != null ) {
				response.reply = new ReplyResponse();
				response.reply.value = question.getReply();
			}
		} else if ( Guess.class.equals(action.getClass()) ) {
			Guess guess = (Guess) action;
			response.action = "guess";
			response.value = guess.getCharacter().getId().toString();
		}
		
		return response;
	}

	private String _id;
	private String action;
	private String value;
	private String by;
	private Date modified;
	private ReplyResponse reply;
	
	public String get_id() {
		return _id;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getValue() {
		return value;
	}
	
	public Date getModified() {
		return modified;
	}
	
	public String getBy() {
		return by;
	}
	
	public ReplyResponse getReply() {
		return reply;
	}
	
	public static class ReplyResponse {
		private String value;
		
		public String getValue() {
			return value;
		}
	}
}
