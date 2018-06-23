package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The active line is a gap buffer,
 * other lines are simple string
 *
 * @author ice1000
 * @since v0.1
 */
public class LineSpan implements TextSequence {
	private @NotNull LinkedList<CharSequence> lines;
	private @Nullable GapBuffer activeLine;
	private int activeLineNumber;
	public final char separator;

	public LineSpan(@NotNull String initial) {
		this(initial, '\n');
	}

	public LineSpan(@NotNull String initial, char separator) {
		lines = new LinkedList<>(Arrays.asList(initial.split(String.valueOf(separator))));
		this.separator = separator;
		if (!initial.isEmpty()) switchToLine(0);
	}

	@Override
	public int length() {
		if (activeLine == null) return 0;
		int ret = 0;
		int i = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			CharSequence line = iterator.next();
			ret += (i == activeLineNumber ? activeLine : line).length() + 1;
		}
		return ret;
	}

	@Override
	public char charAt(int index) {
		if (activeLine == null) throw new StringIndexOutOfBoundsException("Cannot call `charAt` on an empty string.");
		checkIndex(index);
		int currentLength = 0;
		for (int i = 0; i < lines.size(); i++) {
			CharSequence line = i == activeLineNumber ? activeLine : lines.get(i);
			int currentEnd = currentLength + line.length();
			if (index < currentEnd && index >= currentLength) return line.charAt(index - currentLength);
			if (index == currentEnd) return separator;
			currentLength = currentEnd + 1;
		}
		throw new StringIndexOutOfBoundsException("Index " + index + " is too large");
	}

	@Override
	public @NotNull String toString() {
		if (activeLine == null) return "";
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			CharSequence line = iterator.next();
			builder.append(i == activeLineNumber ? activeLine : line).append(separator);
		}
		return builder.toString();
	}

	private void switchToLine(int line) {
		if (activeLine != null) lines.set(activeLineNumber, activeLine.toString());
		activeLine = new GapBuffer(lines.get(line).toString());
		activeLineNumber = line;
	}
}
