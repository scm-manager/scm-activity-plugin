// @flow
import React from "react";
import { PrimaryNavigationLink } from "@scm-manager/ui-components";
import { translate } from "react-i18next";

const ActivityNavigation = ({ t }) => {
  return (
    <PrimaryNavigationLink
      to="/activity"
      match="/activity"
      label={t("scm-activity-plugin.primaryNavigation")}
      key="activity"
    />
  );
};

export default translate("plugins")(ActivityNavigation);
