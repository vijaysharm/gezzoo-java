package com.vijaysharma.gezzoo.models;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class Guess extends Action {
	@Load private Ref<Character> character;

	@SuppressWarnings("unused") private Guess() {};
	public Guess(String newId, Character guessedCharacter) {
		super(newId);
		character = Ref.create(guessedCharacter);
	}

	public Character getCharacter() {
		return character.get();
	}
	@Override
	public String toString() {
		return "Guess [getId()=" + getId() + ", getCharacter()=" + getCharacter() + "]";
	}
}
