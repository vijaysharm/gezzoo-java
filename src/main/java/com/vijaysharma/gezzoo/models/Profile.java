package com.vijaysharma.gezzoo.models;

import java.util.Set;

import com.google.common.collect.Sets;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Profile {
	@Id private String id;
	private String name;
	@Index private Set<String> roles = Sets.newHashSet();
	
	@SuppressWarnings("unused") private Profile() {}
	public Profile(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Set<String> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "Profile [id=" + id + ", name=" + name + ", roles=" + roles
				+ "]";
	}
	public boolean isAdmin() {
		return getRoles().contains("admin");
	}
}
