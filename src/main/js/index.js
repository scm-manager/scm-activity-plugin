// @flow

import React from "react";
import {binder} from "@scm-manager/ui-extensions";
import {PrimaryNavigationLink} from "@scm-manager/ui-components";
import {Route} from "react-router-dom";
import Activity from "./Activity";

const predicate = (props: Object) => {
  return props.links && props.links.activity;
};

const ActivityRoute = ({authenticated, links}) => {
  return (
    <Route
      path={"/activity"}
      render={() => <Activity activityUrl={links.activity.href}  />}
    />
  );
};

binder.bind(
  "main.route",
  ActivityRoute,
  predicate);

const ActivityNavLink = () => {
  return (
    <PrimaryNavigationLink
      to={"/activity"}
      match={"/activity"}
      label={"Activity"}
      key={"activity"}
    />
  );
};

binder.bind(
  "primary-navigation.first-menu",
  ActivityNavLink,
  predicate
);
