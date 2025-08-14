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

package sonia.scm.activity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sonia.scm.repository.Changeset;

import java.io.Serializable;

/**
 *
 * @author Sebastian Sdorra
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@XmlRootElement(name = "activity")
@XmlAccessorType(XmlAccessType.FIELD)
public class Activity implements Serializable {


  @XmlElement(name = "repository-id")
  private String repositoryId;

  @XmlElement(name = "repository-name")
  private String repositoryName;

  @XmlElement(name = "repository-namespace")
  private String repositoryNamespace;

  @XmlElement(name = "repository-type")
  private String repositoryType;
  private Changeset changeset;
}
