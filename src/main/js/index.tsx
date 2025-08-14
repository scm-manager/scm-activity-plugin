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

import React from "react";
import { binder, extensionPoints } from "@scm-manager/ui-extensions";
import { Link, Links } from "@scm-manager/ui-types";
import { ProtectedRoute } from "@scm-manager/ui-components";
import Activity from "./Activity";
import ActivityNavigation from "./ActivityNavigation";

const predicate = (props: Props) => {
  return props?.links && props.links.activity;
};

type Props = {
  authenticated?: boolean;
  links: Links;
};

const ActivityRoute: React.FC<Props> = ({ authenticated, links }) => {
  const activityUrl = (links?.activity as Link)?.href;
  return (
    <ProtectedRoute
      path="/activity"
      component={() => <Activity activityUrl={activityUrl ?? undefined} />}
      authenticated={!!(authenticated && !!activityUrl)}
    />
  );
};

binder.bind<extensionPoints.MainRoute>("main.route", ActivityRoute, predicate);
binder.bind<extensionPoints.PrimaryNavigationFirstMenu>("primary-navigation.first-menu", ActivityNavigation, predicate);
binder.bind<extensionPoints.MainRedirect>("main.redirect", () => "/activity", predicate);

export default ActivityRoute;
