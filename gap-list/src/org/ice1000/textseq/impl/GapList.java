package org.ice1000.textseq.impl;

import java.util.*;
import java.util.function.Consumer;

/**
 * Standalone implementation as a {@link java.util.List}.
 *
 * @author ice1000
 * @since v0.1
 */
@SuppressWarnings("WeakerAccess")
public class GapList<T> extends AbstractList<T> implements List<T>, RandomAccess {
	private Object[] buffer;
	private int gapBegin, gapEnd;

	private GapList(Object[] buffer, int gapBegin, int gapEnd) {
		this.buffer = buffer;
		this.gapBegin = gapBegin;
		this.gapEnd = gapEnd;
	}

	public GapList(int initialCapacity) {
		this(new Object[initialCapacity], 0, initialCapacity);
	}

	public GapList() {
		this(32);
	}

	@Override
	public int size() {
		return buffer.length - gapLength();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		rangeCheck(index);
		return (T) buffer[index < gapBegin ? index : index + gapLength()];
	}

	private int gapLength() {
		return gapEnd - gapBegin;
	}

	@Override
	public void add(int index, T c) {
		rangeCheckForAdd(index);
		ensureLength(size() + 1);
		if (index == gapBegin) buffer[gapBegin++] = c;
		else {
			moveGap(index);
			buffer[gapBegin++] = c;
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> sequence) {
		rangeCheckForAdd(index);
		Object[] objects = sequence.toArray();
		ensureLength(size() + objects.length);
		if (index == gapBegin) {
			System.arraycopy(objects, 0, buffer, gapBegin, objects.length);
		} else {
			moveGap(index);
			System.arraycopy(objects, 0, buffer, gapBegin, objects.length);
		}
		gapBegin += objects.length;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return addAll(size(), c);
	}

	@Override
	public T set(int index, T element) {
		rangeCheck(index);
		int actualIndex = index <= gapBegin ? index : index + gapLength();
		Object old = buffer[actualIndex];
		buffer[actualIndex] = element;
		//noinspection unchecked
		return (T) old;
	}

	@Override
	public T remove(int index) {
		rangeCheck(index);
		Object result;
		if (index == gapBegin - 1) {
			result = buffer[gapBegin--];
		} else if (index == gapBegin) {
			result = buffer[gapBegin];
			gapEnd++;
		} else {
			moveGap(index);
			result = buffer[gapBegin];
			gapEnd++;
		}
		//noinspection unchecked
		return (T) result;
	}

	private void moveGap(int afterBegin) {
		if (afterBegin == gapBegin) return;
		int afterEnd = gapEnd + afterBegin - gapBegin;
		if (afterBegin > gapBegin) System.arraycopy(buffer, gapBegin, buffer, gapEnd, afterBegin - gapBegin);
		else System.arraycopy(buffer, afterEnd, buffer, afterBegin, gapBegin - afterBegin);
		gapBegin = afterBegin;
		gapEnd = afterEnd;
	}

	private void ensureLength(int length) {
		int bufferLength = buffer.length;
		if (bufferLength >= length) return;
		int newLength = Math.max(bufferLength * 2, length);
		Object[] newBuffer = new Object[newLength];
		System.arraycopy(buffer, 0, newBuffer, 0, gapBegin);
		int newGapEnd = newLength - size() + gapBegin;
		System.arraycopy(buffer, gapEnd, newBuffer, newGapEnd, bufferLength - gapEnd);
		gapEnd = newGapEnd;
		buffer = newBuffer;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < gapBegin; i++) builder.append(buffer[i]);
		for (int i = gapEnd; i < buffer.length; i++) builder.append(buffer[i]);
		return builder.toString();
	}

	@Override
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public GapList<T> clone() {
		return new GapList<>(Arrays.copyOf(buffer, buffer.length), gapBegin, gapEnd);
	}

	@Override
	public Object[] toArray() {
		Object[] ret = new Object[size()];
		System.arraycopy(buffer, 0, ret, 0, gapBegin);
		System.arraycopy(buffer, gapEnd, ret, gapBegin, buffer.length - gapEnd);
		return ret;
	}

	@Override
	public void clear() {
		gapBegin = 0;
		gapEnd = buffer.length;
	}

	/**
	 * Checks if the given index is in range.  If not, throws an appropriate
	 * runtime exception.  This method does *not* check if the index is
	 * negative: It is always used immediately prior to an array access,
	 * which throws an ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void rangeCheck(int index) {
		if (index >= size())
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * A version of rangeCheck used by add and addAll.
	 */
	private void rangeCheckForAdd(int index) {
		if (index > size() || index < 0)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public void forEach(Consumer<? super T> action) {
		Objects.requireNonNull(action);
		for (int i = 0; i < gapBegin; i++) action.accept((T) buffer[i]);
		for (int i = gapEnd; i < buffer.length; i++) action.accept((T) buffer[i]);
	}

	@Override
	public int indexOf(Object o) {
		for (int i = 0; i < gapBegin; i++) if (Objects.equals(o, buffer[i])) return i;
		for (int i = gapEnd; i < buffer.length; i++) if (Objects.equals(o, buffer[i])) return i - gapLength();
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = buffer.length - 1; i >= gapEnd; i--) if (Objects.equals(o, buffer[i])) return i - gapLength();
		for (int i = gapBegin - 1; i >= 0; i--) if (Objects.equals(o, buffer[i])) return i;
		return -1;
	}

	/**
	 * Constructs an IndexOutOfBoundsException detail message.
	 * Of the many possible refactorings of the error handling code,
	 * this "outlining" performs best with both server and client VMs.
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size();
	}

	@Override
	public boolean contains(Object o) {
		for (int i = 0; i < gapBegin; i++) if (Objects.equals(o, buffer[i])) return true;
		for (int i = gapEnd; i < buffer.length; i++) if (Objects.equals(o, buffer[i])) return true;
		return false;
	}
}
