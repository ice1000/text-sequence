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
		insert(index, String.valueOf(c));
	}

	default void delete(int index) {
		delete(index, index + 1);
	}

	default void insert(int index, @NotNull CharSequence sequence) {
		for (int i = 0; i < sequence.length(); i++) insert(index + i, sequence.charAt(i));
	}

	default void delete(int begin, int end) {
		for (int i = begin; i < end; i++) delete(begin);
	}

	default void append(char c) {
		insert(length(), c);
	}

	default void append(@NotNull CharSequence sequence) {
		insert(length(), sequence);
	}
}
