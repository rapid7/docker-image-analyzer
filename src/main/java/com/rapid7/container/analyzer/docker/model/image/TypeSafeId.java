package com.rapid7.container.analyzer.docker.model.image;

/**
 * Base abstraction for a type-safe identifier. Type-safe identifiers can be used to ensure that primitive identifiers
 * for an object are type-safe and do not accidently get misused with other objects. Type-safe identifiers are immutable
 * and cannot be changed once constructed.
 *
 * In order to create a type-safe identifier, this class must be extended and that type used in place of the traditional
 * identifier.
 *
 *
 * @param <T> The type of the identifier.
 */
public abstract class TypeSafeId<T> {

  /**
   * The identifier, this is not final as child classes may have mutable IDs and being final is not compatible with
   * JAXB marshaling and unmarshaling.
   */
  protected T id;

  /**
   * Returns the identifier.
   *
   * @return The identifier. Will never be {@code null}.
   */
  public T getId() {
    return id;
  }

  /**
   * Constructs an identifier with the given value.
   *
   * @param id The identifier. Must not be {@code null}.
   */
  protected TypeSafeId(T id) {
    if (id == null)
      throw new IllegalArgumentException("The identifier must not be null.");

    this.id = id;
  }

  /**
   * Default constructor, allowing reflective construction.
   */
  protected TypeSafeId() {

  }

  /////////////////////////////////////////////////////////////////////////
  // Object overrides
  /////////////////////////////////////////////////////////////////////////

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;

    if (!(obj instanceof TypeSafeId))
      return false;

    Object objId = ((TypeSafeId<?>)obj).getId();
    return objId != null && objId.equals(getId());
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return id.toString();
  }
}
