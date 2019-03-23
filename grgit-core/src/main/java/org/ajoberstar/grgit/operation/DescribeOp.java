package org.ajoberstar.grgit.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.Throwing;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.eclipse.jgit.api.DescribeCommand;

/**
 * Find the nearest tag reachable. Returns an {@link String}}.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-describe.html">grgit-describe</a>
 * @see <a href="http://git-scm.com/docs/git-describe">git-describe Manual Page</a>
 */
public class DescribeOp implements Callable<String> {
  private final Repository repo;

  private Object commit;
  private boolean longDescr;
  private boolean tags;
  private List<String> match = new ArrayList<String>();

  public DescribeOp(Repository repo) {
    this.repo = repo;
  }

  public String call() {
    try {
      DescribeCommand cmd = repo.getJgit().describe();
      if (commit != null) {
        cmd.setTarget(new ResolveService(repo).toRevisionString(commit));
      }

      cmd.setLong(longDescr);
      cmd.setTags(tags);
      if (DefaultGroovyMethods.asBoolean(match)) {
        cmd.setMatch(match.toArray(new String[match.size()]));
      }

      return cmd.call();
    } catch (Exception e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Object getCommit() {
    return commit;
  }

  /**
   * Sets the commit to be described. Defaults to HEAD.
   *
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setCommit(Object commit) {
    this.commit = commit;
  }

  public boolean isLongDescr() {
    return longDescr;
  }

  /**
   * Whether to always use long output format or not.
   */
  public void setLongDescr(boolean longDescr) {
    this.longDescr = longDescr;
  }

  public boolean isTags() {
    return tags;
  }

  /**
   * Include non-annotated tags when determining nearest tag.
   */
  public void setTags(boolean tags) {
    this.tags = tags;
  }

  public List<String> getMatch() {
    return match;
  }

  /**
   * glob patterns to match tags against before they are considered
   */
  public void setMatch(List<String> match) {
    this.match = match;
  }
}
