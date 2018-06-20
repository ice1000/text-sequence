package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class LinkedListTextSequence extends LinkedList<Character> implements ListTextSequence {
	public LinkedListTextSequence() {
	}

	public LinkedListTextSequence(@NotNull List<@NotNull Character> characters) {
		super(characters);
	}

	@Override
	public @NotNull TextSequence subSequence(int start, int end) {
		return new LinkedListTextSequence(subList(start, end));
	}

	@Override
	public @NotNull String toString() {
		return String.join("", this);
	}
}
