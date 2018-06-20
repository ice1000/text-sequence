package org.ice1000.textseq;

import org.ice1000.textseq.impl.ArrayListTextSequence;
import org.jetbrains.annotations.NotNull;

/**
 * Mutable version of {@link CharSequence}
 *
 * @author ice1000
 * @since v0.1
 */
public interface TextSequence extends CharSequence {
	@Override
	default @NotNull TextSequence subSequence(int start, int end) {
		TextSequence characters = new ArrayListTextSequence(length());
		for (int i = start; i < end; i++) characters.append(charAt(i));
		return characters;
	}

	void insert(int index, char c);
	void delete(int index);

	default void append(char c) {
		insert(length(), c);
	}
}
