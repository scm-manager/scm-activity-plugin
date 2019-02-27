// @flow

import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import { ProtectedRoute } from "@scm-manager/ui-components";
import type { Links } from "@scm-manager/ui-types";
import Activity from "./Activity";
import ActivityNavigation from "./ActivityNavigation";

const predicate = (props: Object) => {
  return props.links && props.links.activity;
};

type Props = {
  authenticated?: boolean,
  links: Links
};

class ActivityRoute extends React.Component<Props> {
  constructor(props: Props) {
    super(props);
  }

  renderActivity = () => {
    const { links } = this.props;
    return <Activity activityUrl={links.activity.href} />;
  };

  render() {
    const { authenticated, links } = this.props;

    return (
      <ProtectedRoute
        path="/activity"
        component={this.renderActivity}
        authenticated={authenticated && links.activity.href}
      />
    );
  }
}

binder.bind("main.route", ActivityRoute);

binder.bind("primary-navigation.first-menu", ActivityNavigation, predicate);

binder.bind("main.redirect", () => "/activity");
