package org.ice1000.textseq;

import org.ice1000.textseq.impl.ArrayListTextSequence;
import org.jetbrains.annotations.NotNull;

/**
 * Provides default implementation of {@link TextSequence}
 *
 * @author ice1000
 * @see org.ice1000.textseq.TextSequence
 * @since v0.3
 */
public abstract class TextSequenceBase implements TextSequence {
	@Override
	public @NotNull TextSequence subSequence(int start, int end) {
		TextSequence characters = new ArrayListTextSequence(length());
		for (int i = start; i < end; i++) characters.append(charAt(i));
		return characters;
	}

	@Override
	public void insert(int index, char c) {
		checkIndex(index);
		insert(index, String.valueOf(c));
	}

	@Override
	public void delete(int index) {
		checkIndex(index);
		delete(index, index + 1);
	}

	@Override
	public void insert(int index, @NotNull CharSequence sequence) {
		checkIndex(index);
		for (int i = 0; i < sequence.length(); i++) insert(index + i, sequence.charAt(i));
	}

	@Override
	public void delete(int begin, int end) {
		checkIndex(begin);
		checkIndex(end);
		for (int i = begin; i < end; i++) delete(begin);
	}

	@Override
	public void append(char c) {
		insert(length(), c);
	}

	@Override
	public void clear() {
		delete(0, length());
	}

	@Override
	public void append(@NotNull CharSequence sequence) {
		insert(length(), sequence);
	}

	@Override
	public void set(int index, char newValue) {
		delete(index);
		insert(index, newValue);
	}

	@Override
	public void checkIndex(int index) throws StringIndexOutOfBoundsException {
		if (index < 0) throw new StringIndexOutOfBoundsException("Negative number " + index);
		int length = length();
		if (index > length)
			throw new StringIndexOutOfBoundsException("Index " + index + ", length " + length);
	}

	@Override
	public @NotNull String toString() {
		return super.toString();
	}
}
