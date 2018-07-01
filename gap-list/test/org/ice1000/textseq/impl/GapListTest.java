package org.ice1000.textseq.impl;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
			list.add(String.valueOf(i));
	}

	@Override
	protected void tearDown() throws Exception {
		System.out.println(list);
		super.tearDown();
	}

	// All tests have method names beginning with "test":
	public void testInsert() {
		assertEquals(SIZE, list.size());
		list.add(1, "Insert");
		assertEquals(SIZE + 1, list.size());
		assertEquals("Insert", list.get(1));
	}

	public void testReplace() {
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
		compare(list, IntStream.range(0, SIZE).mapToObj(String::valueOf).toArray(String[]::new));
	}

	public void testRemove() {
		assertEquals(SIZE, list.size());
		list.remove(1);
		assertEquals(SIZE - 1, list.size());
		compare(list,
				Stream.concat(Stream.of("0"), IntStream.range(2, SIZE).mapToObj(String::valueOf)).toArray(String[]::new));
	}

	public void testToArray() {
		assertArrayEquals(list.toArray(),
				IntStream.range(0, SIZE).mapToObj(String::valueOf).toArray(String[]::new));
	}

	public void testClear() {
		list.clear();
		assertEquals(0, list.size());
	}

	public void testClone() {
		assertEquals(list, list.clone());
	}

	public void testIndexOf() {
		list.add("Hey");
		assertEquals(SIZE, list.indexOf("Hey"));
		list.add("Hey"); // second
		assertEquals(SIZE, list.indexOf("Hey"));
		assertEquals(0, list.indexOf("0"));
		assertEquals(2, list.indexOf("2"));
		list.add(0, "BoyNextDoor");
		assertEquals(SIZE + 1, list.indexOf("Hey"));
		assertEquals(-1, list.indexOf("TakeItBoy"));
	}

	public void testForEach() {
		AtomicInteger index = new AtomicInteger();
		list.forEach(s -> assertEquals(String.valueOf(index.getAndIncrement()), s));
	}

	public void testContains() {
		assertTrue(list.contains("15"));
		list.add(5, "233");
		assertTrue(list.contains("15"));
		assertFalse(list.contains("114514"));
	}

	public void testLastIndexOf() {
		list.add("Hey");
		assertEquals(SIZE, list.lastIndexOf("Hey"));
		list.add("Hey"); // second
		assertEquals(SIZE + 1, list.lastIndexOf("Hey"));
		assertEquals(0, list.indexOf("0"));
		assertEquals(2, list.indexOf("2"));
		list.add(0, "BoyNextDoor");
		assertEquals(SIZE + 2, list.lastIndexOf("Hey"));
		assertEquals(-1, list.lastIndexOf("TakeItBoy"));
	}

	public void testAddAll() {
		List<String> c = Arrays.asList("An", "African", "Swallow");
		list.addAll(c);
		assertEquals(SIZE + c.size(), list.size());
		compare(list,
				Stream.concat(IntStream.range(0, SIZE).mapToObj(String::valueOf),
						Stream.of(c.toArray())).toArray(String[]::new));
		list.addAll(0, c);
		assertEquals(SIZE + (c.size() << 1), list.size());
		compare(list,
				Stream.concat(Stream.of(c.toArray()), Stream.concat(IntStream.range(0, SIZE).mapToObj(String::valueOf),
						Stream.of(c.toArray()))).toArray(String[]::new));
	}
}
