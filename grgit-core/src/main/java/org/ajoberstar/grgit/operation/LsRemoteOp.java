package org.ajoberstar.grgit.operation;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ajoberstar.grgit.Ref;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.auth.TransportOpUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;

/**
 * List references in a remote repository.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-lsremote.html">grgit-lsremote</a>
 * @see <a href="https://git-scm.com/docs/git-ls-remote">git-ls-remote Manual Page</a>
 * @since 2.0.0
 */
public class LsRemoteOp implements Callable<Map<Ref, String>> {
  private final Repository repo;

  private String remote = "origin";
  private boolean heads = false;
  private boolean tags = false;

  public LsRemoteOp(Repository repo) {
    this.repo = repo;
  }

  public Map<Ref, String> call() {
    LsRemoteCommand cmd = repo.getJgit().lsRemote();
    TransportOpUtil.configure(cmd, repo.getCredentials());
    cmd.setRemote(remote);
    cmd.setHeads(heads);
    cmd.setTags(tags);
    try {
      return cmd.call().stream()
          .collect(Collectors.toMap(jgitRef -> new Ref(((org.eclipse.jgit.lib.Ref) jgitRef).getName()), jgitRef -> ObjectId.toString(((org.eclipse.jgit.lib.Ref) jgitRef).getObjectId())));
    } catch (GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public String getRemote() {
    return remote;
  }

  public void setRemote(String remote) {
    this.remote = remote;
  }

  public boolean getHeads() {
    return heads;
  }

  public boolean isHeads() {
    return heads;
  }

  public void setHeads(boolean heads) {
    this.heads = heads;
  }

  public boolean getTags() {
    return tags;
  }

  public boolean isTags() {
    return tags;
  }

  public void setTags(boolean tags) {
    this.tags = tags;
  }
}
