package com.vijaysharma.gezzoo.database;

public class Filter {
	public static class Property {
		private final String property;

		public Property(String property) {
			this.property = property;
		}
		
		public String getProperty() {
			return property;
		}
	}
	
	public enum Operator {
		EQUALS("="),
		NOT_EQUAL_TO("!="),
		GREATER_THAN_OR_EQUAL_TO(">="),
		LESS_THAN_OR_EQUAL_TO("<=");
		
		private final String operation;
		private Operator(String operation) {
			this.operation = operation;
		}
		
		public String getOperation() {
			return operation;
		}
	}
	
	private final Property property;
	private final Operator operator;
	private final Object value;

	public Filter(Property property, Operator operator, Object value) {
		this.property = property;
		this.operator = operator;
		this.value = value;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public Property getProperty() {
		return property;
	}
	
	public Object getValue() {
		return value;
	}
	
	public static class KeyFilter<T> {
		private final Operator operator;
		private final T value;
		
		public KeyFilter(Operator operator, T value) {
			this.operator = operator;
			this.value = value;
		}
		
		public Operator getOperator() {
			return operator;
		}
		
		public T getValue() {
			return value;
		}
	}
}
