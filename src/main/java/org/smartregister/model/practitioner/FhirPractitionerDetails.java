/*
 * Copyright 2021 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartregister.model.practitioner;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.*;
import org.smartregister.model.location.LocationHierarchy;

import java.util.List;

@DatatypeDef(name = "fhir")
public class FhirPractitionerDetails extends Type implements ICompositeType {

    @Child(
            name = "careteams",
            type = {CareTeam.class},
            order = 1,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    List<CareTeam> careTeams;

    @Child(
            name = "teams",
            type = {Organization.class},
            order = 2,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    List<Organization> organizations;


    @Child(
            name = "locations",
            type = {Location.class},
            order = 3,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    private List<Location> locations;

    @Child(
            name = "locationHierarchyList",
            type = {LocationHierarchy.class},
            order = 4,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    private List<LocationHierarchy> locationHierarchyList;

    @Child(
      name = "practitionerRoles",
      type = {PractitionerRole.class},
      order = 5,
      min = 0,
      max = -1,
      modifier = false,
      summary = false)
    List<PractitionerRole> practitionerRoles;

    @Child(
      name = "groups",
      type = {Group.class},
      order = 6,
      min = 0,
      max = -1,
      modifier = false,
      summary = false)
    List<Group> groups;
    @Child(
            name = "practitioner",
            type = {Practitioner.class},
            order = 7,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    private List<Practitioner> practitioners;

    public List<CareTeam> getCareTeams() {
        return careTeams;
    }

    public void setCareTeams(
            List<CareTeam> careTeams) {
        this.careTeams = careTeams;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<LocationHierarchy> getLocationHierarchyList() {
        return locationHierarchyList;
    }

    public void setLocationHierarchyList(List<LocationHierarchy> locationHierarchyList) {
        this.locationHierarchyList = locationHierarchyList;
    }

    public List<PractitionerRole> getPractitionerRoles() {
        return practitionerRoles;
    }

    public void setPractitionerRoles(List<PractitionerRole> practitionerRoles) {
        this.practitionerRoles = practitionerRoles;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Practitioner> getPractitioners() {
        return practitioners;
    }

    public void setPractitioners(List<Practitioner> practitioners) {
        this.practitioners = practitioners;
    }

    @Override
    public Type copy() {
        FhirPractitionerDetails fhirPractitionerDetails = new FhirPractitionerDetails();
        copyValues(fhirPractitionerDetails);
        return fhirPractitionerDetails;
    }

    @Override
    public boolean isEmpty() {
        return ElementUtil.isEmpty(practitioners);
    }

    @Override
    protected Type typedCopy() {
        return copy();
    }
}
