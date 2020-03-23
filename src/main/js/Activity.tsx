/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import React from "react";
import { withRouter } from "react-router-dom";
import { withTranslation, WithTranslation } from "react-i18next";
import { Changeset, Repository } from "@scm-manager/ui-types";
import { Loading, Notification, Page } from "@scm-manager/ui-components";
import { findAll } from "./api";
import { Activities, ActivityGroup } from "./ActivityGroup";
import ActivityGroupEntry from "./ActivityGroupEntry";

type Props = WithTranslation & {
  activityUrl: string;
};

type State = {
  loading: boolean;
  error?: string;
  activities: Activities;
};

class Activity extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
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
    const result: ActivityGroup[] = [];
    const groups = [];
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
            type: activity.repositoryType
          };
          const changesets: Changeset[] = [];
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
    const { activities } = this.state;
    if (activities && activities.activities && activities.activities.length > 0) {
      return this.groupByRepo(activities).map(group => {
        return <ActivityGroupEntry group={group} />;
      });
    } else {
      return <Notification>{t("scm-activity-plugin.notification.empty-list")}</Notification>;
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

export default withRouter(withTranslation("plugins")(Activity));
