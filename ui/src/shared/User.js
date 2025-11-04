/**
 * Represents a user authenticated via Keycloak.
 *
 * This class models only the user information portion of a Keycloak ID token,
 * omitting session and cryptographic fields such as `exp`, `iat`, `jti`, etc.
 *
 * @class
 */
class User {
  /**
   * Create a new User instance.
   *
   * @param {Object} params - The user data.
   * @param {string} params.sub - The unique subject identifier for the user (UUID).
   * @param {string} params.name - The full name of the user.
   * @param {string} params.given_name - The user's given (first) name.
   * @param {string} params.family_name - The user's family (last) name.
   * @param {string} params.preferred_username - The username or email the user prefers to use for login.
   * @param {string} params.email - The user's email address.
   * @param {boolean} params.email_verified - Indicates if the user's email has been verified.
   */
  constructor({
    sub,
    name,
    given_name,
    family_name,
    preferred_username,
    email,
    email_verified,
  }) {
    /**
     * The unique user identifier assigned by Keycloak.
     * @type {string}
     */
    this.id = sub;

    /**
     * The user's full name.
     * @type {string}
     */
    this.name = name;

    /**
     * The user's given (first) name.
     * @type {string}
     */
    this.givenName = given_name;

    /**
     * The user's family (last) name.
     * @type {string}
     */
    this.familyName = family_name;

    /**
     * The preferred username (often the email address).
     * @type {string}
     */
    this.username = preferred_username;

    /**
     * The user's email address.
     * @type {string}
     */
    this.email = email;

    /**
     * Whether the user's email has been verified.
     * @type {boolean}
     */
    this.emailVerified = email_verified;
  }

  /**
   * Returns the user's full name in "Given Family" format.
   *
   * @returns {string} The formatted full name.
   */
  getFullName() {
    return `${this.givenName} ${this.familyName}`;
  }

  /**
   * Returns a simplified JSON representation of the user.
   *
   * @returns {Object} A plain object with user details.
   */
  toJSON() {
    return {
      id: this.id,
      name: this.name,
      givenName: this.givenName,
      familyName: this.familyName,
      username: this.username,
      email: this.email,
      emailVerified: this.emailVerified,
    };
  }
}

// Example usage:
// const user = new User(idToken);
// console.log(user.toJSON());

export default User;
