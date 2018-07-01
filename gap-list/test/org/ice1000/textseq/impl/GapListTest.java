package org.ice1000.textseq.impl;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;

public class GapListTest extends TestCase {
	public static final int SIZE = 100;
	private GapList<String> list = new GapList<>();

	// You can use the constructor instead of setUp():
	public GapListTest(String name) {
		super(name);
		for (int i = 0; i < 100; i++)
			list.add("" + i);
	}

	// All tests have method names beginning with "test":
	public void testInsert() {
		System.out.println("Running testInsert()");
		assertEquals(SIZE, list.size());
		list.add(1, "Insert");
		assertEquals(SIZE + 1, list.size());
		assertEquals("Insert", list.get(1));
	}

	public void testReplace() {
		System.out.println("Running testReplace()");
		assertEquals(SIZE, list.size());
		list.set(1, "Replace");
		assertEquals(100, list.size());
		assertEquals("Replace", list.get(1));
	}

	// A "helper" method to reduce code duplication. As long
	// as the name doesn't start with "test," it will not
	// be automatically executed by JUnit.
	private void compare(GapList<String> lst, String... strs) {
		Object[] array = lst.toArray();
		assertEquals("Arrays not the same length", array.length, strs.length);
		for (int i = 0; i < array.length; i++)
			assertEquals((String) array[i], strs[i]);
	}

	public void testOrder() {
		System.out.println("Running testOrder()");
		compare(list, IntStream.range(0, SIZE).mapToObj(String::valueOf).toArray(String[]::new));
	}

	public void testRemove() {
		System.out.println("Running testRemove()");
		assertEquals(SIZE, list.size());
		list.remove(1);
		assertEquals(SIZE - 1, list.size());
		compare(list,
				Stream.concat(Stream.of("0"), IntStream.range(2, SIZE).mapToObj(String::valueOf)).toArray(String[]::new));
	}

	public void testToArray() {
		System.out.println("Running testToArray()");
		assertArrayEquals(list.toArray(),
				IntStream.range(0, SIZE).mapToObj(String::valueOf).toArray(String[]::new));
	}

	public void testIndexOf() {
		System.out.println("Running testIndexOf()");
		list.add("Hey");
		assertEquals(SIZE, list.indexOf("Hey"));
		list.add("Hey"); // second
		assertEquals(SIZE, list.indexOf("Hey"));
		assertEquals(0, list.indexOf("0"));
		assertEquals(2, list.indexOf("2"));
	}

	public void testLastIndexOf() {
		System.out.println("Running testLastIndexOf()");
		list.add("Hey");
		assertEquals(SIZE, list.lastIndexOf("Hey"));
		list.add("Hey"); // second
		assertEquals(SIZE + 1, list.lastIndexOf("Hey"));
		assertEquals(0, list.indexOf("0"));
		assertEquals(2, list.indexOf("2"));
	}

	public void testAddAll() {
		System.out.println("Running testAddAll()");
		List<String> c = Arrays.asList("An", "African", "Swallow");
		list.addAll(c);
		assertEquals(SIZE + c.size(), list.size());
		compare(list,
				Stream.concat(IntStream.range(0, SIZE).mapToObj(String::valueOf),
						Stream.of(c.toArray())).toArray(String[]::new));
	}
}
