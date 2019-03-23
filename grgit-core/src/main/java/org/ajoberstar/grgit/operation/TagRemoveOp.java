package org.ajoberstar.grgit.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.DeleteTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Removes one or more tags from the repository. Returns a list of the fully qualified tag names
 * that were removed.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-tag.html">grgit-tag</a>
 * @see <a href="http://git-scm.com/docs/git-tag">git-tag Manual Page</a>
 * @since 0.2.0
 */
public class TagRemoveOp implements Callable<List<String>> {
  private final Repository repo;

  private List<String> names = new ArrayList();

  public TagRemoveOp(Repository repo) {
    this.repo = repo;
  }

  public List<String> call() {
    try {
      DeleteTagCommand cmd = repo.getJgit().tagDelete();
      cmd.setTags(names.stream().<String>map(name -> new ResolveService(repo).toTagName(name)).toArray(String[]::new));
      return cmd.call();
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public List getNames() {
    return names;
  }

  /**
   * Names of tags to remove.
   *
   * @see {@link ResolveService#toTagName(Object)}
   */
  public void setNames(List names) {
    this.names = names;
  }
}
