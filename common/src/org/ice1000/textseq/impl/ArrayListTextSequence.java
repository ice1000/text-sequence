package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ArrayListTextSequence extends ArrayList<Character> implements ListTextSequence {
	public ArrayListTextSequence() {
	}

	public ArrayListTextSequence(int initialCapacity) {
		super(initialCapacity);
	}

	public ArrayListTextSequence(@NotNull List<@NotNull Character> characters) {
		super(characters);
	}

	@Override
	public @NotNull TextSequence subSequence(int start, int end) {
		return new ArrayListTextSequence(subList(start, end));
	}

	@Override
	public @NotNull String toString() {
		return String.join("", this);
	}
}
