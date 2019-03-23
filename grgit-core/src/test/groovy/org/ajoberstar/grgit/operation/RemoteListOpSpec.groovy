package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Remote
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec

class RemoteListOpSpec extends MultiGitOpSpec {
  def 'will list all remotes'() {
    given:
    Grgit remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1'
    remoteGrgit.commit(message: 'do', all: true)

    Grgit localGrgit = clone('local', remoteGrgit)

    expect:
    localGrgit.remote.list() == [
      new Remote('origin', remoteGrgit.repository.rootDir.canonicalFile.toPath().toUri().toString(), null, ['+refs/heads/*:refs/remotes/origin/*'], [], false)
    ]
  }
}
