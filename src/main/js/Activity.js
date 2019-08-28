//@flow
import React from "react";
import { translate } from "react-i18next";
import { withRouter } from "react-router-dom";
import {
  ErrorNotification,
  Loading,
  Notification,
  Page
} from "@scm-manager/ui-components";
import { findAll } from "./api";
import type { Activities, ActivityGroup } from "./ActivityGroup";
import type { Changeset, Repository } from "@scm-manager/ui-types";
import ActivityGroupEntry from "./ActivityGroupEntry";

type Props = {
  activityUrl: any,
  // context prop
  t: string => string
};

type State = {
  loading: boolean,
  error?: string,
  activities: Activities
};

class Activity extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { loading: true };
  }

  componentDidMount(): void {
    const { activityUrl } = this.props;
    if (activityUrl) {
      findAll(activityUrl)
        .then(activities => {
          this.setState({
            loading: false,
            activities
          });
        })
        .catch(error => {
          this.setState({
            loading: false,
            error
          });
        });
    }
  }

  groupByRepo(activities: Activities): ActivityGroup[] {
    let result: ActivityGroup[] = [];
    let groups = [];
    let lastGroupName = "";
    if (activities && activities.activities) {
      for (let activity of activities.activities) {
        const groupName =
          activity.repositoryNamespace + "/" + activity.repositoryName;
        let group = groups[groupName];
        if (groupName !== lastGroupName) {
          lastGroupName = groupName;
          let repository: Repository = {
            namespace: activity.repositoryNamespace,
            name: activity.repositoryName,
            type: activity.repositoryType
          };
          let changesets: Changeset[] = [];
          group = {
            repository,
            changesets
          };
          groups[groupName] = group;
          result.push(group);
        }
        group.changesets.push(activity._embedded.changeset);
      }
    }
    return result;
  }

  getBody() {
    const { t } = this.props;
    const { activities, error } = this.state;
    if (
      activities &&
      activities.activities &&
      activities.activities.length > 0
    ) {
      return this.groupByRepo(activities).map(group => {
        return <ActivityGroupEntry group={group} />;
      });
    } else if (error) {
      return "";
    } else {
      return (
        <Notification>
          {t("scm-activity-plugin.notification.empty-list")}
        </Notification>
      );
    }
  }

  render() {
    const { t } = this.props;
    const { loading, error } = this.state;

    if (loading) {
      return <Loading />;
    }

    return (
      <Page
        title={t("scm-activity-plugin.root-page.title")}
        subtitle={t("scm-activity-plugin.root-page.subtitle")}
        error={error}
      >
        {this.getBody()}
      </Page>
    );
  }
}

export default withRouter(translate("plugins")(Activity));