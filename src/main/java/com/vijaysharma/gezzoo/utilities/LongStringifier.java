package com.vijaysharma.gezzoo.utilities;

import com.googlecode.objectify.stringifier.Stringifier;

public class LongStringifier implements Stringifier<Long>
{
	@Override
	public String toString(Long obj) {
		return obj.toString();
	}

	@Override
	public Long fromString(String str) {
		return Long.parseLong(str);
	}
}
