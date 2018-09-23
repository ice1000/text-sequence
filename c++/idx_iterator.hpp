//
// Created by ice1000 on 18-8-26.
//

#pragma once
#ifndef DEX_STAR_IDX_ITERATOR_HPP
#define DEX_STAR_IDX_ITERATOR_HPP

#include <stddef.h>

namespace textseq {
	template <typename Member, typename Owner>
	class IdxIter {
	private:
		using Self = IdxIter<Member, Owner>;
	public:
		size_t index;
		Owner *owner;

		IdxIter(size_t index, Owner *owner) : index(index), owner(owner) { }
		inline Member operator*() const { return (*owner)[index]; }
		inline auto &operator++() noexcept { ++index; return *this; }
		inline auto &operator--() noexcept { --index; return *this; }
		inline Self next() const noexcept { return {index + 1, owner}; }
		inline Self prev() const noexcept { return {index - 1, owner}; }
		inline auto operator==(Self const &o) const noexcept { return index == o.index && owner == o.owner; }
		inline auto operator!=(Self const &o) const noexcept { return !(*this == o); }
	};
}

#endif //DEX_STAR_IDX_ITERATOR_HPP
