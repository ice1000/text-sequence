package org.ice1000.textseq.test;

import org.ice1000.textseq.impl.LinkedListTextSequence;
import org.junit.Test;

public class TrivialLinkedListTest extends TextSequenceTest {
	public TrivialLinkedListTest() {
		super(LinkedListTextSequence::new);
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
