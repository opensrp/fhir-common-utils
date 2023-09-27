package org.smartregister.model.location.utils;

import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Reference;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static final int TOTAL_LOCATIONS = 50000;// Generate 50K locations
    public static final int TOTAL_CHILD_LOCATIONS_PER_NODE = 5; //Total sub-locations per location
    public static List<Location> getTestLocations() {

        List<Location> locationList = new ArrayList<>();

        int parentId = 1;

        for (int i = 1; i <  TOTAL_LOCATIONS + 1; i++) {

            if(i == 1){
                parentId = 1;
            } else if(i % TOTAL_CHILD_LOCATIONS_PER_NODE == 0 ){
                parentId++;
            }

            locationList.add(getLocation("Location/" + i, "Test Location " + i, (i == 1 ? null : "Location/" + parentId)));
        }

        return locationList;
    }

    private static Location getLocation(String id, String name, String reference) {

        Location location = new Location();
        location.setId(id);
        location.setName(name);

        Reference partOfReference = new Reference();
        partOfReference.setReference(reference);
        location.setPartOf(partOfReference);

        return location;
    }
}
