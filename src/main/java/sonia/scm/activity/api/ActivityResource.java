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


import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import sonia.scm.activity.ActivityManager;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.web.VndMediaType;

/**
 * @author Sebastian Sdorra
 */
@OpenAPIDefinition(tags = {
  @Tag(name = "Activity Plugin", description = "Activity plugin provided endpoints")
})
@Path(ActivityResource.PATH)
public class ActivityResource {

  public static final String PATH = "v2/plugins/activity";

  public static final int PAGE_SIZE = 20;

  private final ActivityManager activityManager;

  private final ActivityMapper mapper;

  @Inject
  public ActivityResource(ActivityManager activityManager, ActivityMapper mapper) {
    this.activityManager = activityManager;
    this.mapper = mapper;
  }

  @GET
  @Path("")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get activities", description = "Returns latest activities for current user as JSON.", tags = "Activity Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = ActivitiesDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public ActivitiesDto getLatestActivity(@Context UriInfo uriInfo) {
    return mapper.using(uriInfo).map(activityManager.getLatestActivity(PAGE_SIZE));
  }

  @GET
  @Path("atom")
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Operation(summary = "Get activities", description = "Returns latest activities for current user as Atom-XML.", tags = "Activity Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_ATOM_XML,
      schema = @Schema(implementation = ActivitiesDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public ActivitiesDto getLatestAtomActivity(@Context UriInfo uriInfo) {
    return mapper.using(uriInfo).map(activityManager.getLatestActivity(PAGE_SIZE));
  }

  @GET
  @Path("rss")
  @Produces("application/rss+xml")
  @Operation(summary = "Get activities", description = "Returns latest activities for current user as RSS-XML.", tags = "Activity Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = "application/rss+xml",
      schema = @Schema(implementation = ActivitiesDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public ActivitiesDto getLatestRssActivity(@Context UriInfo uriInfo) {
    return mapper.using(uriInfo).map(activityManager.getLatestActivity(PAGE_SIZE));
  }
}
