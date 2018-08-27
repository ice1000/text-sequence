package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.ice1000.textseq.TextSequenceBase;
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
public class LineSpan extends TextSequenceBase implements TextSequence {
	private @NotNull LinkedList<CharSequence> lines;
	private @Nullable GapBuffer currentLine;
	private int currentLineNum, currentLineStart, currentLineEnd;
	public final char separator;
	private int length;

	public LineSpan() {
		this("");
	}

	public LineSpan(@NotNull String initial) {
		this(initial, '\n');
	}

	public LineSpan(@NotNull String initial, char separator) {
		lines = new LinkedList<>(Arrays.asList(initial.split(String.valueOf(separator), -1)));
		this.separator = separator;
		this.currentLineStart = 0;
		this.currentLineEnd = 0;
		this.length = initial.length();
		if (!initial.isEmpty()) switchToLine(0);
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public void insert(int index, char c) {
		checkIndex(index);
		length++;
		if (currentLine == null || isIndexNotInCurrentLine(index)) {
			switchToLineOfIndex(index);
			assert currentLine != null;
		}
		int indexInCurrentLine = index - currentLineStart;
		if (c == separator) {
			int newLineNumber = currentLineNum + 1;
			TextSequence first = currentLine.subSequence(0, indexInCurrentLine);
			TextSequence second = currentLine.subSequence(indexInCurrentLine, currentLine.length());
			currentLine = null;
			lines.set(currentLineNum, first);
			lines.add(newLineNumber, second);
			switchToLine(newLineNumber);
		} else {
			try {
				currentLine.insert(indexInCurrentLine, c);
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentLineEnd++;
		}
	}

	@Override
	public void delete(int index) {
		checkIndex(index);
		length--;
		if (currentLine == null || isIndexNotInCurrentLine(index)) {
			switchToLineOfIndex(index);
			assert currentLine != null;
		}
		if (index == currentLineEnd) {
			int newLineNumber = currentLineNum + 1;
			CharSequence sequence = lines.get(newLineNumber);
			lines.remove(newLineNumber);
			currentLine.append(sequence);
		} else {
			currentLine.delete(index - currentLineStart);
		}
	}

	@Override
	public char charAt(int index) {
		if (currentLine == null) throw new StringIndexOutOfBoundsException("Cannot call `charAt` on an empty string.");
		checkIndex(index);
		int currentLength = 0;
		int i = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			CharSequence next = iterator.next();
			CharSequence line = i == currentLineNum ? currentLine : next;
			int currentEnd = currentLength + line.length();
			if (index < currentEnd && index >= currentLength) return line.charAt(index - currentLength);
			if (index == currentEnd) return separator;
			currentLength = currentEnd + 1;
		}
		throw new StringIndexOutOfBoundsException("Index " + index + " is too large");
	}

	@Override
	public @NotNull String toString() {
		if (currentLine == null) return "";
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			if (i != 0) builder.append(separator);
			CharSequence line = iterator.next();
			builder.append(i == currentLineNum ? currentLine : line);
		}
		return builder.toString();
	}

	@Contract(pure = true)
	private boolean isIndexNotInCurrentLine(int index) {
		return index < currentLineStart || index > currentLineEnd;
	}

	public int lineCount() {
		return lines.size();
	}

	public int lineSize(int index) {
		return lineAt(index).length();
	}

	@Override
	public void clear() {
		lines.clear();
		this.currentLineStart = 0;
		this.currentLineEnd = 0;
		this.length = 0;
		switchToLine(0);
	}

	public @NotNull CharSequence lineAt(int index) {
		if (currentLine != null && index == currentLineNum) return currentLine;
		return lines.get(index);
	}

	private void switchToLine(int line) {
		if (currentLine != null) lines.set(currentLineNum, currentLine.toString());
		int i = 0;
		int start = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			CharSequence sequence = iterator.next();
			int iteratorEnd = start + sequence.length();
			if (i == line) {
				currentLine = new GapBuffer(sequence.toString());
				currentLineNum = line;
				currentLineStart = start;
				currentLineEnd = iteratorEnd;
				break; // == return;
			}
			start = iteratorEnd + 1;
		}
	}

	private void switchToLineOfIndex(int index) {
		if (currentLine != null) lines.set(currentLineNum, currentLine.toString());
		int start = 0;
		int i = 0;
		for (Iterator<CharSequence> iterator = lines.iterator(); iterator.hasNext(); i++) {
			CharSequence sequence = iterator.next();
			int iteratorLineEnd = start + sequence.length();
			if (index <= iteratorLineEnd) {
				currentLine = new GapBuffer(sequence.toString());
				currentLineNum = i;
				currentLineStart = start;
				currentLineEnd = iteratorLineEnd;
				break; // == return;
			}
			start = iteratorLineEnd + 1;
		}
	}
}
