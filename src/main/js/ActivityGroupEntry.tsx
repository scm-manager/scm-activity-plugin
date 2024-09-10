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
import styled from "styled-components";
import { withTranslation, WithTranslation } from "react-i18next";
import { ChangesetList, Icon } from "@scm-manager/ui-components";
import { ActivityGroup } from "./ActivityGroup";

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
  border-radius: 4px;
`;

type Props = WithTranslation & {
  group: ActivityGroup;
};

type State = {
  collapsed: boolean;
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
          <ChangesetList repository={group.repository} changesets={group.changesets} />
        </Wrapper>
      );
    }

    return (
      <StyledActivityGroup>
        <div className="has-cursor-pointer" onClick={this.toggleCollapse}>
          <Headline>
            <Icon name={icon} color="default" /> {group.repository.namespace}/{group.repository.name} -{" "}
            {group.repository.type}{" "}
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

export default withTranslation("plugins")(ActivityGroupEntry);
