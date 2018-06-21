package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Only one gap is used.
 *
 * @author ice1000
 * @since v0.1
 */
public class GapBuffer implements TextSequence {
	private char[] buffer;
	private int gapBegin, gapEnd;

	public GapBuffer(@NotNull char[] buffer) {
		this.buffer = buffer;
		this.gapBegin = 0;
		this.gapEnd = buffer.length;
	}

	public GapBuffer() {
		this(new char[32]);
	}

	@Override
	public int length() {
		return buffer.length - gapLength();
	}

	@Override
	public char charAt(int index) {
		checkIndex(index);
		if (index < gapBegin)
			return buffer[index];
		else return buffer[index + gapLength()];
	}

	@Contract(pure = true)
	private int gapLength() {
		return gapEnd - gapBegin;
	}

	@Override
	public void insert(int index, char c) {
		ensureLength(length() + 1);
		if (index == gapBegin) buffer[gapBegin++] = c;
		else {
			moveGap(index - gapBegin);
			assert index == gapBegin;
			buffer[gapBegin++] = c;
		}
	}

	@Override
	public void delete(int index) {
		if (index == gapBegin) gapBegin--;
		else {
			moveGap(index - gapBegin);
			assert index == gapBegin;
			gapBegin--;
		}
	}
	private void moveGap(int shift) {
		int afterBegin = gapBegin + shift;
		int afterEnd = gapEnd + shift;
		checkIndex(afterBegin);
		checkIndex(afterEnd);
		System.arraycopy(buffer, afterBegin, buffer, afterEnd, Math.abs(shift));
	}

	private void ensureLength(int length) {
		int bufferLength = buffer.length;
		if (bufferLength >= length) return;
		int newLength = Math.max(bufferLength * 3 / 2, length);
		char[] newBuffer = new char[newLength];
		System.arraycopy(buffer, 0, newBuffer, 0, gapBegin);
		int newGapEnd = newLength - length() + gapBegin;
		System.arraycopy(buffer, gapEnd, newBuffer, newGapEnd, bufferLength - gapEnd);
		gapEnd = newGapEnd;
		buffer = newBuffer;
	}

	@Override
	public @NotNull String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < gapBegin; i++) builder.append(buffer[i]);
		for (int i = gapEnd; i < buffer.length; i++) builder.append(buffer[i]);
		return builder.toString();
	}
}
