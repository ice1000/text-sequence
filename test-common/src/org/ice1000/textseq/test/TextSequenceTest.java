package org.ice1000.textseq.test;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("WeakerAccess")
public class TextSequenceTest {
	public TextSequenceTest(@NotNull Supplier<@NotNull TextSequence> sequenceSupplier) {
		this.sequenceSupplier = sequenceSupplier;
	}

	private @NotNull Supplier<@NotNull TextSequence> sequenceSupplier;

	public void runAll() {
		test0();
		test1();
		test2();
		test3();
	}

	public void test0() {
		TextSequence characters = sequenceSupplier.get();
		String s = "reiuji utsuho";
		for (int i = 0; i < s.length(); i++) characters.append(s.charAt(i));
		characters.subSequence(3, 5);
		assertEquals(s.substring(3, 7), characters.subSequence(3, 7).toString());
	}

	public void test1() {
		TextSequence characters = sequenceSupplier.get();
		characters.append('a');
		characters.append('b');
		characters.append('c');
		assertEquals("abc", characters.toString());
		assertEquals('a', characters.charAt(0));
	}

	public void test2() {
		TextSequence characters = sequenceSupplier.get();
		characters.append("abc");
		assertEquals("abc", characters.toString());
		assertEquals('a', characters.charAt(0));
	}

	public void test3() {
		TextSequence characters = sequenceSupplier.get();
		characters.append("abc");
		characters.delete(0);
		assertEquals("bc", characters.toString());
		characters.delete(0);
		assertEquals('c', characters.charAt(0));
	}
}
