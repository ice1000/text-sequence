package org.ice1000.textseq;

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
	@NotNull TextSequence subSequence(int start, int end);
	void insert(int index, char c);
	void delete(int index);
	void insert(int index, @NotNull CharSequence sequence);
	void delete(int begin, int end);
	void append(char c);
	void clear();
	void append(@NotNull CharSequence sequence);
	void set(int index, char newValue);
	void checkIndex(int index) throws StringIndexOutOfBoundsException;
}
