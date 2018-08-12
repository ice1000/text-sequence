package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author ice1000
 * @since v0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface ListTextSequence extends List<Character>, TextSequence {
	@Override
	default @NotNull TextSequence subSequence(int start, int end) {
		TextSequence characters = new ArrayListTextSequence(length());
		for (int i = start; i < end; i++) characters.append(charAt(i));
		return characters;
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

	default void checkIndex(int index) throws StringIndexOutOfBoundsException {
		if (index < 0) throw new StringIndexOutOfBoundsException("Negative number " + index);
		int length = length();
		if (index > length)
			throw new StringIndexOutOfBoundsException("Index " + index + ", length " + length);
	}

	@Override
	default int length() {
		return size();
	}

	@Override
	@Contract(pure = true)
	default char charAt(int index) {
		return get(index);
	}

	@Override
	default void insert(int index, char c) {
		add(index, c);
	}

	@Override
	default void delete(int index) {
		remove(index);
	}

	@Override
	void clear();

	@Override
	default void set(int index, char newValue) {
		set(index, (Character) newValue);
	}
}
