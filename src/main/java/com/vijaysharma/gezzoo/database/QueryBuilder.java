package com.vijaysharma.gezzoo.database;

import java.util.List;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.vijaysharma.gezzoo.database.Filter.KeyFilter;

public class QueryBuilder<T> {
	private final Class<T> clazz;
	private List<Filter> filters;
	private List<KeyFilter<?>> keyFilters;
	private Integer limit;
	private Objectify service;
	
	public QueryBuilder(Class<T> clazz) {
		this(ObjectifyService.ofy(), clazz);
	}
	
	public QueryBuilder(Objectify service, Class<T> clazz) {
		this.service = service;
		this.clazz = clazz;
		this.filters = Lists.newArrayList();
		this.keyFilters = Lists.newArrayList();
	}

	public QueryBuilder<T> addFilter(Filter...filters) {
		for ( Filter filter : filters ) {
			this.filters.add(filter);
		}
		
		return this;
	}
	
	public <K> QueryBuilder<T> addKeyFilter(KeyFilter<K> filter) {
		this.keyFilters.add(filter);
		
		return this;
	}
	
	public QueryBuilder<T> limit(int limit) {
		this.limit = limit;

		return this;
	}
	
	public Query<T> build() {
		Query<T> query = service.load().type(clazz);

		for ( KeyFilter<?> filter : keyFilters ) {
			Key<T> key = getKey(filter);
			query = query.filterKey( filter.getOperator().getOperation(), key );
		}
		
		for ( Filter filter : filters ) {
			query = query.filter(
                filter.getProperty().getProperty() + " " + filter.getOperator().getOperation(), 
                filter.getValue()
             );
		}
		
		if ( limit != null ) query = query.limit(limit);
		
		return query;
	}

	private Key<T> getKey(KeyFilter<?> filter) {
		Object value = filter.getValue();
		if ( value instanceof Long ) {
			Long longId = (Long) value;
			return Key.create(clazz, longId);
		} 
		
		return Key.create(clazz, value.toString());
	}
}
