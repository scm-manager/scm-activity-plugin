// @flow

import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import { ProtectedRoute } from "@scm-manager/ui-components";
import type { Links } from "@scm-manager/ui-types";
import Activity from "./Activity";
import ActivityNavigation from "./ActivityNavigation";

type RouteProps = {
  authenticated?: boolean,
  links: Links
};

const predicate = (props: Object) => {
  return props.links && props.links.activity;
};

const ActivityRoute = ({ authenticated, links }: RouteProps) => {
  return (
    <ProtectedRoute
      path="/activity"
      component={() => <Activity activityUrl={links.activity.href} />}
      authenticated={authenticated && links.activity.href}
    />
  );
};

binder.bind("main.route", ActivityRoute);

binder.bind("primary-navigation.first-menu", ActivityNavigation, predicate);

binder.bind("main.redirect", () => "/activity");
