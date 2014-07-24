package com.vijaysharma.gezzoo.models;

import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Board {
	@Id private Long id;
	private String name;
	private List<Character> characters;
	
	@SuppressWarnings("unused") private Board() {}
	public Board(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Character> getCharacters() {
		return characters;
	}
	
	public void setCharacters(List<Character> characters) {
		this.characters = characters;
	}

	public Long getId() {
		return id;
	}
	@Override
	public String toString() {
		return "Board [id=" + id + ", name=" + name + ", characters="
				+ characters + "]";
	}
}
