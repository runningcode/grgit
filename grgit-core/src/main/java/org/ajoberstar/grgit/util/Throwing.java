package org.ajoberstar.grgit.util;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Throwing {
  private Throwing() {
    // don't instantiate
  }

  public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
    throw (E) e;
  }

  public static <T, R> Function<T, R> function(ThrowingFunction<T, R> fn) {
    return fn;
  }

  public static <T> Consumer<T> consumer(ThrowingConsumer<T> fn) {
    return fn;
  }

  public static interface ThrowingFunction<T, R> extends Function<T, R> {
    default R apply(T arg) {
      try {
        return applyThrows(arg);
      } catch (Exception e) {
        sneakyThrow(e);
        return null;
      }
    }

    R applyThrows(T arg) throws Exception;
  }

  public static interface ThrowingConsumer<T> extends Consumer<T> {
    default void accept(T arg) {
      try {
        acceptThrows(arg);
      } catch (Exception e) {
        sneakyThrow(e);
      }
    }

    void acceptThrows(T arg) throws Exception;
  }
}
