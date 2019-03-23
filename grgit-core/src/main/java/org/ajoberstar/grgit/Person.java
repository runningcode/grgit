package org.ajoberstar.grgit;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A person.
 *
 * @since 0.1.0
 */
public final class Person {
  private final String name;
  private final String email;

  public Person(String name, String email) {
    this.name = name;
    this.email = email;
  }

  /**
   * Name of person.
   */
  public String getName() {
    return name;
  }

  /**
   * Email address of person.
   */
  public String getEmail() {
    return email;
  }


  @Override
  public boolean equals(Object that) {
    return EqualsBuilder.reflectionEquals(this, that);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
