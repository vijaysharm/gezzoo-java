package com.vijaysharma.gezzoo.utilities;

import java.util.UUID;

public class IdFactory {
	public static final IdFactory DEFAULT = new IdFactory();

	private IdFactory() {}
	public String newId() {
		return UUID.randomUUID().toString();
	}
}
