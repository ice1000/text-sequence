package org.ice1000.textseq.test;

import org.ice1000.textseq.impl.ArrayListTextSequence;
import org.junit.Test;

public class TrivialArrayListTest extends TextSequenceTest {
	public TrivialArrayListTest() {
		super(ArrayListTextSequence::new);
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
