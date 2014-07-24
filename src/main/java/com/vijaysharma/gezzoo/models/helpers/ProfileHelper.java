package com.vijaysharma.gezzoo.models.helpers;

import java.util.List;

import com.google.common.collect.Sets;
import com.vijaysharma.gezzoo.database.Action;
import com.vijaysharma.gezzoo.database.DatabaseService;
import com.vijaysharma.gezzoo.database.QueryBuilder;
import com.vijaysharma.gezzoo.models.Counters;
import com.vijaysharma.gezzoo.models.Profile;
import com.vijaysharma.gezzoo.utilities.IdFactory;

public class ProfileHelper {	
	private final DatabaseService service;
	private final IdFactory idFactory;

	public ProfileHelper(DatabaseService databaseService) {
		this(databaseService, IdFactory.DEFAULT);
	}
	
	ProfileHelper(DatabaseService service, IdFactory idFactory) {
		this.service = service;
		this.idFactory = idFactory;
	}

	public Profile loadUserProfile(String id) {
		return service.load(Profile.class, id);
	}

	public Profile createUserProfile() {
		Long countId = service.transaction(new Action<Long>() {
			@Override
			public Long go() {
				Counters counters = service.load(Counters.class, Counters.COUNTERS_ID);
				if ( counters == null )
					counters = new Counters();

				long next = counters.next();
				service.save(counters);
				
				return next;
			}
		});
		
		String name = createId(countId);
		String userid = idFactory.newId(); 

		return createUserProfile(name, userid);
	}

	private Profile createUserProfile(String name, String userid) {
		Profile p = new Profile(userid);
		p.setName(name);
		p.setRoles(Sets.newHashSet("user"));
		
		service.save(p);
		
		return p;
	}

	private String createId(Long userId) {
		return "gezzo_" + userId;
	}

	public QueryBuilder<Profile> newQuery() {
		return service.newQuery(Profile.class);
	}

	public List<Profile> query(QueryBuilder<Profile> query) {
		return service.find(query);
	}
}
