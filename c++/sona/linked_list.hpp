//
// Created by ice1000 on 18-8-31.
//

#pragma once
#ifndef DEX_STAR_LINKED_LIST_H
#define DEX_STAR_LINKED_LIST_H

//////////////////////////////////////////////////////////////////////////////

//  芙蓉城三月雨纷纷 四月绣花针                                             //
//  羽毛扇遥指千军阵 锦缎裁几寸                                             //
//  看铁马踏冰河 丝线缝韶华 红尘千帐灯                                      //
//  山水一程风雪再一程                                                      //
//  红烛枕五月花叶深 六月杏花村                                             //
//  红酥手青丝万千根 姻缘多一分                                             //
//  等残阳照孤影 牡丹染铜樽 满城牧笛声                                      //
//  伊人倚门望君踏归程                                                      //

//  君可见刺绣每一针 有人为你疼                                             //
//  君可见牡丹开一生 有人为你等                                             //
//  江河入海奔 万物为谁春                                                   //
//  明月照不尽离别人                                                        //
//  君可见刺绣又一针 有人为你疼                                             //
//  君可见夏雨秋风 有人为你等                                               //
//  翠竹泣墨痕 锦书画不成                                                   //
//  情针意线绣不尽 鸳鸯枕                                                   //

//  此生笑傲风月瘦如刀 催人老                                               //
//  来世与君暮暮又朝朝 多逍遥                                               //

//  绕指柔破锦千万针 杜鹃啼血声                                             //
//  芙蓉花蜀国尽缤纷 转眼尘归尘                                             //
//  战歌送离人 行人欲断魂                                                   //
//  浓情蜜意此话当真

//  君可见刺绣每一针 有人为你疼                                             //
//  君可见牡丹开一生 有人为你等                                             //
//  江河入海奔 万物为谁春                                                   //
//  明月照不尽离别人                                                        //
//  君可见刺绣又一针 有人为你疼                                             //
//  君可见夏雨秋风有人 为你等                                               //
//  翠竹泣墨痕 锦书画不成                                                   //
//  情针意线绣不尽 鸳鸯枕                                                   //

//////////////////////////////////////////////////////////////////////////////

#include <stddef.h>
#include <stdlib.h>
#include <assert.h>
#ifdef MOVE_SEMANTICS
#include <new>
#define LINKED_LIST_FORWARD std::forward
#define LINKED_LIST_SWAP std::swap
#else
#define LINKED_LIST_FORWARD forward
#define LINKED_LIST_SWAP swap
#endif // MOVE_SEMANTICS
#include <util/tools.hpp>

#define Tpl template<typename T>
#define TpArgs template<typename... Args>
#define TpnItr typename LinkedList<T>::Iter
#define TpnLN typename LinkedList<T>::ListNode
#define TpnLNB typename LinkedList<T>::ListNodeBase
#define TpnCItr typename LinkedList<T>::ConstIter
#define ITER_AT_BODY \
if (index < (size() >> 1)) { \
  auto node = begin(); for (decltype(index) i = 0; i < index; ++i) node++; return node; \
} else { \
  auto node = end(); for (decltype(index) i = size(); i > index; --i) node--; return node; \
}
#define ITER_BODY \
const_reference operator*()const; \
const_pointer operator->()const { return &operator*(); }; \
bool operator==(const Self &_another) const; \
bool operator!=(const Self &_another) const; \
Self &operator++(); \
Self &operator--(); \
const Self operator++(signed int); \
const Self operator--(signed int); \
Self &operator=(Self const &) = default; \
auto distance_to(Self const &o) const { \
	size_t distance{0}; \
	Self iter{*this}; \
	while (iter != o) ++distance, ++iter; \
	return distance; \
} \
inline auto distance_from(Self const &o) const { return o.distance_to(*this); }

#define ITER_TYPES \
using value_type = typename LinkedList::value_type; \
using difference_type = typename LinkedList::difference_type; \
using pointer = typename LinkedList::pointer; \
using const_pointer = typename LinkedList::const_pointer; \
using reference = typename LinkedList::reference; \
using const_reference = typename LinkedList::const_reference;

namespace sona {
	Tpl struct equal_to { bool operator()(T const &t1, T const &t2) { return t1 == t2; } };
#ifdef MOVE_SEMANTICS
	Tpl void destroy_at(T *t) { t->~T(); free(t); }
	template <typename T, typename... Args> void construct(Ref<Ptr<T>> t, Args &&... args) {
		t = reinterpret_cast<Ptr<T>>(malloc(sizeof(T)));
		::new (t) T(LINKED_LIST_FORWARD<Args>(args)...);
	}
#else
	Tpl void destroy_at(T *t) { delete t; }
	template <typename T, typename... Args> void construct(Ref<Ptr<T>> t, Args &&... args) {
		t = new T(LINKED_LIST_FORWARD<Args>(args)...);
	}
#endif // MOVE_SEMANTICS

	Tpl class LinkedList {
	public:
		using value_type = T;
		using pointer = value_type *;
		using const_pointer = const value_type *;
		using reference = value_type &;
		using const_reference = const value_type &;
		using size_type = size_t;
		using difference_type = ptrdiff_t;

		class Iter;
		class ConstIter;

		using iterator = Iter;
		using const_iterator = ConstIter;

		LinkedList();
		LinkedList(const LinkedList &_another);
		explicit LinkedList(size_type _n);
		LinkedList(size_type _n, const_reference _value);
		template <typename InputIterator>
		LinkedList(InputIterator _first, InputIterator _last);
		~LinkedList();

		constexpr inline auto size() const noexcept { return length; }
		constexpr inline auto empty() const noexcept { auto a = head->next == head; assert(a == (length == 0)); return a; }

		Iter insert(ConstIter _position, const_reference _value);
		Iter insert(ConstIter _position, size_type _n, const_reference _value);
		template <typename InputIterator>
		Iter insert(ConstIter _position, InputIterator _first, InputIterator _last);

#ifdef MOVE_SEMANTICS
		inline auto insert(ConstIter _position, value_type &&_value) { return emplace(_position, std::move(_value)); }
#endif // MOVE_SEMANTICS

		TpArgs Iter emplace(ConstIter _position, Args &&... _args);
		TpArgs Iter emplace_back(Args &&... _args);
		TpArgs Iter emplace_front(Args &&... _args);
		inline auto push_back(const_reference _value) { insert(cend(), _value); }
		inline auto push_front(const_reference _value) { insert(cbegin(), _value); }
		Iter erase(ConstIter _position);
		Iter erase(ConstIter _first, ConstIter _last);
		inline auto erase_at(size_type index) { return erase(citer_at(index)); }
		inline auto insert_at(size_type index, const_reference _value) { return insert(citer_at(index), _value); }

		void pop_back();
		void pop_front();
		void clear() noexcept;
		void remove(const_reference _value);
		template <typename UnaryPredicate> void remove_if(UnaryPredicate _pred);
		void unique();
		template <typename BinaryPredicate> void unique(BinaryPredicate _binary_pred);
		Iter iter_at(size_type index);
		ConstIter iter_at(size_type index) const;
		ConstIter citer_at(size_type index) const;
		inline auto &operator[](size_type index) { return *iter_at(index); }
		inline auto const &operator[](size_type index) const { return *citer_at(index); }

/// TODO implement these functions when necessary
//		void splice(ConstIter _pos, LinkedList &_other);
//		void splice(ConstIter _pos, LinkedList &&_other);
//		void splice(ConstIter _pos, LinkedList &_other, ConstIter _it);
//		void splice(ConstIter _pos, LinkedList &&_other, ConstIter _it);
//		void splice(ConstIter _pos, LinkedList &_other, ConstIter _first,
//		            ConstIter _last);
//		void splice(ConstIter _pos, LinkedList &&_other, ConstIter _first,
//		            ConstIter _last);

		void swap(LinkedList &_another) noexcept;

		Iter begin() noexcept;
		Iter end() noexcept;
		ConstIter begin() const noexcept;
		ConstIter end() const noexcept;
		ConstIter cbegin() const noexcept;
		ConstIter cend() const noexcept;

	private:
		struct ListNodeBase {
			ListNodeBase *prev = nullptr;
			ListNodeBase *next = nullptr;

			ListNodeBase() = default;
			virtual ~ListNodeBase() = default;
		};

		struct ListNode : public ListNodeBase {
			T value;
			TpArgs explicit ListNode(Args &&... _args) : ListNodeBase(), value(_args...) {}
			~ListNode() override = default;
		};

		ListNodeBase *head;
		size_type length{0};
	};

	Tpl class LinkedList<T>::Iter {
		friend class LinkedList;

	public:
		ITER_TYPES
		using Self = Iter;

		Iter() = default;
		Iter(const Self &_another) = default;
		// See C++ Defect Report #179
    Iter(const TpnCItr &_const_iterator);
		~Iter() = default;

		reference operator*();
		pointer operator->() { return &operator*(); };
		ITER_BODY

	private:
		Iter(LinkedList *_get_from, TpnLNB *_node);

		LinkedList *get_from = nullptr;
		TpnLNB *node = nullptr;
	};

	Tpl class LinkedList<T>::ConstIter {
		friend class LinkedList;

	public:
		ITER_TYPES
		using Self = ConstIter;

		ConstIter() = default;
		ConstIter(const Self &_another) = default;
		// See C++ Defect Report #179
		explicit ConstIter(const TpnItr &_mutable_iterator);
		~ConstIter() = default;
		ITER_BODY

		Self &operator=(Iter const &_another);

	private:
		ConstIter(const LinkedList *_get_from, const TpnLNB *_node);

		const LinkedList *get_from = nullptr;
		const TpnLNB *node = nullptr;
	};

	Tpl LinkedList<T>::LinkedList(const LinkedList &_another) : LinkedList() { insert(cend(), _another.cbegin(), _another.cend()); }
	Tpl LinkedList<T>::LinkedList(size_type _n, const_reference _value) : LinkedList() { insert(cend(), _n, _value); }
	Tpl LinkedList<T>::LinkedList(size_type _n) : LinkedList(_n, value_type()) {}
	Tpl LinkedList<T>::LinkedList() : head(new ListNodeBase) { head->prev = head; head->next = head; }

	Tpl template <typename InputIterator>
	LinkedList<T>::LinkedList(InputIterator _first, InputIterator _last) : LinkedList() { insert(cend(), _first, _last); }
	Tpl LinkedList<T>::~LinkedList() { erase(cbegin(), cend()); delete head; }

	Tpl TpnItr LinkedList<T>::insert(ConstIter _position, const_reference _value) { return emplace(_position, _value); }
	Tpl TpnItr LinkedList<T>::insert(ConstIter _position, size_type _n, const_reference _value) {
		for (size_type i = 0; i < _n; ++i) insert(_position, _value);
	}

	Tpl template <typename InputIterator> TpnItr LinkedList<T>::insert(ConstIter _position, InputIterator _first, InputIterator _last) {
		for (; _first != _last; ++_first) insert(_position, *_first);
		return {this, const_cast<ListNodeBase *>(_position.node)};
	}

	Tpl TpArgs TpnItr LinkedList<T>::emplace(ConstIter _position, Args &&... _args) {
		Iter position{this, const_cast<ListNodeBase *>(_position.node)};

		ListNode *node = nullptr; // (ListNode *)malloc(sizeof(ListNode));
		construct(node, LINKED_LIST_FORWARD<Args>(_args)...);

		node->prev = position.node->prev;
		node->next = position.node;

		position.node->prev->next = node;
		position.node->prev = node;

		++length;
		return {this, node};
	}

	Tpl TpArgs TpnItr LinkedList<T>::emplace_back(Args &&... _args) { emplace(cend(), LINKED_LIST_FORWARD<Args>(_args)...); }
	Tpl TpArgs TpnItr LinkedList<T>::emplace_front(Args &&... _args) { emplace(cbegin(), LINKED_LIST_FORWARD<Args>(_args)...); }

	Tpl TpnItr LinkedList<T>::erase(ConstIter _position) {
		assert(!empty());
		ListNodeBase *prev = _position.node->prev, *next = _position.node->next;

		prev->next = next;
		next->prev = prev;

		auto *mutable_node = const_cast<ListNodeBase *>(_position.node);
		auto *converted_node = reinterpret_cast<ListNode *>(mutable_node);

		destroy_at(converted_node);
		--length;
		return {this, next};
	}

	Tpl TpnItr LinkedList<T>::erase(ConstIter _first, ConstIter _last) {
		for (; _first != _last; _first = erase(_first));
		return {this, const_cast<ListNodeBase *>(_last.node)};
	}

	Tpl void LinkedList<T>::pop_back() { erase(--cend()); }
	Tpl void LinkedList<T>::pop_front() { erase(cbegin()); }
	Tpl void LinkedList<T>::clear() noexcept { erase(cbegin(), cend()); }
	Tpl void LinkedList<T>::remove(const_reference _value) { remove_if([&](const_reference _another) -> bool { return _another == _value; }); }
	Tpl void LinkedList<T>::unique() { unique(equal_to<T>()); }

	Tpl template <typename UnaryPredicate>
	void LinkedList<T>::remove_if(UnaryPredicate _pred) {
		for (auto it = begin(); it != end();) {
			if (_pred(*it)) { it = erase(ConstIter{it}); } else { it++; }
		}
	}

	Tpl template <typename BinaryPredicate>
	void LinkedList<T>::unique(BinaryPredicate _binary_pred) {
		if (cbegin() == cend()) return;

		auto it = cbegin(), it2 = ++cbegin();
		while (it2 != cend()) {
			it = _binary_pred(*it, *it2) ? erase(it) : it + 1;

			it2 = it++;
			LINKED_LIST_SWAP(it2, it);
		}
	}

/// TODO add implementation when necessary
//	Tpl void LinkedList<T>::splice(ConstIter _pos, LinkedList &_other) {
//		splice(_pos, _other, _other.begin(), _other.end());
//	}

//	Tpl void LinkedList<T>::splice(ConstIter _pos, LinkedList &_other, ConstIter _it) {
//		splice(_pos, _other, _it, _other.cend());
//	}

//	Tpl void LinkedList<T>::splice(ConstIter _pos, LinkedList &_other, ConstIter _first, ConstIter _last) {
//		insert(_pos, _first, _last);
//		_other.erase(_first, _last);
//	}

	Tpl void LinkedList<T>::swap(LinkedList &_another) noexcept { LINKED_LIST_SWAP(head, _another.head); }
	Tpl TpnItr LinkedList<T>::iter_at(size_type index) { assert(index <= size()); ITER_AT_BODY; }
	Tpl TpnCItr LinkedList<T>::iter_at(size_type index) const { assert(index <= size()); ITER_AT_BODY; }
	Tpl TpnCItr LinkedList<T>::citer_at(size_type index) const { assert(index <= size()); ITER_AT_BODY; }
	Tpl TpnItr LinkedList<T>::begin() noexcept { return {this, head->next}; }
	Tpl TpnItr LinkedList<T>::end() noexcept { return {this, head}; }
	Tpl TpnCItr LinkedList<T>::begin() const noexcept { return {this, head->next}; }
	Tpl TpnCItr LinkedList<T>::end() const noexcept { return {this, head}; }
	Tpl TpnCItr LinkedList<T>::cbegin() const noexcept { return begin(); }
	Tpl TpnCItr LinkedList<T>::cend() const noexcept { return end(); }
	Tpl LinkedList<T>::Iter::Iter(const TpnCItr &_const_iterator) : get_from(const_cast<LinkedList *>(_const_iterator.get_from)), node(const_cast<TpnLNB *>(_const_iterator.node)) {}
	Tpl bool LinkedList<T>::Iter::operator==(const Iter &_another) const { return node == _another.node; }
	Tpl bool LinkedList<T>::Iter::operator!=(const Iter &_another) const { return !operator==(_another); }
	Tpl TpnItr::reference LinkedList<T>::Iter::operator*() { return (reinterpret_cast<TpnLN *>(node))->value; }
	Tpl TpnItr::const_reference LinkedList<T>::Iter::operator*() const { return (reinterpret_cast<const TpnLN *>(node))->value; }
	Tpl TpnItr &LinkedList<T>::Iter::operator++() { node = node->next; return *this; }
	Tpl TpnItr &LinkedList<T>::Iter::operator--() { node = node->prev; return *this; }
	Tpl const TpnItr LinkedList<T>::Iter::operator++(int) { Iter ret{*this}; node = node->next; return ret; }
	Tpl const TpnItr LinkedList<T>::Iter::operator--(int) { Iter ret{*this}; node = node->prev; return ret; }
	Tpl LinkedList<T>::Iter::Iter(LinkedList *_get_from, TpnLNB *_node)		: get_from(_get_from), node(_node) {}
	Tpl LinkedList<T>::ConstIter::ConstIter(const TpnItr &_mutable_iterator) : get_from(_mutable_iterator.get_from), node(_mutable_iterator.node) {}
	Tpl TpnCItr &LinkedList<T>::ConstIter::operator=(Iter const &_that) { this->get_from = _that.get_from; this->node = _that.node; return *this; }
	Tpl bool LinkedList<T>::ConstIter::operator==(const ConstIter &_another) const { return node == _another.node; }
	Tpl bool LinkedList<T>::ConstIter::operator!=(const ConstIter &_another) const { return !operator==(_another); }
	Tpl TpnCItr &LinkedList<T>::ConstIter::operator++() { node = node->next; return *this; }
	Tpl TpnCItr &LinkedList<T>::ConstIter::operator--() { node = node->prev; return *this; }
	Tpl const TpnCItr LinkedList<T>::ConstIter::operator++(int) { ConstIter ret{*this}; node = node->next; return ret; }
	Tpl const TpnCItr LinkedList<T>::ConstIter::operator--(int) { ConstIter ret{*this}; node = node->prev; return ret; }
	Tpl void swap(LinkedList<T> &_a, LinkedList<T> &_b) { _a.swap(_b); }
	Tpl TpnCItr::const_reference LinkedList<T>::ConstIter::operator*() const { return reinterpret_cast<const TpnLN *>(node)->value; }
	Tpl LinkedList<T>::ConstIter::ConstIter(const LinkedList *_get_from, const TpnLNB *_node) : get_from(_get_from), node(_node) {}
} // namespace sona

#undef Tpl
#undef TpArgs
#undef TpnItr
#undef TpnCItr
#undef TpnLNB
#undef TpnLN
#undef LINKED_LIST_FORWARD
#undef LINKED_LIST_SWAP
#undef ITER_AT_BODY
#undef ITER_TYPES
#undef ITER_BODY
#endif //DEX_STAR_LINKED_LIST_H
