package com.vijaysharma.gezzoo.models;

import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class Question extends Action {
	private String question;
	private String reply;
	
	@SuppressWarnings("unused") private Question() {}
	public Question(String id, String question) { super(id); this.question = question; }
	
	public String getQuestion() {
		return question;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	
	public String getReply() {
		return reply;
	}

	@Override
	public String toString() {
		return "Question [question=" + question + ", reply=" + reply
				+ ", getId()=" + getId() + "]";
	}
}

