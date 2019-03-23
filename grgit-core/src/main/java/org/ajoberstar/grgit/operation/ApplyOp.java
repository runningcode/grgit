package org.ajoberstar.grgit.operation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import org.ajoberstar.grgit.Repository;
import org.ajoberstar.grgit.util.CoercionUtil;
import org.ajoberstar.grgit.util.Throwing;
import org.eclipse.jgit.api.ApplyCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Apply a patch to the index.
 *
 * @see <a href="http://ajoberstar.org/grgit/grgit-apply.html">grgit-apply</a>
 * @see <a href="http://git-scm.com/docs/git-apply">git-apply Manual Page</a>
 * @since 0.1.0
 */
public class ApplyOp implements Callable<Void> {
  private final Repository repo;
  private File patch;

  public ApplyOp(Repository repo) {
    this.repo = repo;
  }

  public Void call() {
    ApplyCommand cmd = repo.getJgit().apply();
    if (patch == null) {
      throw new IllegalStateException("Must set a patch file.");
    }

    try (InputStream patchStream = Files.newInputStream(getPatch().toPath())) {
      cmd.setPatch(patchStream);
      cmd.call();
      return null;
    } catch (IOException | GitAPIException e) {
      Throwing.sneakyThrow(e);
      return null;
    }
  }

  public File getPatch() {
    return patch;
  }

  /**
   * The patch file to apply to the index.
   *
   * @see {@link CoercionUtil#toFile(Object)}
   */
  public void setPatch(Object patch) {
    this.patch = CoercionUtil.toFile(patch);
  }
}
