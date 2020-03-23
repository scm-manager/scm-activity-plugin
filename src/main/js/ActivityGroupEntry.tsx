/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
