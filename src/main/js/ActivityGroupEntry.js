//@flow
import React from "react";
import type { ActivityGroup } from "./ActivityGroup";
import { ChangesetList, Icon } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import styled from "styled-components";

const StyledActivityGroup = styled.div`
    margin-bottom: 1rem;
`;

const Headline = styled.h3`
  font-size: 1.25rem;
    & small {
      font-size: 0.875rem;
    }
`;

const Wrapper = styled.div`
    margin: 1rem 0 2rem;
    padding: 1rem;
    border: 1px solid #dbdbdb;
    border-radius: 4px
`;

type Props = {
  group: ActivityGroup,

  // context prop
  t: string => string
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
    const { t, group } = this.props;
    const { collapsed } = this.state;

    const icon = collapsed ? "angle-right" : "angle-down";
    let content = null;
    if (!collapsed) {
      content = (
        <Wrapper>
          <ChangesetList
            repository={group.repository}
            changesets={group.changesets}
          />
        </Wrapper>
      );
    }

    return (
      <StyledActivityGroup>
        <div className="has-cursor-pointer" onClick={this.toggleCollapse}>
          <Headline>
            <Icon name={icon} color="default" /> {group.repository.namespace}/
            {group.repository.name} - {group.repository.type}{" "}
            <small className="has-text-grey-light">
              (
              {t("scm-activity-plugin.changeset", {
                count: group.changesets.length
              })}
              )
            </small>
          </Headline>
        </div>
        {content}
      </StyledActivityGroup>
    );
  }
}

export default translate("plugins")(ActivityGroupEntry);
