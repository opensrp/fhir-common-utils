package org.smartregister.helpers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Location;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.utils.Constants;

public class LocationHelperTest {

  @Test
  public void testUpdateLocationTagsWithoutMocking() {
    Location location = new Location();
    Assert.assertTrue(location.getMeta().getTag().isEmpty());

    List<String> ancestorIds = Arrays.asList("location-ancestor", "location-2");

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

    Assert.assertFalse(location.getMeta().getTag().isEmpty());

    List<Coding> tags = location.getMeta().getTag();
    Assert.assertEquals(2, tags.size());
    Assert.assertTrue(tags.stream().anyMatch(tag -> tag.getCode().equals("location-ancestor")));
    Assert.assertTrue(tags.stream().anyMatch(tag -> tag.getCode().equals("location-2")));
  }
}
