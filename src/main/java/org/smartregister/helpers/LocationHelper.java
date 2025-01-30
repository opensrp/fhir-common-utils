package org.smartregister.helpers;

import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.utils.Constants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;


public class LocationHelper {

  private static final Logger logger =
      LoggerFactory.getLogger(LocationHelper.class);

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
    List<String> ancestorIds = new ArrayList<>();

    if (location.hasPartOf() && location.getPartOf().hasReference()) {
      String parentLocationId = location.getPartOf().getReferenceElement().getIdPart();
      Location parentLocation = client.read().resource(Location.class).withId(parentLocationId).execute();

      ancestorIds = parentLocation.getMeta().getTag().stream()
          .filter(tag -> Constants.DEFAULT_LOCATION_LINEAGE_TAG_URL.equals(tag.getSystem()))
          .map(Coding::getCode)
          .collect(Collectors.toList());

      ancestorIds.add(parentLocationId);
      client.update().resource(location).execute();
    }

    locationsQueue.add(new LocationWithTags(location, ancestorIds));

    while (!locationsQueue.isEmpty()) {
      LocationWithTags currentLocationWithTags = locationsQueue.poll();
      Location currentLocation = currentLocationWithTags.location;
      String currentLocationId = currentLocation.getIdElement().getIdPart();
      List<String> currentAncestorIds = new ArrayList<>(currentLocationWithTags.ancestorIds);
      logger.info("Adding lineage tags to Location Id : {}",  currentLocationId);

      List<Coding> newTags = currentLocation.getMeta().getTag().stream()
          .filter(tag -> !Constants.DEFAULT_LOCATION_LINEAGE_TAG_URL.equals(tag.getSystem()))
          .collect(Collectors.toList());

      for (String tag : currentAncestorIds) {
        newTags.add(new Coding()
            .setSystem(Constants.DEFAULT_LOCATION_LINEAGE_TAG_URL)
            .setCode(tag));
      }
      currentLocation.getMeta().setTag(newTags);
      client.update().resource(currentLocation).execute();

      currentAncestorIds.add(currentLocationId);

      IQuery<IBaseBundle> query =
          client.search()
              .forResource(Location.class)
              .where(new ReferenceClientParam(Location.SP_PARTOF)
                  .hasAnyOfIds(currentLocationId));

      Bundle childLocationBundle = query.usingStyle(SearchStyleEnum.POST)
          .count(100)
          .returnBundle(Bundle.class)
          .execute();

      if (childLocationBundle != null) {
        fetchAllBundlePagesAndInject(client, childLocationBundle);
        for (Bundle.BundleEntryComponent childLocationEntry : childLocationBundle.getEntry()) {
          Location childLocation = (Location) childLocationEntry.getResource();
          locationsQueue.add(new LocationWithTags(childLocation, currentAncestorIds));
        }
      }
    }
    return location;
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
