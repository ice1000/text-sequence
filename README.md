# Text Sequence

[Read this paper][paper0]

Windows|Linux|Coverage|Bintray
 :---: |:---:| :---:  | :---:
[![][w-l]][w-i]|[![][l-l]][l-i]|[![][c-i]][c-l]|[![][b-i]][b-l]

  [paper0]: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.48.1265&rep=rep1&type=pdf
  [w-l]: https://ci.appveyor.com/api/projects/status/rfk89093smhsv5rf?svg=true
  [w-i]: https://ci.appveyor.com/project/ice1000/text-sequence
  [l-l]: https://circleci.com/gh/ice1000/text-sequence.svg?style=svg
  [l-i]: https://circleci.com/gh/ice1000/text-sequence
  [c-l]: https://codecov.io/gh/ice1000/text-sequence
  [c-i]: https://codecov.io/gh/ice1000/text-sequence/branch/master/graph/badge.svg
  [b-l]: https://bintray.com/ice1000/ice1000/textseq
  [b-i]: https://img.shields.io/bintray/v/ice1000/ice1000/textseq.svg

# Architecture

+ Interface class `org.ice1000.textseq.TextSequence` in common
+ Trivial implementations in common
+ Implementations in their own subproject
+ Standalone `GapList<T>` (with no dependencies) that uses a gap buffer to maintain elements, which is more efficient than `ArrayList<T>`.
  + C++ version (C++17 with dependency on `cstddef`, `cassert` and `cstring`)

# Build

```groovy
repositories { jcenter() }
dependencies {
  String textseq_version = "v0.3"
  compile "org.ice1000.textseq:gap-list:$textseq_version" // standalone GapList impl
  compile "org.ice1000.textseq:common:$textseq_version" // common + trivial impl
  compile "org.ice1000.textseq:impl-builder:$textseq_version" // array method, depends on common
  compile "org.ice1000.textseq:impl-gap:$textseq_version" // gap method, depends on common
  compile "org.ice1000.textseq:impl-line:$textseq_version" // line span method, depends on impl-gap
}
```

# Progress

+ [X] Trivial implementations (using `ArrayList`, `LinkedList`), say, **the linked list method**
+ [X] `StringBuilderTextSequence` implementation, say, **the array method**, [DevKt][devkt]
+ [X] `GapBuffer` implementation, say, **the gap method**, [Emacs][emacs], [Scintilla][scintilla], [Java Swing][swing]
+ [X] `LineSpan` implementation, say, **the line span method**, [Hemlock][hemlock]
  + The active line is a gap buffer, other lines are simple string, discussed in [this paper][paper1]
+ [ ] `FixedSizeBuffer` implementation, say, **fixed size buffer**
+ [ ] `PieceTable` implementation, say, **the piece table method**, [VSCode][code], [AbiWord][abiw]
+ [ ] `Rope` (aka Balanced Tree), [Vim][vim]

  [devkt]: https://github.com/ice1000/dev-kt
  [emacs]: https://www.gnu.org/software/emacs/
  [scintilla]: https://www.scintilla.org/
  [swing]: https://docs.oracle.com/javase/7/docs/api/javax/swing/text/GapContent.html
  [hemlock]: https://www.cons.org/cmucl/hemlock/
  [code]: https://code.visualstudio.com/
  [abiw]: https://www.abisource.com/
  [vim]: https://www.vim.org/
  [paper1]: https://www.common-lisp.net/project/flexichain/download/StrandhVilleneuveMoore.pdf
