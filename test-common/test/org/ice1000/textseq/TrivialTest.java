package org.ice1000.textseq;

import org.ice1000.textseq.impl.ArrayListTextSequence;
import org.junit.Test;

public class TrivialTest extends TextSequenceTest {
	public TrivialTest() {
		super(ArrayListTextSequence::new);
	}

	@Test
	@Override
	public void runAll() {
		super.runAll();
	}
}
