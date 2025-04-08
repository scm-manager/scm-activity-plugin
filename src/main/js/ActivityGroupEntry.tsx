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

import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import { ChangesetList, NoStyleButton } from "@scm-manager/ui-components";
import { Icon } from "@scm-manager/ui-core";
import { ActivityGroup } from "./ActivityGroup";

const Headline = styled.h3`
  font-size: 1.25rem;
  & small {
    font-size: 0.875rem;
  }
`;

const Wrapper = styled.div`
  margin: 1rem 0 2rem;
  padding: 1rem;
  border: var(--scm-border);
  border-radius: 0.25rem;
`;

type Props = {
  group: ActivityGroup;
};

const ActivityGroupEntry: React.FC<Props> = ({ group }) => {
  const [t] = useTranslation("plugins");
  const [collapsed, setCollapsed] = useState(false);

  const toggleCollapse = () => {
    setCollapsed(!collapsed);
  };

  const icon = collapsed ? "angle-right" : "angle-down";
  const contentId = `activity-group-${group.repository.namespace}-${group.repository.name}`;

  let content = null;
  if (!collapsed) {
    content = (
      <Wrapper id={contentId}>
        <ChangesetList repository={group.repository} changesets={group.changesets} />
      </Wrapper>
    );
  }

  return (
    <div className="mb-4">
      <NoStyleButton
        onClick={toggleCollapse}
        aria-expanded={!collapsed}
        aria-controls={contentId}
      >
        <Headline>
          <Icon>{icon}</Icon> {group.repository.namespace}/{group.repository.name} - {group.repository.type}{" "}
          <small className="has-text-grey-light">
            (
            {t("scm-activity-plugin.changeset", {
              count: group.changesets.length,
            })}
            )
          </small>
        </Headline>
      </NoStyleButton>
      {content}
    </div>
  );
};

export default ActivityGroupEntry;
