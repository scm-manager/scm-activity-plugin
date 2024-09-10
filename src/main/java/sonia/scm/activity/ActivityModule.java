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

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;
import sonia.scm.activity.api.ActivityMapper;
import sonia.scm.plugin.Extension;

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
public class ActivityModule extends AbstractModule {

  /**
   * Method description
   *
   */
  @Override
  protected void configure() {
    bind(ActivityMapper.class).to(Mappers.getMapper(ActivityMapper.class).getClass());
  }
}
