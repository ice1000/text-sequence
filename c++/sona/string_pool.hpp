#pragma once
#ifndef STRING_POOL_HPP
#define STRING_POOL_HPP

#include <stddef.h>
#include <stdint.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>

#ifndef STRING_POOL_STRCMP
#define STRING_POOL_STRCMP strcmp
#endif // STRING_POOL_STRCMP

#ifndef STRING_POOL_STRCPY
// #ifdef WIN32
// #define STRING_POOL_STRCPY(a, b, n) strcpy_s
// #else // WIN32
#define STRING_POOL_STRCPY strcpy
// #endif // WIN32
#endif // STRING_POOL_STRCPY

#ifndef STRING_POOL_STRNCPY
#ifdef WIN32
#ifndef __MINGW32__
#define STRING_POOL_STRNCPY(a, b, n) strncpy_s((a), (n) + 1, (b), (n))
#else // __MINGW32__
#define STRING_POOL_STRNCPY(a, b, n) strncpy((a), (b), (n))
#endif // __MINGW32__
#else // WIN32
#define STRING_POOL_STRNCPY(a, b, n) strncpy((a), (b), (n))
#endif // WIN32
#endif // STRING_POOL_STRNCPY

#ifndef STRING_POOL_STRLEN
#define STRING_POOL_STRLEN strlen
#endif // STRING_POOL_STRLEN

#ifndef STRING_POOL_CHAR
#define STRING_POOL_CHAR char
#endif // STRING_POOL_CHAR

#ifndef STRING_POOL_DEFAULT_STRING
#define STRING_POOL_DEFAULT_STRING ""
#endif // STRING_POOL_DEFAULT_STRINGs

namespace sona {
	using CharType = STRING_POOL_CHAR;
	using RawStr = const CharType *;
	template<typename T> using Ptr = T *;
	template<typename T> using Com = T const&;

	class StringPoolBase {
	protected:
		friend class String;

		virtual uint64_t CreateString(RawStr, size_t, bool) = 0;
		virtual uint64_t CreateString(CharType const*, CharType const*) = 0;
		virtual RawStr GetString(uint64_t) const = 0;
		virtual size_t GetStringSize(uint64_t) const = 0;
		auto CreateString(RawStr str, bool mayConsumeInputStr) { return CreateString(str, STRING_POOL_STRLEN(str), mayConsumeInputStr); }
		auto CreateString(RawStr str) { return CreateString(str, false); }

	public:
		virtual ~StringPoolBase() = default;
	};

	template<size_t BucketCount = 4396>
	class StringPool : public StringPoolBase {
		friend class String;

		typedef struct hashMapItem {
			Ptr<STRING_POOL_CHAR> value;
			size_t size;
			Ptr<struct hashMapItem> next;
			uint64_t handle;
		} HashMapItem;

		Ptr<HashMapItem> hashMap[BucketCount] = {0};

		static Ptr<HashMapItem> newHashMapItem(RawStr value, uint64_t handle,
		                                       bool mayConsumeInputValue,
		                                       size_t cachedSize) {
			auto entry = (Ptr<HashMapItem>) malloc(sizeof(HashMapItem));
			entry->size = !mayConsumeInputValue ? STRING_POOL_STRLEN(value) : cachedSize;
			if (!mayConsumeInputValue) {
				entry->value = (Ptr<STRING_POOL_CHAR>) malloc((entry->size + 1) * sizeof(CharType));
				STRING_POOL_STRCPY(entry->value, value);
			} else entry->value = const_cast<Ptr<CharType>>(value);
			entry->next = NULL;
			entry->handle = handle;
			return entry;
		}

		static uint64_t BKDRHash(RawStr str) {
			uint64_t hash = 0;
			CharType ch = *str++;
			while (ch) {
				hash = hash * 13 + ch;
				ch = *str++;
			}
			return hash;
		}

		uint64_t CreateString(CharType const *beg, CharType const *end) override {
			size_t size = end - beg;
			if (!size) return CreateString(STRING_POOL_DEFAULT_STRING, 0, false);
			auto str = (CharType *) malloc((size + 1) * sizeof(CharType));
			STRING_POOL_STRNCPY(str, beg, size);
			str[size] = '\0';
			return CreateString(str, size, true);
		}

		uint64_t CreateString(RawStr str, size_t cachedSize, bool mayConsumeInputStr) override {
			uint64_t strHash = BKDRHash(str);
			auto bucket = static_cast<size_t>(strHash % BucketCount);

			if (hashMap[bucket]) {
				Ptr<HashMapItem> item = hashMap[bucket];

				/// one element only
				if (item->next == NULL) {
					if (!STRING_POOL_STRCMP(item->value, str)) {
						return item->handle;
					} else {
						item->next = newHashMapItem(str, bucket * (1ULL << 31) + 1, mayConsumeInputStr, cachedSize);
						return item->next->handle;
					}
				}

				int n = 1;
				while (item->next != NULL) {
					if (!STRING_POOL_STRCMP(item->value, str)) {
						return item->handle;
					}
					item = item->next;
					n++;
				}
				item->next = newHashMapItem(str, bucket * (1ULL << 31) + n, mayConsumeInputStr, cachedSize);
				return item->next->handle;
			} else {
				hashMap[bucket] = newHashMapItem(str, bucket * (1ULL << 31), mayConsumeInputStr, cachedSize);
				return hashMap[bucket]->handle;
			}
		}

		RawStr GetString(uint64_t hStr) const override {
			auto bucket = static_cast<size_t>(hStr / (1ULL << 31));
			Ptr<HashMapItem> item = hashMap[bucket];
			while (item) {
				if (item->handle == hStr) return item->value;
				item = item->next;
			}

			assert(!"Internal error");
			abort();
		}

		size_t GetStringSize(uint64_t hStr) const override {
			auto bucket = static_cast<size_t>(hStr / (1ULL << 31));
			Ptr<HashMapItem> item = hashMap[bucket];
			while (item) {
				if (item->handle == hStr) return item->size;
				item = item->next;
			}

			assert(!"Internal error");
			abort();
		}

		~StringPool() override {
			for (size_t i = 0; i < BucketCount; i++) {
				Ptr<HashMapItem> iter = hashMap[i];
				while (iter != NULL) {
					Ptr<HashMapItem> t = iter;
					iter = iter->next;
					free(t->value);
					free(t);
				}
			}
		}
	};

	class String {
	private:
		using Pool = Ptr<StringPoolBase>;
		using PChar = Ptr<CharType const>;
		Pool pool;
		uint64_t handle;
	public:
		/// for lua use only
		explicit String() : pool(nullptr), handle(0) {};
		String(Pool pool, RawStr raw) : pool(pool), handle(pool->CreateString(raw, 0, false)) {}
		String(Pool pool, PChar begin, PChar end) : pool(pool), handle(pool->CreateString(begin, end)) {}
		explicit String(Ptr<StringPoolBase> pool) : pool(pool) {
			static auto hEmptyStr = pool->CreateString(STRING_POOL_DEFAULT_STRING, 0, false);
			handle = hEmptyStr;
		}

		friend bool operator==(Com<String> lhs, Com<String> rhs) {
			return lhs.pool != rhs.pool ? !STRING_POOL_STRCMP(lhs, rhs) : lhs.handle == rhs.handle;
		}

		friend bool operator!=(Com<String> lhs, Com<String> rhs) { return !(lhs == rhs); }
		operator RawStr() const { return Raw(); }
		RawStr Raw() const { return pool->GetString(handle); }
		String CreateFromSamePool(RawStr raw) const { return {pool, raw}; }
		inline auto Size() const { return pool->GetStringSize(handle); }
	};
}

#endif // STRING_POOL_HPP
