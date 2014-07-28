package com.vijaysharma.gezzoo.service.helpers;

import static com.googlecode.objectify.ObjectifyService.factory;
import static com.vijaysharma.gezzoo.service.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Subclass;

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
		public B(String b) { b_property = b; }
	}
	
	@Entity
	public static class A {
		@Id private Long id;
		@Index private ArrayList<B> b_objects = new ArrayList<DatastoreTest.B>();
		@Index private B b_item;
		@Index private String a_property; 
	}
	
	@Ignore
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
	
	@Entity
	public class Animal {
	    @Id Long id;
	    String name;
	}
	        
	@Subclass(index=true)
	public class Mammal extends Animal {
	    boolean longHair;
	}
	        
	@Subclass
	public class Cat extends Mammal {
	    boolean hypoallergenic;
	}

	@Entity
	public static class Base {
		private @Id String id;
		private String base_data = "base-data";
		
		private Base() {};
		Base(String id){ this.id = id; }
	}

	@Subclass
	public static class Extended extends Base {
		private String ex_data = "extended-data";
		private Extended(){};
		private Extended(String id){ super(id); }
	}
	
	@Entity
	public static class TestClass {
		@Id Long id;
		List<Base> items = new ArrayList<DatastoreTest.Base>();
	}
	
	@Test
	public void test_inheritance() {
		factory().register(Animal.class);
		factory().register(Mammal.class);
		factory().register(Cat.class);
		
		Animal annie = new Animal();
		annie.name = "Annie";
		ofy().save().entity(annie).now();

		Mammal mam = new Mammal();
		mam.name = "Mam";
		mam.longHair = true;
		ofy().save().entity(mam).now();

		Cat nyan = new Cat();
		nyan.name = "Nyan";
		nyan.longHair = true;
		nyan.hypoallergenic = true;
		ofy().save().entity(nyan).now();

		// This will return the Cat
		Animal fetched = ofy().load().type(Animal.class).id(nyan.id).now();
		assertEquals(Cat.class, fetched.getClass());
		
		// This query will produce three objects, the Animal, Mammal, and Cat
		List<Animal> all = ofy().load().type(Animal.class).list();
		assertEquals(3, all.size());
		
		// This query will produce the Mammal and Cat
		List<Mammal> mammals = ofy().load().type(Mammal.class).list();
		assertEquals(2, mammals.size());
		
		assertEquals(Animal.class, all.get(0).getClass());
		assertEquals(Mammal.class, all.get(1).getClass());
		assertEquals(Cat.class, all.get(2).getClass());
		
		factory().register(TestClass.class);
		factory().register(Extended.class);
		
		TestClass obj = new TestClass();
		obj.items.add(new Base("dsfkdjsfds"));
		obj.items.add(new Extended("dhgewvfrrfds"));
		Key<TestClass> key = ofy().save().entity(obj).now();
		
		TestClass now = ofy().load().key(key).now();
		assertEquals(2, now.items.size());
	}
}
