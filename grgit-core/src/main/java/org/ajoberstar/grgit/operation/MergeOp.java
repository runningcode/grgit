package org.ajoberstar.grgit.operation;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.service.ResolveService;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

/**
 * Merges changes from a single head. This is a simplified version of merge. If any conflict occurs
 * the merge will throw an exception. The conflicting files can be identified with
 * {@code grgit.status()}.
 *
 * <p>
 * Merge another head into the current branch.
 * </p>
 *
 * <pre>
 * grgit.merge(head: 'some-branch')
 * </pre>
 *
 * <p>
 * Merge with another mode.
 * </p>
 *
 * <pre>
 * grgit.merge(mode: MergeOp.Mode.ONLY_FF)
 * </pre>
 * <p>
 * See <a href="http://git-scm.com/docs/git-merge">git-merge Manual Page</a>.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-merge.html">grgit-merge</a>
 * @see <a href="http://git-scm.com/docs/git-merge">git-merge Manual Page</a>
 * @since 0.2.0
 */
public class MergeOp implements Callable<Void> {
  private final Repository repo;

  private Object head;
  private String message;
  private Mode mode;

  public MergeOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    try {
      MergeCommand cmd = repo.getJgit().merge();
      if (head != null) {
        /*
         * we want to preserve ref name in merge commit msg. if it's a ref, don't resolve down to commit id
         */
        Ref ref = repo.getJgit().getRepository().findRef(head.toString());
        if (ref == null) {
          String revstr = new ResolveService(repo).toRevisionString(head);
          cmd.include(JGitUtil.resolveObject(repo, revstr));
        } else {
          cmd.include(ref);
        }
      }

      if (message != null) {
        cmd.setMessage(message);
      }

      switch (getMode()) {
        case ONLY_FF:
          cmd.setFastForward(MergeCommand.FastForwardMode.FF_ONLY);
          break;
        case CREATE_COMMIT:
          cmd.setFastForward(MergeCommand.FastForwardMode.NO_FF);
          break;
        case SQUASH:
          cmd.setSquash(true);
          break;
        case NO_COMMIT:
          cmd.setCommit(false);
          break;
      }
      MergeResult result = cmd.call();
      if (!result.getMergeStatus().isSuccessful()) {
        throw new IllegalStateException("Could not merge (conflicting files can be retrieved with a call to grgit.status()): " + String.valueOf(result));
      }
      return null;
    } catch (GitAPIException | IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public Object getHead() {
    return head;
  }

  /**
   * The head to merge into the current HEAD.
   *
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  public void setHead(Object head) {
    this.head = head;
  }

  public String getMessage() {
    return message;
  }

  /**
   * The message to use for the merge commit
   */
  public void setMessage(String message) {
    this.message = message;
  }

  public Mode getMode() {
    return Optional.ofNullable(mode).orElse(Mode.DEFAULT);
  }

  /**
   * How to handle the merge.
   */
  public void setMode(String mode) {
    this.mode = Mode.valueOf(mode.toUpperCase().replace("-", "_"));
  }

  public enum Mode {
    DEFAULT,
    /**
     * Only merges if a fast-forward is possible. Behaves like --ff-only.
     */
    ONLY_FF,
    /**
     * Always creates a merge commit (even if a fast-forward is possible). Behaves like --no-ff.
     */
    CREATE_COMMIT,
    /**
     * Squashes the merged changes into one set and leaves them uncommitted. Behaves like --squash.
     */
    SQUASH,
    /**
     * Merges changes, but does not commit them. Behaves like --no-commit.
     */
    NO_COMMIT;
  }
}
