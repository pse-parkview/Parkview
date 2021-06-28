/**
 * Encapsulates information regarding a commit
 */
interface Commit {

  /**
   * The date on which the commit was made
   */
  get date(): Date;

  /**
   * The commit message
   */
  get commitMessage(): string;

  /**
   * The commit author
   */
  get author(): string;

  /**
   * True if the commit has a benchmark associated with it
   */
  get hasBenchmark(): boolean;

  /**
   * The commit hash
   */
  get sha(): string;

  /**
   * The name of the branch the commit is on
   */
  get branch(): string;
}
