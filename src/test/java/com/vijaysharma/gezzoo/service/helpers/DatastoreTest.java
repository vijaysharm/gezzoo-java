package com.vijaysharma.gezzoo.service.helpers;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

public class DatastoreTest {
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
	@Before
	public void before() {
		helper.setUp();
	}
	
	@After
	public void after() {
		ofy().clear();
		helper.tearDown();
	}

	@Entity
	public static class D {
		@Id private Long id;
		@Index private String d_data;
	}
	
	@Entity
	public static class C {
		@Id private Long id;
		
		@Index @Load private Ref<D> d;
		@Index private String c_data;
	}
	
	public static class B {
		private String b_property;
		private B() {};
		public B(String b) { b_property = b; }
	}
	
	@Entity
	public static class A {
		@Id private Long id;
		@Index private ArrayList<B> b_objects = new ArrayList<DatastoreTest.B>();
		@Index private B b_item;
		@Index private String a_property; 
	}
	
	@Test
	public void test() {
		factory().register(A.class);
//		factory().register(B.class);
		factory().register(C.class);
		factory().register(D.class);
		
		A a = new A();
		a.a_property = "something";
		a.b_objects.add(new B("b1"));
		a.b_objects.add(new B("b2"));
		a.b_item = new B("b3");
		
		ofy().save().entity(a);
		A actual = ofy()
			.load()
			.type(A.class)
//			.filter("a_property =", "something") // should pass
//			.filter("b_item.b_property =", "b2") // should fail
//			.filter("b_item.b_property =", "b3") // should pass
//			.filter("b_objects.b_property =", "b3") // should fail
			.filter("b_objects.b_property =", "b1") // should pass
			.first()
			.now();

		assertEquals(actual.a_property, a.a_property);
		assertEquals(actual.b_objects.size(), a.b_objects.size());
		assertEquals(actual.b_objects.get(0).b_property, a.b_objects.get(0).b_property);
		assertEquals(actual.b_objects.get(1).b_property, a.b_objects.get(1).b_property);
		
		D d = new D();
		d.d_data = "ds_data";
		ofy().save().entity(d).now();
		
		C c = new C();
		c.c_data = "c_data";
		c.d = Ref.create(d);
		ofy().save().entity(c).now();

		C ac = ofy()
			.load()
			.type(C.class)
			.filter("d.d_data =", "ds_data") // should pass
			.first()
			.now();
		
		assertEquals("ds_data", ac.d.get().d_data);
		assertNotNull(ac);
	}
}
