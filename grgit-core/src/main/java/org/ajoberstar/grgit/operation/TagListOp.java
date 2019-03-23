package org.ajoberstar.grgit.operation;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.Tag;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Lists tags in the repository. Returns a list of {@link Tag}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-tag.html">grgit-tag</a>
 * @see <a href="http://git-scm.com/docs/git-tag">git-tag Manual Page</a>
 * @since 0.2.0
 */
public class TagListOp implements Callable<List<Tag>> {
  private final Repository repo;

  public TagListOp(Repository repo) {
    this.repo = repo;
  }

  public List<Tag> call() {
    ListTagCommand cmd = repo.getJgit().tagList();

    try {
      return cmd.call().stream()
          .map(ref -> JGitUtil.resolveTag(repo, ref))
          .collect(Collectors.toList());
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }
}
