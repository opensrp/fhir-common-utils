package org.smartregister.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Location;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.defaultanswers.ReturnsDeepStubs;

import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.IRead;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import ca.uhn.fhir.rest.gclient.IReadTyped;
import ca.uhn.fhir.rest.gclient.IUntypedQuery;

public class LocationHelperTest {

  IGenericClient client;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    client = mock(IGenericClient.class, new ReturnsDeepStubs());
  }

  @Test
  public void testUpdateLocationLineage() {
    Location location = new Location();
    location.setId("location1");
    location.setPartOf(null);

    Location childLocation = new Location();
    childLocation.setId("location2");
    childLocation.setPartOf(new org.hl7.fhir.r4.model.Reference("Location/location1"));

    Bundle childLocationBundle = new Bundle();
    Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
    entry.setResource(childLocation);
    childLocationBundle.setEntry(Collections.singletonList(entry));

    IRead iReadMock = mock(IRead.class);
    IReadTyped<IBaseResource> iReadTypedMock = mock(IReadTyped.class);
    IReadExecutable<Location> iReadExecutableMock = mock(IReadExecutable.class);

    IUntypedQuery<Location> iUntypedQueryMock = mock(IUntypedQuery.class);
    IQuery<Location> iQueryMock = mock(IQuery.class);

    Mockito.doReturn(iReadMock).when(client).read();
    Mockito.doReturn(iReadTypedMock).when(iReadMock).resource(Location.class);
    Mockito.doReturn(iReadExecutableMock).when(iReadTypedMock).withId("location1");
    Mockito.doReturn(location).when(iReadExecutableMock).execute();

    Mockito.doReturn(iUntypedQueryMock).when(client).search();
    Mockito.doReturn(iQueryMock).when(iUntypedQueryMock).forResource(Location.class);
    Mockito.doReturn(iQueryMock).when(iQueryMock).where(Mockito.any(ICriterion.class));
    Mockito.doReturn(iQueryMock).when(iQueryMock).usingStyle(SearchStyleEnum.POST);
    Mockito.doReturn(iQueryMock).when(iQueryMock).count(100);
    Mockito.doReturn(iQueryMock).when(iQueryMock).returnBundle(Bundle.class);
    Mockito.doReturn(childLocationBundle, (Bundle) null).when(iQueryMock).execute();

    Location result = LocationHelper.updateLocationLineage(client, "location1");

    assertNotNull(result);
    assertEquals("location1", result.getIdElement().getIdPart());
    verify(client, times(1)).read();
    verify(client, times(2)).search();
  }
}
