package org.ajoberstar.grgit.util;

import java.io.File;
import java.nio.file.Path;

public final class CoercionUtil {
  private CoercionUtil() {
    // don't instantiate
  }

  public static File toFile(Object obj) {
    if (obj instanceof File) {
      return ((File) (obj));
    } else if (obj instanceof Path) {
      return ((Path) obj).toFile();
    } else {
      return new File(obj.toString());
    }
  }
}
