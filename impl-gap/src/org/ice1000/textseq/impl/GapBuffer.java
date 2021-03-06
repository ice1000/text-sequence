package org.ice1000.textseq.impl;

import org.ice1000.textseq.TextSequence;
import org.ice1000.textseq.TextSequenceBase;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Only one gap is used.
 *
 * @author ice1000
 * @since v0.1
 */
public class GapBuffer extends TextSequenceBase implements TextSequence {
	private char[] buffer;
	private int gapBegin, gapEnd;

	@Override
	public @NotNull TextSequence subSequence(int start, int end) {
		checkIndex(start);
		checkIndex(end);
		if (end < start) return new GapBuffer();
		GapBuffer gapBuffer = new GapBuffer(new char[end - start + 5]);
		for (int i = start, ok = Math.min(gapBegin, end); i < ok; i++) gapBuffer.append(buffer[i]);
		for (int i = Math.max(start, gapEnd); i < end; i++) gapBuffer.append(buffer[i]);
		return gapBuffer;
	}

	private GapBuffer(@NotNull char[] buffer) {
		this.buffer = buffer;
		clear();
	}

	public GapBuffer() {
		this(new char[32]);
	}

	public GapBuffer(@NotNull String initial) {
		this.buffer = initial.toCharArray();
		this.gapBegin = initial.length();
		this.gapEnd = initial.length();
	}

	@Override
	public int length() {
		return buffer.length - gapLength();
	}

	@Override
	public void clear() {
		this.gapBegin = 0;
		this.gapEnd = buffer.length;
	}

	@Override
	public char charAt(int index) {
		checkIndex(index);
		return buffer[index < gapBegin ? index : index + gapLength()];
	}

	@Contract(pure = true)
	private int gapLength() {
		return gapEnd - gapBegin;
	}

	@Override
	public void insert(int index, char c) {
		checkIndex(index);
		ensureLength(length() + 1);
		if (index == gapBegin) buffer[gapBegin++] = c;
		else {
			moveGap(index);
			buffer[gapBegin++] = c;
		}
	}

	@Override
	public void insert(int index, @NotNull CharSequence sequence) {
		checkIndex(index);
		int length = sequence.length();
		ensureLength(length() + length);
		if (index == gapBegin) {
			for (int i = 0; i < length; i++) buffer[gapBegin + i] = sequence.charAt(i);
		} else {
			moveGap(index);
			for (int i = 0; i < length; i++) buffer[gapBegin + i] = sequence.charAt(i);
		}
		gapBegin += length;
	}

	@Override
	public void set(int index, char newValue) {
		checkIndex(index);
		buffer[index < gapBegin ? index : index + gapLength()] = newValue;
	}

	@Override
	public void delete(int index) {
		checkIndex(index);
		// Object result;
		if (index == gapBegin - 1) {
			// result = buffer[gapBegin--];
			gapBegin--;
		} else if (index == gapBegin) {
			// result = buffer[gapBegin];
			gapEnd++;
		} else {
			moveGap(index);
			// result = buffer[gapBegin];
			gapEnd++;
		}
	}

//	@Override
//	public void delete(int begin, int end) {
//		checkIndex(begin);
//		checkIndex(end);
//		int actualEnd = end + gapLength();
//		if (begin == gapBegin) {
//			this.gapEnd = actualEnd;
//		} else if (actualEnd == this.gapEnd) {
//			this.gapBegin = begin;
//		} else {
//			moveGap(begin - gapBegin);
//			assert begin == gapBegin;
//			if (gapBegin == 0) {
//				this.gapEnd += end;
//			} else {
//				// gapBegin == 0 == begin
//				gapBegin = end;
//			}
//		}
//	}

	private void moveGap(int afterBegin) {
		if (afterBegin == gapBegin) return;
		int afterEnd = gapEnd + afterBegin - gapBegin;
		if (afterBegin > gapBegin) System.arraycopy(buffer, gapEnd, buffer, gapBegin, afterBegin - gapBegin);
		else System.arraycopy(buffer, afterBegin, buffer, afterEnd, gapBegin - afterBegin);
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
