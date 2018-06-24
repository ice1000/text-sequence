package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.Contract;
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
@SuppressWarnings("WeakerAccess")
public class LineSpan implements TextSequence {
	private @NotNull LinkedList<CharSequence> lines;
	private @Nullable GapBuffer activeLine;
	private int activeLineNumber, currentLineStart, currentLineEnd;
	public final char separator;

	public LineSpan() {
		this("");
	}

	public LineSpan(@NotNull String initial) {
		this(initial, '\n');
	}

	public LineSpan(@NotNull String initial, char separator) {
		lines = new LinkedList<>(Arrays.asList(initial.split(String.valueOf(separator))));
		this.separator = separator;
		this.currentLineStart = 0;
		this.currentLineEnd = 0;
		if (!initial.isEmpty()) switchToLine(0);
	}

	@Override
	public int length() {
		if (activeLine == null) return 0;
		int ret = 0;
		int i = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			CharSequence line = iterator.next();
			ret += (i == activeLineNumber ? activeLine : line).length();
		}
		return ret + i;
	}

	@Override
	public void insert(int index, char c) {
		checkIndex(index);
		if (activeLine == null || !isIndexInCurrentLine(index)) {
			switchToLineOfIndex(index);
			assert activeLine != null;
		}
		int indexInCurrentLine = index - currentLineStart;
		try {
			activeLine.insert(indexInCurrentLine, c);
			currentLineEnd++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(int index) {
		checkIndex(index);
		if (activeLine == null || !isIndexInCurrentLine(index)) {
			switchToLineOfIndex(index);
			assert activeLine != null;
		}
		int indexInCurrentLine = index - currentLineStart;
		activeLine.delete(indexInCurrentLine);
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
			if (i != 0) builder.append(separator);
			CharSequence line = iterator.next();
			builder.append(i == activeLineNumber ? activeLine : line);
		}
		return builder.append(separator).toString();
	}

	@Contract(pure = true)
	private boolean isIndexInCurrentLine(int index) {
		return index >= currentLineStart && index <= currentLineEnd;
	}

	private void switchToLine(int line) {
		if (activeLine != null) lines.set(activeLineNumber, activeLine.toString());
		int i = 0;
		int start = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			CharSequence sequence = iterator.next();
			if (i == line) {
				activeLine = new GapBuffer(sequence.toString());
				activeLineNumber = line;
				currentLineStart = start;
				currentLineEnd = start + sequence.length();
			}
			start += sequence.length() + 1;
		}
	}

	private void switchToLineOfIndex(int index) {
		if (activeLine != null) lines.set(activeLineNumber, activeLine.toString());
		int i = 0;
		int start = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			CharSequence sequence = iterator.next();
			if (index >= start) {
				activeLine = new GapBuffer(sequence.toString());
				activeLineNumber = i;
				currentLineStart = start;
				currentLineEnd = start + sequence.length();
			}
			start += sequence.length() + 1;
		}
	}
}
