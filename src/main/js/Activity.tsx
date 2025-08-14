/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Changeset, Repository } from "@scm-manager/ui-types";
import { Page } from "@scm-manager/ui-components";
import { Loading, Notification, useDocumentTitle } from "@scm-manager/ui-core";
import { findAll } from "./api";
import { Activities, ActivityGroup } from "./ActivityGroup";
import ActivityGroupEntry from "./ActivityGroupEntry";

type Props = {
  activityUrl?: string;
};

const Activity: React.FC<Props> = ({ activityUrl }) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | undefined>(undefined);
  const [activities, setActivities] = useState<Activities | undefined>(undefined);
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-activity-plugin.root-page.title"));

  useEffect(() => {
    if (activityUrl) {
      findAll(activityUrl)
        .then((activities) => {
          setLoading(false);
          setActivities(activities);
        })
        .catch((error) => {
          setLoading(false);
          setError(error);
        });
    }
  }, [activityUrl]);

  const groupByRepo = (activities: Activities): ActivityGroup[] => {
    const result: ActivityGroup[] = [];
    const groups: { [key: string]: ActivityGroup } = {};
    let lastGroupName = "";
    if (activities && activities.activities) {
      for (const activity of activities.activities) {
        const groupName = activity.repositoryNamespace + "/" + activity.repositoryName;
        let group = groups[groupName];
        if (groupName !== lastGroupName) {
          lastGroupName = groupName;
          const repository: Repository = {
            namespace: activity.repositoryNamespace,
            name: activity.repositoryName,
            type: activity.repositoryType,
          } as Repository;
          const changesets: Changeset[] = [];
          group = {
            repository,
            changesets,
          };
          groups[groupName] = group;
          result.push(group);
        }
        if (activity._embedded) {
          group.changesets.push(activity._embedded.changeset);
        }
      }
    }
    return result;
  };

  const getBody = () => {
    if (activities && activities.activities && activities.activities.length > 0) {
      return groupByRepo(activities).map((group) => {
        return <ActivityGroupEntry key={group.repository.namespace + "/" + group.repository.name} group={group} />;
      });
    } else {
      return <Notification>{t("scm-activity-plugin.notification.empty-list")}</Notification>;
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <Page
      title={t("scm-activity-plugin.root-page.title")}
      subtitle={t("scm-activity-plugin.root-page.subtitle")}
      error={error}
    >
      {getBody()}
    </Page>
  );
};

export default Activity;
