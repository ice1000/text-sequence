package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
public class StringBuilderTextSequence implements TextSequence {
	private @NotNull StringBuilder impl;

	public StringBuilderTextSequence(@NotNull StringBuilder impl) {
		this.impl = impl;
	}

	public StringBuilderTextSequence() {
		this(new StringBuilder());
	}

	public StringBuilderTextSequence(int capacity) {
		this(new StringBuilder(capacity));
	}

	@Override
	public int length() {
		return impl.length();
	}

	@Override
	public char charAt(int index) {
		return impl.charAt(index);
	}

	@Override
	public void insert(int index, char c) {
		checkIndex(index);
		impl.insert(index, c);
	}

	@Override
	public void insert(int index, @NotNull CharSequence sequence) {
		checkIndex(index);
		impl.insert(index, sequence.toString());
	}

	@Override
	public void delete(int index) {
		checkIndex(index);
		impl.deleteCharAt(index);
	}

	@Override
	public void delete(int begin, int end) {
		checkIndex(begin);
		checkIndex(end);
		impl.delete(begin, end);
	}

	@Override
	public void set(int index, char newValue) {
		impl.setCharAt(index, newValue);
	}

	@Override
	public @NotNull String toString() {
		return impl.toString();
	}

	@Override
	public @NotNull TextSequence subSequence(int start, int end) {
		return new StringBuilderTextSequence(new StringBuilder(impl.substring(start, end)));
	}
}
