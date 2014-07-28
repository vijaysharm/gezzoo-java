package com.vijaysharma.gezzoo.service;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;
import com.vijaysharma.gezzoo.models.Counters;
import com.vijaysharma.gezzoo.models.Game;
import com.vijaysharma.gezzoo.models.Guess;
import com.vijaysharma.gezzoo.models.Player;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.models.Question;

/**
 * Custom Objectify Service that this application should use.
 */
public class ObjectifyService {
	static { 
		factory().register(Board.class);
		factory().register(Character.class);
		factory().register(Counters.class);
		factory().register(Game.class);
		factory().register(Player.class);
		factory().register(Profile.class);
		factory().register(Question.class);
		factory().register(Guess.class);
	}
    /**
     * Use this static method for getting the Objectify service object in order to make sure the
     * above static block is executed before using Objectify.
     * @return Objectify service object.
     */
    public static Objectify ofy() {
        return com.googlecode.objectify.ObjectifyService.ofy();
    }

    /**
     * Use this static method for getting the Objectify service factory.
     * @return ObjectifyFactory.
     */
    public static ObjectifyFactory factory() {
        return com.googlecode.objectify.ObjectifyService.factory();
    }
}
