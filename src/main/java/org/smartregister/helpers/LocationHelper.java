package org.smartregister.helpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.utils.Constants;

import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;

public class LocationHelper {

  private static final Logger logger = LoggerFactory.getLogger(LocationHelper.class);

  // Custom class to pair a Location with its lineage ancestors
  static class LocationWithTags {
    Location location;
    List<String> ancestorIds;

    public LocationWithTags(Location location, List<String> ancestorIds) {
      this.location = location;
      this.ancestorIds = ancestorIds;
    }
  }

  public static Location updateLocationLineage(IGenericClient client, String locationId) {
    Queue<LocationWithTags> locationsQueue = new LinkedList<>();
    Location location = client.read().resource(Location.class).withId(locationId).execute();

    List<String> ancestorIds = getParentLocationLineage(client, location);
    locationsQueue.add(new LocationWithTags(location, ancestorIds));

    while (!locationsQueue.isEmpty()) {
      LocationWithTags currentLocationWithTags = locationsQueue.poll();
      Location currentLocation = currentLocationWithTags.location;
      String currentLocationId = currentLocation.getIdElement().getIdPart();

      updateLocationTags(client, currentLocation, currentLocationWithTags.ancestorIds);
      List<Location> childLocations = fetchChildLocations(client, currentLocationId);

      for (Location childLocation : childLocations) {
        locationsQueue.add(
            new LocationWithTags(
                childLocation,
                new ArrayList<>(currentLocationWithTags.ancestorIds)));
      }
    }
    return location;
  }

  private static List<String> getParentLocationLineage(IGenericClient client, Location location) {
    if (!location.hasPartOf() || !location.getPartOf().hasReference()) {
      return new ArrayList<>();
    }

    String parentLocationId = location.getPartOf().getReferenceElement().getIdPart();
    Location parentLocation =
        client.read().resource(Location.class).withId(parentLocationId).execute();

    List<String> ancestorIds =
        parentLocation.getMeta().getTag().stream()
            .filter(
                tag ->
                    Constants.DEFAULT_LOCATION_LINEAGE_TAG_URL.equals(
                        tag.getSystem()))
            .map(Coding::getCode)
            .collect(Collectors.toList());

    ancestorIds.add(parentLocationId);
    client.update().resource(location).execute();
    return ancestorIds;
  }

  private static void updateLocationTags(
      IGenericClient client, Location location, List<String> ancestorIds) {
    logger.info("Adding lineage tags to Location Id : {}", location.getIdElement().getIdPart());

    List<Coding> newTags =
        location.getMeta().getTag().stream()
            .filter(
                tag ->
                    !Constants.DEFAULT_LOCATION_LINEAGE_TAG_URL.equals(
                        tag.getSystem()))
            .collect(Collectors.toList());

    for (String tag : ancestorIds) {
      newTags.add(
          new Coding()
              .setSystem(Constants.DEFAULT_LOCATION_LINEAGE_TAG_URL)
              .setCode(tag));
    }

    location.getMeta().setTag(newTags);
    client.update().resource(location).execute();
  }

  private static List<Location> fetchChildLocations(IGenericClient client, String locationId) {
    IQuery<IBaseBundle> query =
        client.search()
            .forResource(Location.class)
            .where(
                new ReferenceClientParam(Location.SP_PARTOF)
                    .hasAnyOfIds(locationId));

    Bundle childLocationBundle =
        query.usingStyle(SearchStyleEnum.POST)
            .count(100)
            .returnBundle(Bundle.class)
            .execute();

    if (childLocationBundle != null) {
      fetchAllBundlePagesAndInject(client, childLocationBundle);
      return childLocationBundle.getEntry().stream()
          .map(Bundle.BundleEntryComponent::getResource)
          .filter(resource -> resource instanceof Location)
          .map(resource -> (Location) resource)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * This is a recursive function which updates the result bundle with results of all pages
   * whenever there's an entry for Bundle.LINK_NEXT
   *
   * @param fhirClient the Generic FHIR Client instance
   * @param resultBundle the result bundle from the first request
   */
  public static void fetchAllBundlePagesAndInject(
      IGenericClient fhirClient, Bundle resultBundle) {

    if (resultBundle.getLink(Bundle.LINK_NEXT) != null) {

      cleanUpBundlePaginationNextLinkServerBaseUrl((GenericClient) fhirClient, resultBundle);

      Bundle pageResultBundle = fhirClient.loadPage().next(resultBundle).execute();

      resultBundle.getEntry().addAll(pageResultBundle.getEntry());
      resultBundle.setLink(pageResultBundle.getLink());

      fetchAllBundlePagesAndInject(fhirClient, resultBundle);
    }

    resultBundle.setLink(
        resultBundle.getLink().stream()
            .filter(
                bundleLinkComponent ->
                    !Bundle.LINK_NEXT.equals(bundleLinkComponent.getRelation()))
            .collect(Collectors.toList()));
    resultBundle.getMeta().setLastUpdated(resultBundle.getMeta().getLastUpdated());
  }

  public static void cleanUpBundlePaginationNextLinkServerBaseUrl(
      GenericClient fhirClient, Bundle resultBundle) {
    String cleanUrl =
        cleanHapiPaginationLinkBaseUrl(
            resultBundle.getLink(Bundle.LINK_NEXT).getUrl(), fhirClient.getUrlBase());
    resultBundle
        .getLink()
        .replaceAll(
            bundleLinkComponent ->
                Bundle.LINK_NEXT.equals(bundleLinkComponent.getRelation())
                    ? new Bundle.BundleLinkComponent(
                    new StringType(Bundle.LINK_NEXT),
                    new UriType(cleanUrl))
                    : bundleLinkComponent);
  }

  public static String cleanHapiPaginationLinkBaseUrl(
      String originalUrl, String fhirServerBaseUrl) {
    return originalUrl.indexOf('?') > -1
        ? fhirServerBaseUrl + originalUrl.substring(originalUrl.indexOf('?'))
        : fhirServerBaseUrl;
  }
}
