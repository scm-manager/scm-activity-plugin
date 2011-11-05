/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.activity;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "activity")
@XmlAccessorType(XmlAccessType.FIELD)
public class Activity
{

  /**
   * Constructs ...
   *
   */
  public Activity() {}

  /**
   * Constructs ...
   *
   *
   * @param repository
   * @param changeset
   */
  public Activity(Repository repository, Changeset changeset)
  {
    this.repositoryId = repository.getId();
    this.repositoryName = repository.getName();
    this.repositoryType = repository.getType();
    this.changeset = changeset;
  }

  /**
   * Constructs ...
   *
   *
   *
   * @param repositoryId
   * @param repositoryName
   * @param repositoryType
   * @param changeset
   */
  public Activity(String repositoryId, String repositoryName,
                  String repositoryType, Changeset changeset)
  {
    this.repositoryId = repositoryId;
    this.repositoryName = repositoryName;
    this.changeset = changeset;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public Changeset getChangeset()
  {
    return changeset;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getRepositoryId()
  {
    return repositoryId;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getRepositoryName()
  {
    return repositoryName;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getRepositoryType()
  {
    return repositoryType;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param changeset
   */
  public void setChangeset(Changeset changeset)
  {
    this.changeset = changeset;
  }

  /**
   * Method description
   *
   *
   * @param repositoryId
   */
  public void setRepositoryId(String repositoryId)
  {
    this.repositoryId = repositoryId;
  }

  /**
   * Method description
   *
   *
   * @param repositoryName
   */
  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  /**
   * Method description
   *
   *
   * @param repositoryType
   */
  public void setRepositoryType(String repositoryType)
  {
    this.repositoryType = repositoryType;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private Changeset changeset;

  /** Field description */
  @XmlElement(name = "repository-id")
  private String repositoryId;

  /** Field description */
  @XmlElement(name = "repository-name")
  private String repositoryName;

  /** Field description */
  @XmlElement(name = "repository-type")
  private String repositoryType;
}
