package com.vijaysharma.gezzoo.models;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Character {
	@Id private Long id;
	private String name;
	private String img;
	private Set<String> category = new HashSet<String>();
	
	public Long getId() {
		return id;
	}
	
	public String getImage() {
		return img;
	}
	
	public String getName() {
		return name;
	}
	
	
	@Override
	public String toString() {
		return "Character [id=" + id + ", name=" + name + ", img=" + img
				+ ", category=" + category + "]";
	}

	public static Character newCharacter(String name, String category, String img) {
		Character c = new Character();
		c.category.addAll(Sets.newHashSet(category));
		c.name = name;
		c.img = img;
		
		return c;
	}
}
