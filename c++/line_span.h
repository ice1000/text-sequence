//
// Created by ice1000 on 18-8-19.
//

#pragma once
#ifndef DEX_STAR_LINE_SPAN_HPP
#define DEX_STAR_LINE_SPAN_HPP

#include "sona/linked_list.hpp"
#include "sona/string_pool.hpp"
#include "gap_list.hpp"
#include "seg_tree.hpp"

namespace textseq {
	class LineSpan;
}

class textseq::LineSpan {
public:
	using char_type = sona::CharType;
	using size_type = size_t;
	using lines_type = sona::LinkedList<sona::String>;
	using lines_iter = lines_type::iterator;

	struct LineInfo {
		size_type lineNumber;
		size_type lineStart;
		size_type lineEnd;
		lines_iter iter;

		/// for lua
		explicit LineInfo() : LineInfo(lines_iter()) {}
		explicit LineInfo(lines_iter iter) : LineInfo(0, 0, 0, iter) {}
		LineInfo(LineInfo const&) = default;
		LineInfo(size_type lineNumber,
		         size_type lineStart,
		         size_type lineEnd,
		         lines_iter iter);

		inline auto contains(size_type index) const noexcept { return index >= lineStart && index <= lineEnd; }
		inline auto containsStrict(size_type index) const noexcept { return index >= lineStart && index < lineEnd; }
	};

private:
	::Ptr<sona::StringPoolBase> pool;
	lines_type lines;
	LineInfo currentLineInfo;
	GapList<char_type> currentLine{};
	size_t length{};

	sona::String currentLineToString();
	void modifyCurrentLine(LineInfo info);
	void switchToLine(size_type line);
	LineInfo fakeNextLineInfo();

	inline auto outOfCurrentLine(size_type index) const { return !currentLineInfo.contains(index); }
	inline auto saveCurrentLine() { return lines[currentLineInfo.lineNumber] = currentLineToString(); }

public:
	const char_type separator;

	explicit LineSpan(char_type separator = '\n');

	char_type at(size_type index) const;
	void insert(size_type index, const char_type *charSequence, size_t sequenceSize);
	char_type remove(size_type index);
	LineInfo line_info_of(size_type index);
	LineInfo line_info_at(size_type line);
	void clear();

	inline auto const &get_current_line() const noexcept { return currentLine; }
	inline auto &get_current_line() noexcept { return currentLine; }
	inline auto current_line_index() const noexcept { return currentLineInfo.lineNumber; }
	inline auto line_at(size_type index) { return index == current_line_index() ? saveCurrentLine() : lines[index]; }
	inline auto line_of(size_type index) { return index == current_line_index() ? saveCurrentLine() : *line_info_of(index).iter; }
	inline auto switch_to_line(size_type line) { if (line != current_line_index()) switchToLine(line); }
	inline auto switch_to_line_of_index(size_type index) { if (!currentLineInfo.contains(index)) saveCurrentLine(), modifyCurrentLine(line_info_of(index)); }
	inline auto &iter_lines() { saveCurrentLine(); return lines; }
	inline auto const &citer_lines() { saveCurrentLine(); return lines; }
	inline auto &iter_lines_unsafe() const noexcept { return lines; }
	inline auto const &citer_lines_unsafe() const noexcept { return lines; }
	inline auto size() const noexcept { return length; }
	inline auto current_line_begin() const noexcept { return currentLineInfo.lineStart; }
	inline auto current_line_end() const noexcept { return currentLineInfo.lineEnd; }
	inline auto empty() const noexcept { return size() == 0; }
	inline auto line_count() const noexcept { return lines.size(); }
	inline auto line_size(size_type index) const { return index == current_line_index() ? currentLine.size() : lines[index].Size(); }
	inline auto insert(size_type index, char_type c) { insert(index, &c, 1); }
	inline auto append(char_type c) { insert(size(), c); }
	inline auto append(const char_type *str, size_type strSize) { insert(size(), str, strSize); }
	inline auto operator[](size_type index) const { return at(index); }
};

#endif //DEX_STAR_LINE_SPAN_HPP
