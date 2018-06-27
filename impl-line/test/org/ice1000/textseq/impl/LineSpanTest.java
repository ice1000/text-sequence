package org.ice1000.textseq.impl;

import org.ice1000.textseq.test.TextSequenceTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineSpanTest extends TextSequenceTest {
	public LineSpanTest() {
		super(LineSpan::new);
	}

	@Test
	@Override
	public void runAll() {
		super.runAll();
	}

	@Test
	@Override
	public void benchmark() {
		super.benchmark();
	}

	@Test
	@Override
	public void intenseBenchmark() {
		super.intenseBenchmark();
	}

	@Test
	@Override
	public void realWorldTextEditorBenchmark() {
		super.realWorldTextEditorBenchmark();
	}

	@Test
	public void simple() {
		String firstLine = "# Text Sequence\n";
		String initial = firstLine +
				"\n" +
				"[Read this paper][paper0]\n" +
				"\n" +
				"Windows|Linux\n" +
				":---:|:---:\n" +
				"[![AV][w-l]][w-i]|[![CircleCI][l-l]][l-i]\n";
		LineSpan lineSpan = new LineSpan(initial);
		assertEquals(initial, lineSpan.toString());
		assertEquals(initial.length(), lineSpan.length());
		System.out.println(
				lineSpan.charAt(16));
		for (int i = 0; i < initial.length(); i++) {
			assertEquals("Element " + i, initial.charAt(i), lineSpan.charAt(i));
		}
		// side effect
		lineSpan.delete(firstLine.length());
		assertEquals(new StringBuilder(initial).deleteCharAt(firstLine.length()).toString(), lineSpan.toString());
	}
}
