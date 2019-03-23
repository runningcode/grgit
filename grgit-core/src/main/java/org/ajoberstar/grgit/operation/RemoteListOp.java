package org.ajoberstar.grgit.operation;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ajoberstar.grgit.Remote;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.transport.RemoteConfig;

/**
 * Lists remotes in the repository. Returns a list of {@link Remote}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-remote.html">grgit-remote</a>
 * @see <a href="http://git-scm.com/docs/git-remote">git-remote Manual Page</a>
 */
public class RemoteListOp implements Callable<List<Remote>> {
  private final Repository repository;

  public RemoteListOp(Repository repo) {
    this.repository = repo;
  }

  @Override
  public List<Remote> call() {
    try {
      return RemoteConfig.getAllRemoteConfigs(repository.getJgit().getRepository().getConfig()).stream()
          .map(rc -> {
            if (rc.getURIs().size() > 1 || rc.getPushURIs().size() > 1) {
              throw new IllegalArgumentException("Grgit does not currently support multiple URLs in remote: [uris: " + String.valueOf(rc.getURIs()) + ", pushURIs:" + String.valueOf(rc.getPushURIs()) + "]");
            }
            return JGitUtil.convertRemote(rc);
          }).collect(Collectors.toList());
    } catch (URISyntaxException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }
}
