/**
 * Encapsulates information regarding a commit
 */
import {Device} from "./device";

export interface Commit {

  /**
   * The date on which the commit was made
   */
  get date(): string;

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
   * Available devices?
   */
  get availableDevices(): Device[];
}
