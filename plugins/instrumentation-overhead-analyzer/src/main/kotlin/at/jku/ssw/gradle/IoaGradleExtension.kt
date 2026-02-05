package at.jku.ssw.gradle

import at.jku.ssw.shared.IoaKind

class IoaGradleExtension() {
  var enabled: Boolean = true
  var kind: IoaKind = IoaKind.None
}