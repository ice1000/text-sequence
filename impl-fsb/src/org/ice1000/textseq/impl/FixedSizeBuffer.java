package org.ice1000.textseq.impl;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * @author ice1000
 */
public class FixedSizeBuffer {
	private @NotNull LinkedList<@NotNull GapBuffer> buffers;

	private FixedSizeBuffer(@NotNull LinkedList<@NotNull GapBuffer> buffers) {
		this.buffers = buffers;
	}

	public FixedSizeBuffer() {
		this(new LinkedList<>());
	}
}
