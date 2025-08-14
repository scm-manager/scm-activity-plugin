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

package sonia.scm.activity.api;


import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sonia.scm.api.v2.resources.ChangesetDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("squid:S2160") // No semantics for equals here
public class ActivityDto extends HalRepresentation {

  private String repositoryName;

  private String repositoryNamespace;

  private String repositoryType;

  public ActivityDto(Links links, Embedded embedded) {
    super(links, embedded);
  }

  public ChangesetDto extractChangeset(){
    List<ChangesetDto> changeset = getEmbedded().getItemsBy("changeset", ChangesetDto.class);
    return changeset.get(0);
  }
}
