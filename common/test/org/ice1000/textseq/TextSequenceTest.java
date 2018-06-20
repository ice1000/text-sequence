package org.ice1000.textseq;

import org.ice1000.textseq.impl.ArrayListTextSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextSequenceTest {
	@Test
	public void main() {
		TextSequence characters = new ArrayListTextSequence();
		String s = "reiuji utsuho";
		for (int i = 0; i < s.length(); i++) characters.append(s.charAt(i));
		characters.subSequence(3, 5);
		assertEquals(s.substring(3, 7), characters.subSequence(3, 7).toString());
	}

	@Test
	public void basic() {
		TextSequence characters = new ArrayListTextSequence();
		characters.append('a');
		characters.append('b');
		characters.append('c');
		assertEquals("abc", characters.toString());
		assertEquals('a', characters.charAt(0));
	}

	@Test
	public void insert() {
		TextSequence characters = new ArrayListTextSequence();
		characters.append("abc");
		assertEquals("abc", characters.toString());
		assertEquals('a', characters.charAt(0));
	}
}
