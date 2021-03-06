package org.ice1000.textseq.test;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("WeakerAccess")
@TestOnly
public class TextSequenceTest {
	@TestOnly
	public TextSequenceTest(@NotNull Supplier<@NotNull TextSequence> sequenceSupplier) {
		this.sequenceSupplier = sequenceSupplier;
	}

	private @NotNull Supplier<@NotNull TextSequence> sequenceSupplier;

	@TestOnly
	public void runAll() {
		test0();
		test1();
		test2();
		test3();
		test4();
		test5();
	}

	@TestOnly
	public void test0() {
		TextSequence characters = sequenceSupplier.get();
		String s = "reiuji utsuho";
		for (int i = 0; i < s.length(); i++) characters.append(s.charAt(i));
		characters.subSequence(3, 5);
		assertEquals(s.substring(3, 7), characters.subSequence(3, 7).toString());
	}

	@TestOnly
	public void test1() {
		TextSequence characters = sequenceSupplier.get();
		characters.append('a');
		characters.append('b');
		characters.append('c');
		assertEquals("abc", characters.toString());
		assertEquals('a', characters.charAt(0));
	}

	@TestOnly
	public void test2() {
		TextSequence characters = sequenceSupplier.get();
		characters.append("abc");
		assertEquals("abc", characters.toString());
		assertEquals('a', characters.charAt(0));
	}

	@TestOnly
	public void test3() {
		TextSequence characters = sequenceSupplier.get();
		characters.append("abc");
		characters.delete(0);
		assertEquals("bc", characters.toString());
		characters.delete(0);
		assertEquals('c', characters.charAt(0));
	}

	@TestOnly
	public void test4() {
		TextSequence characters = sequenceSupplier.get();
		characters.append("abc");
		characters.delete(2);
		assertEquals("ab", characters.toString());
		characters.delete(1);
		assertEquals('a', characters.charAt(0));
	}

	@TestOnly
	public void test5() {
		TextSequence characters = sequenceSupplier.get();
		characters.append("abc");
		characters.set(2, 'd');
		assertEquals("abd", characters.toString());
		characters.set(1, 'a');
		assertEquals('a', characters.charAt(1));
	}

	@TestOnly
	public void benchmark() {
		benchmark(3000);
	}

	@TestOnly
	public void intenseBenchmark() {
		benchmark(15000);
	}

	@TestOnly
	public void realWorldTextEditorBenchmark() {
		realWorldTextEditorBenchmark(15000);
	}

	@TestOnly
	public void realWorldTextEditorBenchmark(int loopCount) {
		TextSequence sequence = sequenceSupplier.get();
		sequence.insert(0, "init");
		Random random = new Random();
		long start = System.currentTimeMillis();
		String text =
				"This is just a test string, which is intended to be very long, in order to make the test more " +
						"intense, since double are truncated when they're converted into strings.";
		for (int i = 0, index = 0; i < loopCount; i++, index += text.length()) {
			sequence.insert(index, text);
			if (random.nextInt(100) < 2) index = 0;
		}
		System.out.println("Insertion: " + (System.currentTimeMillis() - start) + ", final length: " + sequence.length());
		start = System.currentTimeMillis();
		int length = sequence.length();
		for (int i = 0, index = random.nextInt(length); i < loopCount; i++) {
			int size = random.nextInt(Math.min(length / loopCount, sequence.length() - index));
			sequence.delete(index, index + size);
			while (index > sequence.length()) index = random.nextInt(sequence.length());
		}
		System.out.println("Deletion: " + (System.currentTimeMillis() - start));
	}

	@TestOnly
	public void benchmark(int loopCount) {
		TextSequence sequence = sequenceSupplier.get();
		sequence.insert(0, "init");
		Random random = new Random();
		long start = System.currentTimeMillis();
		for (int i = 0; i < loopCount; i++)
			sequence.insert(random.nextInt(sequence.length()), String.valueOf(random.nextDouble()));
		System.out.println("Insertion: " + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		int length = sequence.length();
		for (int i = 0; i < loopCount; i++) {
			int begin = random.nextInt(sequence.length());
			int size = random.nextInt(length / loopCount);
			if (begin + size >= sequence.length()) begin = 0;
			sequence.delete(begin, begin + size);
		}
		System.out.println("Deletion: " + (System.currentTimeMillis() - start));
	}
}
