//@flow
import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";
import type { ActivityGroup } from "./ActivityGroup";
import { ChangesetList, Icon } from "@scm-manager/ui-components";
import { translate } from "react-i18next";

const styles = {
  fontSize: {
    fontSize: "1.5rem"
  },
  activityGroup: {
    marginBottom: "1em"
  },
  wrapper: {
    margin: "1rem 0 2rem",
    padding: "1rem",
    border: "1px solid var(--border)",
    borderRadius: "4px"
  },
  clearfix: {
    clear: "both"
  }
};

type Props = {
  group: ActivityGroup,

  // context prop
  t: string => string,
  classes: any
};

type State = {
  collapsed: boolean
};

class ActivityGroupEntry extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      collapsed: false
    };
  }

  toggleCollapse = () => {
    this.setState(prevState => ({
      collapsed: !prevState.collapsed
    }));
  };

  render() {
    const { t, group, classes } = this.props;
    const { collapsed } = this.state;

    const icon = collapsed ? "angle-right" : "angle-down";
    let content = null;
    if (!collapsed) {
      content = (
        <div className={classes.wrapper}>
          <ChangesetList
            repository={group.repository}
            changesets={group.changesets}
          />
        </div>
      );
    }
    return (
      <div className={classes.activityGroup}>
        <h3
          className={classNames("has-cursor-pointer", classes.fontSize)}
          onClick={this.toggleCollapse}
        >
          <Icon name={icon} color="default" /> {group.repository.namespace}/
          {group.repository.name} - {group.repository.type} / (
          {group.changesets.length}{" "}
          {group.changesets.length > 1
            ? t("scm-activity-plugin.changeset-plural")
            : t("scm-activity-plugin.changeset-singular")}
          )
        </h3>
        {content}
      </div>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(ActivityGroupEntry));
