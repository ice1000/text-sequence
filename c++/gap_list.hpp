#pragma once
#include <stddef.h>
#include <assert.h>
#include <string.h>
#include "idx_iterator.hpp"

#define CharSeq template <typename CharSequence>

namespace textseq {
	template<typename T>
	class GapList;

	using GapBuffer = GapList<char>;
}

/**
 * Standalone implementation as a {@link vector}.
 *
 * @author ice1000
 */
template<typename T>
class textseq::GapList {
private:
	template<typename A, typename B>
	constexpr static inline auto max(A &&a, B &&b) { return a > b ? a : b; }
	template<typename A, typename B>
	constexpr static inline auto arraycopy(A *dest, B *src, size_t size) { return memmove(dest, src, size * sizeof(A)); }

	T *buffer;
	size_t bufferLength, gapBegin, gapEnd;

	GapList(T *buffer,
	        size_t size,
	        size_t gapBegin,
	        size_t gapEnd
	) : buffer(buffer), bufferLength(size), gapBegin(gapBegin), gapEnd(gapEnd) { }

	auto moveGap(size_t afterBegin) {
		if (afterBegin == gapBegin) return;
		size_t afterEnd = gapEnd + afterBegin - gapBegin;
		afterBegin > gapBegin
		/* to right */ ? arraycopy(buffer + gapBegin, buffer + gapEnd, afterBegin - gapBegin)
		/* to left  */ : arraycopy(buffer + afterEnd, buffer + afterBegin, gapBegin - afterBegin);
		gapBegin = afterBegin;
		gapEnd = afterEnd;
	}

	auto ensureLength(size_t length) {
		if (bufferLength >= length) return;
		size_t newLength = max(bufferLength * 2, length);
		auto newBuffer = new T[newLength];
		arraycopy(newBuffer, buffer, gapBegin);
		size_t newGapEnd = newLength - size() + gapBegin;
		arraycopy(newBuffer + newGapEnd, buffer + gapEnd, bufferLength - gapEnd);
		gapEnd = newGapEnd;
		delete[] buffer;
		buffer = newBuffer;
		bufferLength = newLength;
	}

	auto eraseAtUnchecked(size_t index) {
		value_type *result;
		size_t size = this->size();
		if (index == gapBegin - 1) {
			result = buffer + gapBegin--;
		} else if (index == gapBegin) {
			result = buffer + gapBegin;
			gapEnd++;
		} else {
			moveGap(index);
			result = buffer + gapBegin;
			gapEnd++;
		}
		return result;
	}

	constexpr inline auto rangeCheck(size_t index) const { assert(index < size()); }
	constexpr inline auto rangeCheckForAdd(size_t index) const { assert(index <= size()); }
	constexpr inline auto gapLength() const noexcept { return gapEnd - gapBegin; }

public:
	explicit GapList(size_t initialCapacity) : GapList(new T[initialCapacity], initialCapacity, 0, initialCapacity) { }
	GapList() : GapList(32) {}
	~GapList() { delete[] buffer; }

	constexpr inline auto size() const noexcept { return bufferLength - gapLength(); }
	constexpr inline auto empty() const noexcept { return size() == 0; }
	constexpr inline auto &operator[](size_t index) { rangeCheck(index); return buffer[index < gapBegin ? index : index + gapLength()]; }
	constexpr inline auto const &operator[](size_t index) const { rangeCheck(index); return buffer[index < gapBegin ? index : index + gapLength()]; }
	constexpr inline auto &at(size_t index) { return operator[](index); }
	constexpr inline auto const &at(size_t index) const { return operator[](index); }

	using value_type = T;
	using reference = value_type &;
	using const_reference = value_type const &;
	using pointer = value_type *;
	using const_pointer = const value_type *;
	using iterator = IdxIter<reference, GapList<T>>;
	using const_iterator = IdxIter<const_reference, const GapList<T>>;

	iterator begin() { return {0, this}; }
	iterator end() { return {size(), this}; }
	const_iterator begin() const { return {0, this}; }
	const_iterator end() const { return {size(), this}; }
	const_iterator cbegin() const { return {0, this}; }
	const_iterator cend() const { return {size(), this}; }

	auto insert(size_t index, const_reference c) {
		rangeCheckForAdd(index);
		ensureLength(size() + 1);
		if (index == gapBegin) buffer[gapBegin++] = c;
		else {
			moveGap(index);
			buffer[gapBegin++] = c;
		}
	}

	auto find(const_reference o) const {
		for (pointer i = buffer, bufferPart = buffer + gapBegin; i < bufferPart; i++) if (o == *i) return i;
		for (pointer i = buffer + gapEnd, bufferEnd = buffer + bufferLength; i < bufferEnd; i++) if (o == *i) return i;
		return nullptr;
	}

	auto find_last(const_reference o) const {
		for (pointer i = buffer + bufferLength - 1, bufferPart = buffer + gapEnd; i >= bufferPart; i--) if (o == *i) return i;
		for (pointer i = buffer + gapBegin - 1; i >= buffer; i--) if (o == *i) return i;
		return nullptr;
	}

	CharSeq auto insert(size_t index, CharSequence const &sequence, size_t sequenceSize) {
		ensureLength(size() + sequenceSize);
		if (index == gapBegin) {
			for (size_t i = 0; i < sequenceSize; i++) buffer[gapBegin + i] = sequence[i];
		} else {
			moveGap(index);
			for (size_t i = 0; i < sequenceSize; i++) buffer[gapBegin + i] = sequence[i];
		}
		gapBegin += sequenceSize;
	}

	inline auto erase_at(size_t index) { rangeCheck(index); return eraseAtUnchecked(index); }
	inline auto push_back(const_reference c) { insert(size(), c); }
	inline auto find_first(const_reference o) const { return find(o); }
	inline auto append(const_reference c) { insert(size(), c); }
	inline auto push_front(const_reference c) { insert(0, c); }
	inline auto pop_front() { assert(!empty()); return eraseAtUnchecked(0); }
	inline auto pop_back() { assert(!empty()); return eraseAtUnchecked(size() - 1); }
	inline auto erase(const_iterator iter) { assert(iter > buffer && iter < buffer + bufferLength); return erase_at(iter - buffer); }
	inline auto clear() { gapBegin = 0; gapEnd = bufferLength; }
	inline auto contains(const_reference o) { return find(o) != nullptr; }
	CharSeq inline auto insert(CharSequence const &sequence, size_t sequenceSize) { insert(size(), sequence, sequenceSize); }
	CharSeq inline auto append(CharSequence const &sequence, size_t sequenceSize) { insert(size(), sequence, sequenceSize); }

	// for (size_t i = 0; i < gapBegin; i++) action.accept((T) buffer[i]);
	// for (size_t i = gapEnd; i < buffer.length; i++) action.accept((T) buffer[i]);

	/// implementation details
	inline auto move_gap(size_t index) { moveGap(index); }
	inline auto move_gap_to_end() { move_gap(size()); }
	constexpr inline auto const raw_data() const { return buffer; }
	constexpr inline auto raw_data() { return buffer; }
	constexpr inline auto raw_data_size() const noexcept { return bufferLength; }
	constexpr inline auto gap_begin() const noexcept { return gapBegin; }
	constexpr inline auto gap_end() const noexcept { return gapEnd; }
};
