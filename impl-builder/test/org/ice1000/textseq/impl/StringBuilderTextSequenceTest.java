package org.ice1000.textseq.impl;

import org.ice1000.textseq.test.TextSequenceTest;
import org.junit.Test;

public class StringBuilderTextSequenceTest extends TextSequenceTest {
	public StringBuilderTextSequenceTest() {
		super(StringBuilderTextSequence::new);
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
}
