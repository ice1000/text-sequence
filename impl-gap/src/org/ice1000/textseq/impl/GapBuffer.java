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

	private GapBuffer(@NotNull char[] buffer) {
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
	public void insert(int index, @NotNull CharSequence sequence) {
		int length = sequence.length();
		ensureLength(length() + length);
		if (index == gapBegin) {
			for (int i = 0; i < length; i++) buffer[gapBegin + i] = sequence.charAt(i);
		} else {
			moveGap(index - gapBegin);
			assert index == gapBegin;
			for (int i = 0; i < length; i++) buffer[gapBegin + i] = sequence.charAt(i);
		}
		gapBegin += length;
	}

	@Override
	public void delete(int index) {
		if (index == gapBegin) {
			if (gapBegin != 0) gapBegin--;
			else gapEnd++;
		} else {
			moveGap(index - gapBegin);
			assert index == gapBegin;
			if (gapBegin != 0) gapBegin--;
			else gapEnd++;
		}
	}

	private void moveGap(int shift) {
		if (shift == 0) return;
		int afterBegin = gapBegin + shift;
		int afterEnd = gapEnd + shift;
		checkInternalIndex(afterBegin);
		checkInternalIndex(afterEnd);
		if (shift > 0) System.arraycopy(buffer, gapBegin, buffer, gapEnd, shift);
		else System.arraycopy(buffer, afterBegin, buffer, afterEnd, -shift);
		gapBegin = afterBegin;
		gapEnd = afterEnd;
	}

	private void ensureLength(int length) {
		int bufferLength = buffer.length;
		if (bufferLength >= length) return;
		int newLength = Math.max(bufferLength * 2, length);
		char[] newBuffer = new char[newLength];
		System.arraycopy(buffer, 0, newBuffer, 0, gapBegin);
		int newGapEnd = newLength - length() + gapBegin;
		System.arraycopy(buffer, gapEnd, newBuffer, newGapEnd, bufferLength - gapEnd);
		gapEnd = newGapEnd;
		buffer = newBuffer;
	}

	private void checkInternalIndex(int index) {
		if (index < 0) throw new StringIndexOutOfBoundsException("Negative number " + index);
		int length = buffer.length;
		if (index > length) throw new StringIndexOutOfBoundsException("Index " + index + ", length " + length);
	}

	@Override
	public @NotNull String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < gapBegin; i++) builder.append(buffer[i]);
		for (int i = gapEnd; i < buffer.length; i++) builder.append(buffer[i]);
		return builder.toString();
	}
}
