package org.ajoberstar.grgit.operation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ajoberstar.grgit.Remote;
import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.util.JGitUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

/**
 * Adds a remote to the repository. Returns the newly created {@link Remote}. If remote with given
 * name already exists, this command will fail.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-remote.html">grgit-remote</a>
 * @see <a href="http://git-scm.com/docs/git-remote">git-remote Manual Page</a>
 */
public class RemoteAddOp implements Callable<Remote> {
  private final Repository repository;

  private String name;
  private String url;
  private String pushUrl;
  private List<String> fetchRefSpecs = new ArrayList();
  private List<String> pushRefSpecs = new ArrayList();
  private boolean mirror;

  public RemoteAddOp(Repository repo) {
    this.repository = repo;
  }

  @Override
  public Remote call() {
    try {
      Config config = repository.getJgit().getRepository().getConfig();
      if (RemoteConfig.getAllRemoteConfigs(config).stream().anyMatch(getName()::equals)) {
        throw new IllegalStateException("Remote " + getName() + " already exists.");
      }
      RemoteConfig remote = new RemoteConfig(config, name);

      if (url != null) {
        remote.addURI(new URIish(url));
      }
      if (pushUrl != null) {
        remote.addPushURI(new URIish(pushUrl));
      }

      remote.setFetchRefSpecs(getFetchRefSpecs().stream().map(RefSpec::new).collect(Collectors.toList()));
      remote.setPushRefSpecs(getPushRefSpecs().stream().map(RefSpec::new).collect(Collectors.toList()));
      remote.setMirror(mirror);
      remote.update(config);
      ((StoredConfig) config).save();
      return JGitUtil.convertRemote(remote);
    } catch (URISyntaxException | IOException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public String getName() {
    return name;
  }

  /**
   * Name of the remote.
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  /**
   * URL to fetch from.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  public String getPushUrl() {
    return pushUrl;
  }

  /**
   * URL to push to.
   */
  public void setPushUrl(String pushUrl) {
    this.pushUrl = pushUrl;
  }

  public List<String> getFetchRefSpecs() {
    return Optional.ofNullable(fetchRefSpecs).filter(x -> !x.isEmpty()).orElse(Arrays.asList("+refs/heads/*:refs/remotes/" + getName() + "/*"));
  }

  /**
   * Specs to fetch from the remote.
   */
  public void setFetchRefSpecs(List fetchRefSpecs) {
    this.fetchRefSpecs = fetchRefSpecs;
  }

  public List<String> getPushRefSpecs() {
    return Optional.ofNullable(pushRefSpecs).orElse(Collections.emptyList());
  }

  /**
   * Specs to push to the remote.
   */
  public void setPushRefSpecs(List pushRefSpecs) {
    this.pushRefSpecs = pushRefSpecs;
  }

  public boolean isMirror() {
    return mirror;
  }

  /**
   * Whether or not pushes will mirror the repository.
   */
  public void setMirror(boolean mirror) {
    this.mirror = mirror;
  }
}
