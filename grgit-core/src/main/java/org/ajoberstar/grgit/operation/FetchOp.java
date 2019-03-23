package org.ajoberstar.grgit.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.auth.TransportOpUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;

/**
 * Fetch changes from remotes.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-fetch.html">grgit-fetch</a>
 * @see <a href="http://git-scm.com/docs/git-fetch">git-fetch Manual Reference.</a>
 * @since 0.2.0
 */
public class FetchOp implements Callable<Void> {
  private final Repository repo;

  private String remote;
  private List<Object> refSpecs = new ArrayList();
  private boolean prune = false;
  private TagMode tagMode = TagMode.AUTO;

  public FetchOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    FetchCommand cmd = repo.getJgit().fetch();
    TransportOpUtil.configure(cmd, repo.getCredentials());
    if (remote != null) {
      cmd.setRemote(remote);
    }
    cmd.setRefSpecs(refSpecs.stream().map(Object::toString).map(RefSpec::new).collect(Collectors.toList()));
    cmd.setRemoveDeletedRefs(prune);
    cmd.setTagOpt(tagMode.getJgit());

    try {
      cmd.call();
      return null;
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public String getRemote() {
    return remote;
  }

  /**
   * Which remote should be fetched.
   */
  public void setRemote(String remote) {
    this.remote = remote;
  }

  public List getRefSpecs() {
    return refSpecs;
  }

  /**
   * List of refspecs to fetch.
   */
  public void setRefSpecs(List refSpecs) {
    this.refSpecs = refSpecs;
  }

  public boolean isPrune() {
    return prune;
  }

  /**
   * {@code true} if branches removed by the remote should be removed locally.
   */
  public void setPrune(boolean prune) {
    this.prune = prune;
  }

  public TagMode getTagMode() {
    return tagMode;
  }

  /**
   * Provides a string conversion to the enums.
   */
  public void setTagMode(String tagMode) {
    this.tagMode = TagMode.valueOf(tagMode.toUpperCase());
  }

  public static enum TagMode {
    AUTO(TagOpt.AUTO_FOLLOW), ALL(TagOpt.FETCH_TAGS), NONE(TagOpt.NO_TAGS);

    private TagMode(TagOpt opt) {
      this.jgit = opt;
    }

    public final TagOpt getJgit() {
      return jgit;
    }

    private final TagOpt jgit;
  }
}
