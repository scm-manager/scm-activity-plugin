//@flow

import type { Changeset, Repository } from "@scm-manager/ui-types";

export type ActivityGroup = {
  repository: Repository,
  changesets: Changeset[]
};

export type Activities = {
  activities: Activity[]
};

export type Activity = {
  changeset: Changeset,
  repositoryId: string,
  repositoryName: string,
  repositoryNamespace: string,
  repositoryType: string
};
