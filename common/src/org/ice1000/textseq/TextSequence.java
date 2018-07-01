package org.ice1000.textseq;

import org.ice1000.textseq.impl.ArrayListTextSequence;
import org.jetbrains.annotations.NotNull;

/**
 * Mutable version of {@link CharSequence}
 *
 * @author ice1000
 * @implNote must impl one of {@link TextSequence#delete(int)} or {@link TextSequence#delete(int, int)} and
 * one of {@link TextSequence#insert(int, char)} or {@link TextSequence#insert(int, CharSequence)}
 * @since v0.1
 */
public interface TextSequence extends CharSequence {
	@Override
	default @NotNull TextSequence subSequence(int start, int end) {
		TextSequence characters = new ArrayListTextSequence(length());
		for (int i = start; i < end; i++) characters.append(charAt(i));
		return characters;
	}

	default void insert(int index, char c) {
		checkIndex(index);
		insert(index, String.valueOf(c));
	}

	default void delete(int index) {
		checkIndex(index);
		delete(index, index + 1);
	}

	default void insert(int index, @NotNull CharSequence sequence) {
		checkIndex(index);
		for (int i = 0; i < sequence.length(); i++) insert(index + i, sequence.charAt(i));
	}

	default void delete(int begin, int end) {
		checkIndex(begin);
		checkIndex(end);
		for (int i = begin; i < end; i++) delete(begin);
	}

	default void append(char c) {
		insert(length(), c);
	}

	default void append(@NotNull CharSequence sequence) {
		insert(length(), sequence);
	}

	default void set(int index, char newValue) {
		delete(index);
		insert(index, newValue);
	}

	default void checkIndex(int index) throws StringIndexOutOfBoundsException {
		if (index < 0) throw new StringIndexOutOfBoundsException("Negative number " + index);
		int length = length();
		if (index > length)
			throw new StringIndexOutOfBoundsException("Index " + index + ", length " + length);
	}
}
