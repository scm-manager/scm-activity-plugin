//@flow
import React from "react";
import injectSheet from "react-jss";
import classNames from "classnames";
import type { ActivityGroup } from "./ActivityGroup";
import { ChangesetList } from "@scm-manager/ui-components";
import { translate } from "react-i18next";

const styles = {
  pointer: {
    cursor: "pointer",
    fontSize: "1.5rem"
  },
  activityGroup: {
    marginBottom: "1em"
  },
  wrapper: {
    padding: "0 0.75rem"
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

    const icon = collapsed ? "fa-angle-right" : "fa-angle-down";
    let content = null;
    if (!collapsed) {
      content = (
        <ChangesetList
          repository={group.repository}
          changesets={group.changesets}
        />
      );
    }
    return (
      <div className={classes.activityGroup}>
        <h2>
          <span className={classes.pointer} onClick={this.toggleCollapse}>
            <i className={classNames("fa", icon)} />{" "}
            {group.repository.namespace}/{group.repository.name} -{" "}
            {group.repository.type} / ({group.changesets.length}{" "}
            {group.changesets.length > 1
              ? t("scm-activity-plugin.changeset-plural")
              : t("scm-activity-plugin.changeset-singular")}
            )
          </span>
        </h2>
        <hr />
        <div>{content}</div>
        <div className={classes.clearfix} />
      </div>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(ActivityGroupEntry));
