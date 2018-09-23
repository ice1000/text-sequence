//
// Created by ice1000 on 18-9-15.
//

#include "line_span.h"

using sona::String;
using sona::StringPool;
using textseq::LineSpan;
using char_type = LineSpan::char_type;
using size_type = LineSpan::size_type;
using LineInfo = LineSpan::LineInfo;

void LineSpan::modifyCurrentLine(LineInfo info) {
	currentLine.clear();
	auto *raw = info.iter->Raw();
	size_type size = info.iter->Size();
	for (size_type index = 0; index < size; ++index) currentLine.push_back(raw[index]);
	currentLineInfo = info;
}

void LineSpan::switchToLine(size_type line) {
	saveCurrentLine();
	auto &&info = line >= current_line_index() ? currentLineInfo : LineInfo(lines.begin());
	auto &&end = lines.end();
	for (; info.iter != end; ++info.iter, info.lineStart = info.lineEnd + 1, ++info.lineNumber) {
		info.lineEnd = info.lineStart + info.iter->Size();
		if (info.lineNumber == line) {
			modifyCurrentLine(info);
			break; // == return;
		}
	}
}

String LineSpan::currentLineToString() {
	currentLine.move_gap_to_end();
	char_type *rawData = currentLine.raw_data();
	auto size = currentLine.size();
	return {pool, rawData, rawData + size};
}

LineSpan::LineSpan(char_type separator
) : pool(new StringPool<>),
    lines(),
    currentLine(),
    separator(separator),
    currentLineInfo() {
	lines.push_back({pool, STRING_POOL_DEFAULT_STRING});
	currentLineInfo.iter = lines.begin();
}

char_type LineSpan::at(size_type index) const {
	assert(index < size());
	size_type start = 0;
	size_type i = 0;
	for (auto &&sequence : lines) {
		size_type iteratorSize = i != current_line_index() ? sequence.Size() : currentLine.size();
		size_type iteratorEnd = start + iteratorSize;
		if (index < iteratorEnd && index >= start) {
			size_type offset = index - start;
			return i == current_line_index() ? currentLine[offset] : sequence[offset];
		}
		if (index == iteratorEnd) return separator;
		start = iteratorEnd + 1, ++i;
	}
	assert(!"Index is too large");
	abort();
}

void LineSpan::insert(size_type index, const char_type *charSequence, size_type sequenceSize) {
	assert(index <= size());
	length += sequenceSize;
	switch_to_line_of_index(index);
	for (size_type i = 0; i < sequenceSize; ++i) {
		size_type indexInCurrentLine = index + i - current_line_begin();
		char_type c = charSequence[i];
		if (c == separator) {
			size_type newLineNumber = current_line_index() + 1;
			currentLine.move_gap_to_end();
			char_type *rawData = currentLine.raw_data();
			lines.insert_at(newLineNumber, {pool, rawData + indexInCurrentLine, rawData + currentLine.size()});
			while (currentLine.size() > indexInCurrentLine) currentLine.pop_back();
			currentLineInfo.lineEnd = current_line_begin() + indexInCurrentLine;
			assert(currentLine.size() == current_line_end() - current_line_begin());
			switch_to_line(newLineNumber);
		} else {
			currentLine.insert(indexInCurrentLine, c);
			currentLineInfo.lineEnd++;
		}
	}
}

char_type LineSpan::remove(size_type index) {
	assert(index < size());
	switch_to_line_of_index(index);
	length--;
	if (index == current_line_end()) {
		auto &&sequence = lines.citer_at(current_line_index() + 1);
		auto sequenceSize = sequence->Size();
		currentLine.append(sequence->Raw(), sequenceSize);
		currentLineInfo.lineEnd += sequenceSize;
		lines.erase(sequence);
		return separator;
	} else {
		auto ret = *currentLine.erase_at(index - current_line_begin());
		currentLineInfo.lineEnd--;
		return ret;
	}
}

LineInfo LineSpan::line_info_of(size_type index) {
	if (currentLineInfo.contains(index)) return currentLineInfo;
	assert(index < size());
	// must be larger than current line end or smaller than current line start
	auto info = index > current_line_end() ? fakeNextLineInfo() : LineInfo(lines.begin());
	auto &&end = lines.end();
	for (; info.iter != end; ++info.iter, ++info.lineNumber, info.lineStart = info.lineEnd + 1) {
		info.lineEnd = info.lineStart + info.iter->Size();
		if (index <= info.lineEnd) return info;
	}
	assert(!"Index too large");
	abort();
}

void LineSpan::clear() {
	lines.clear();
	lines.push_back({pool, STRING_POOL_DEFAULT_STRING});
	currentLineInfo = LineInfo(lines.begin());
	length = 0;
	currentLine.clear();
	delete pool;
	pool = new sona::StringPool<>;
}

LineInfo LineSpan::line_info_at(size_type line) {
	// TODO optimize with `segtree`
	if (current_line_index() == line) return currentLineInfo;
	assert(line < line_count());
	auto &&info = line > current_line_index() ? fakeNextLineInfo() : LineInfo(lines.begin());
	auto &&end = lines.end();
	for (; info.iter != end; ++info.iter, ++info.lineNumber) {
		info.lineEnd = info.lineStart + info.iter->Size();
		if (line <= info.lineNumber) return info;
		info.lineStart = info.lineEnd + 1;
	}
	assert(!"Index too large");
	abort();
}

LineInfo LineSpan::fakeNextLineInfo() {
	auto info = currentLineInfo;
	info.lineStart = info.lineEnd + 1;
	++info.lineNumber;
	++info.iter;
	return info;
}

LineInfo::LineInfo(size_type lineNumber,
                   size_type lineStart,
                   size_type lineEnd,
                   lines_iter iter
) : lineNumber(lineNumber), lineStart(lineStart), lineEnd(lineEnd), iter(iter) {}
