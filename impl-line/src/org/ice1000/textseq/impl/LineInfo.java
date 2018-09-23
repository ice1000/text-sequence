package org.ice1000.textseq.impl;

import java.util.Iterator;

/**
 * One line information for {@link LineSpan}
 *
 * @author ice1000
 * @since v0.4
 */
public class LineInfo {
	int lineNumber;
	int lineStart;
	int lineEnd;
	Iterator<CharSequence> lineIter;

	public LineInfo(int lineNumber, int lineStart, int lineEnd, Iterator<CharSequence> lineIter) {
		this.lineNumber = lineNumber;
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
		this.lineIter = lineIter;
	}
}
