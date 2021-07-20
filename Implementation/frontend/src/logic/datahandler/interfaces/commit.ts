/**
 * Encapsulates information regarding a commit
 */
export interface Commit {

  /**
   * The date on which the commit was made
   */
  get date(): Date;

  /**
   * The commit message
   */
  get message(): string;

  /**
   * The commit author
   */
  get author(): string;

  /**
   * The commit hash
   */
  get sha(): string;

  /**
   * The name of the branch the commit is on
   */
  get branch(): string;

  /**
   * Available devices?
   */
  get availableDevices(): { name: string }[];
}
