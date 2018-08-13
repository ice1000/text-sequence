#pragma once
#include <cstddef>
#include <cassert>
#include <cstring>

#define CharSeq template <typename CharSequence>

/**
 * Standalone implementation as a {@link vector}.
 *
 * @author ice1000
 */
template<typename T>
class GapList {
private:
	template<typename A, typename B>
	static inline auto max(A &&a, B &&b) { return a > b ? a : b; }

	T *buffer;
	size_t bufferLength, gapBegin, gapEnd;

	GapList(T *buffer,
	        size_t size,
	        size_t gapBegin,
	        size_t gapEnd
	) : buffer(buffer), bufferLength(size), gapBegin(gapBegin), gapEnd(gapEnd) { }

	auto moveGap(size_t shift, bool right) {
		if (shift == 0) return;
		size_t afterBegin = gapBegin;
		size_t afterEnd = gapEnd;
		if (right) {
			afterBegin += shift;
			afterEnd += shift;
			memmove(buffer + gapEnd, buffer + gapBegin, shift);
		} else {
			afterBegin -= shift;
			afterEnd -= shift;
			memmove(buffer + afterEnd, buffer + afterBegin, shift);
		}
		gapBegin = afterBegin;
		gapEnd = afterEnd;
	}

	auto ensureLength(size_t length) {
		size_t bufferLength = this->bufferLength;
		if (bufferLength >= length) return;
		size_t newLength = max(bufferLength * 2, length);
		auto newBuffer = new T[newLength];
		GapList::bufferLength = newLength;
		memmove(newBuffer + 0, buffer + 0, gapBegin);
		size_t newGapEnd = newLength - size() + gapBegin;
		memmove(newBuffer + newGapEnd, buffer + gapEnd, bufferLength - gapEnd);
		gapEnd = newGapEnd;
		delete[] buffer;
		buffer = newBuffer;
	}

	constexpr inline auto rangeCheck(size_t index) const { assert(index < size()); }
	constexpr inline auto rangeCheckForAdd(size_t index) const { assert(index <= size()); }
	constexpr inline auto gapLength() const noexcept { return gapEnd - gapBegin; }

public:
	explicit GapList(size_t initialCapacity) : GapList(new T[initialCapacity], initialCapacity, 0, initialCapacity) { }
	explicit GapList() : GapList(32) {}
	~GapList() { delete[] buffer; }

	typedef T                 value_type;
	typedef value_type&       value_reference;
	typedef value_type const& const_value_reference;

	constexpr inline auto size() const noexcept { return bufferLength - gapLength(); }
	constexpr inline auto empty() const noexcept { return size() == 0; }
	constexpr inline auto isEmpty() const noexcept { return empty(); }
	constexpr inline auto isNotEmpty() const noexcept { return !isEmpty(); }
	constexpr inline auto &operator[](size_t index) { rangeCheck(index); return buffer[index < gapBegin ? index : index + gapLength()]; }
	constexpr inline auto const &operator[](size_t index) const { rangeCheck(index); return buffer[index < gapBegin ? index : index + gapLength()]; }

	template <typename Member>
	struct GapListIter {
		size_t index;
		GapList<T> *owner;
		GapListIter(size_t index, GapList *owner) : index(index), owner(owner) { }
		Member operator*() { return (*owner)[index]; }
		GapListIter<Member> &operator++() { ++index; return *this; }
		auto operator==(const GapListIter &o) { return index == o.index && owner == o.owner; }
		auto operator!=(const GapListIter &o) { return !(*this == o); }
	};

	typedef GapListIter<value_reference>       iterator;
	typedef GapListIter<const_value_reference> const_iterator;

	constexpr iterator begin() { return {0, this}; }
	constexpr iterator end() { return {size(), this}; }
	constexpr const_iterator cbegin() const { return {0, this}; }
	constexpr const_iterator cend() const { return {size(), this}; }

	auto insert(size_t index, const_value_reference c) {
		rangeCheckForAdd(index);
		ensureLength(size() + 1);
		if (index == gapBegin) buffer[gapBegin++] = c;
		else {
			bool right = index > gapBegin;
			moveGap(right ? index - gapBegin : gapBegin - index, right);
			assert(index == gapBegin);
			buffer[gapBegin++] = c;
		}
	}

	auto erase_at(size_t index) {
		rangeCheck(index);
		value_type *result;
		size_t size = this->size();
		if (index == gapBegin) {
			result = buffer + gapBegin;
			if (index == size) gapBegin--;
			else gapEnd++;
		} else if (index == gapBegin - 1) {
			result = buffer + gapBegin--;
		} else {
			bool right = index > gapBegin;
			moveGap(right ? index - gapBegin : gapBegin - index, right);
			assert(index == gapBegin);
			result = buffer + gapBegin;
			if (index == size) gapBegin--;
			else gapEnd++;
		}
		return result;
	}

	auto find(const_value_reference o) {
		for (iterator i = buffer, bufferPart = buffer + gapBegin; i < bufferPart; i++) if (o == *i) return i;
		for (iterator i = buffer + gapEnd, bufferEnd = buffer + bufferLength; i < bufferEnd; i++) if (o == *i) return i;
		return nullptr;
	}

	auto find_last(const_value_reference o) {
		for (iterator i = buffer + bufferLength - 1, bufferPart = buffer + gapEnd; i >= bufferPart; i--) if (o == *i) return i;
		for (iterator i = buffer + gapBegin - 1; i >= buffer; i--) if (o == *i) return i;
		return nullptr;
	}

	CharSeq auto insert(size_t index, const CharSequence &sequence, size_t sequenceSize) {
		ensureLength(size() + sequenceSize);
		if (index == gapBegin) {
			for (size_t i = 0; i < sequenceSize; i++) buffer[gapBegin + i] = sequence[i];
		} else {
			bool right = index > gapBegin;
			moveGap(right ? index - gapBegin : gapBegin - index, right);
			assert(index == gapBegin);
			for (size_t i = 0; i < sequenceSize; i++) buffer[gapBegin + i] = sequence[i];
		}
		gapBegin += sequenceSize;
	}

	inline auto push_back(const_value_reference c) { insert(size(), c); }
	inline auto append(const_value_reference c) { insert(size(), c); }
	inline auto push_front(const_value_reference c) { insert(0, c); }
	inline auto pop_front() { return erase_at(0); }
	inline auto pop_back() { return erase_at(size() - 1); }
	inline auto erase(const_iterator iter) { assert(iter > buffer && iter < buffer + bufferLength); return erase_at(iter - buffer); }
	inline auto clear() { gapBegin = 0; gapEnd = bufferLength; }
	inline auto contains(const_value_reference o) { return find(o) != nullptr; }
	CharSeq inline auto insert(const CharSequence &sequence, size_t sequenceSize) { insert(size(), sequence, sequenceSize); }
	CharSeq inline auto append(const CharSequence &sequence, size_t sequenceSize) { insert(size(), sequence, sequenceSize); }

	// for (size_t i = 0; i < gapBegin; i++) action.accept((T) buffer[i]);
	// for (size_t i = gapEnd; i < buffer.length; i++) action.accept((T) buffer[i]);
};

using GapBuffer = GapList<char>;
