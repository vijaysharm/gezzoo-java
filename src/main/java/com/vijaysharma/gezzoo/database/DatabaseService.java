package com.vijaysharma.gezzoo.database;

import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;

import java.util.Collection;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Work;


public class DatabaseService {
	public static DatabaseService db() {
		return new DatabaseService(ofy());
	}

	private final Objectify service;
	public DatabaseService(Objectify service) {
		this.service = service;
	}
	
	public <T> QueryBuilder<T> newQuery(Class<T> clazz) {
		return new QueryBuilder<T>(service, clazz);
	}
	
	public <T> void save(T object) {
		service.save().entity(object).now();
	}

	public <T> T load(Class<T> clazz, String id) {
		return service.load().key(Key.create(clazz, id)).now();
	}
	
	public <T> T load(Class<T> clazz, Long id) {
		return service.load().key(Key.create(clazz, id)).now();
	}

	public <T> List<T> find(QueryBuilder<T> query) {
		return query.build().list();
	}
	
	public <T> T findOne(QueryBuilder<T> query) {
		return query.build().first().now();
	}
	
	public <T> void delete(Class<T> clazz, Collection<Long> ids) {
		service.delete().type(clazz).ids(ids).now();	
	}
	
	public <T> T transaction(final Transaction<T> action) {
		return service.transact(new Work<T>() {
			@Override
			public T run() {
				return action.go();
			}
		});
	}
}
