# Text Sequence

Paper: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.48.1265&rep=rep1&type=pdf

Windows|Linux
:---:|:---:
[![AV][w-l]][w-i]|[![CircleCI][l-l]][l-i]

  [w-l]: https://ci.appveyor.com/api/projects/status/rfk89093smhsv5rf?svg=true
  [w-i]: https://ci.appveyor.com/project/ice1000/text-sequence
  [l-l]: https://circleci.com/gh/ice1000/text-sequence.svg?style=svg
  [l-i]: https://circleci.com/gh/ice1000/text-sequence

# Progress

+ [X] Trivial implementations (using `ArrayList`, `LinkedList`), say, **the linked list method**
+ [X] `StringBuilder` implementation, say, **the array method**, [DevKt][devkt]
+ [ ] `GapBuffer` implementation, say, **the gap method**, [Emacs][emacs], [Scintilla][scintilla], [Java Swing][swing]
+ [ ] `LineSpan` implementation, say, **the line span method**, [Hemlock][hemlock]
+ [ ] `SizedBuffer` implementation, say, **fixed size buffer**
+ [ ] `PieceTable` implementation, say, **the piece table method**, [VSCode][code], [AbiWord][abiw]

  [devkt]: https://github.com/ice1000/dev-kt
  [emacs]: https://www.gnu.org/software/emacs/
  [scintilla]: https://www.scintilla.org/
  [swing]: https://docs.oracle.com/javase/7/docs/api/javax/swing/text/GapContent.html
  [hemlock]: https://www.cons.org/cmucl/hemlock/
  [code]: https://code.visualstudio.com/
  [abiw]: https://www.abisource.com/
