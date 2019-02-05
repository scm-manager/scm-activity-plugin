// @flow
import { apiClient } from "@scm-manager/ui-components";

export function findAll(link: string) {
  return apiClient.get(link).then(resp => resp.json());
}
