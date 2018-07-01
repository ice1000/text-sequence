package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * @author ice1000
 * @since v0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface ListTextSequence extends List<Character>, TextSequence {
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
	default void set(int index, char newValue) {
		set(index, (Character) newValue);
	}
}
