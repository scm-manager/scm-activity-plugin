import React from "react";
import { withTranslation } from "react-i18next";
import { PrimaryNavigationLink } from "@scm-manager/ui-components";

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

export default withTranslation("plugins")(ActivityNavigation);
