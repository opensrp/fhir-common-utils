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
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.model.api.annotation.SearchParamDefinition;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.ResourceType;

import java.util.ArrayList;
import java.util.List;

@ResourceDef(
        name = "PractitionerDetail",
        profile = "http://hl7.org/fhir/profiles/custom-resource")
public class PractitionerDetails extends Practitioner {

    @Child(
            name = "fhir",
            type = {FhirPractitionerDetails.class})
    @Description(
            shortDefinition = "Get resources from FHIR Server",
            formalDefinition = "Get resources from FHIR Server")
    private FhirPractitionerDetails fhirPractitionerDetails;

    @SearchParamDefinition(
            name = "keycloak-uuid",
            path = "PractitionerDetails.keycloak-uuid",
            description = "A practitioner's keycloak-uuid",
            type = "token"
    )
    public static final String SP_KEYCLOAK_UUID = "keycloak-uuid";
    public static final TokenClientParam KEYCLOAK_UUID = new TokenClientParam("keycloak-uuid");

    @Override
    public Practitioner copy() {
        Practitioner practitioner = new Practitioner();
        Bundle bundle = new Bundle();
        List<Bundle.BundleEntryComponent> theEntry = new ArrayList<>();
        Bundle.BundleEntryComponent entryComponent = new Bundle.BundleEntryComponent();
        entryComponent.setResource(new Bundle());
        theEntry.add(entryComponent);
        bundle.setEntry(theEntry);
        this.copyValues(practitioner);
        return practitioner;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Bundle;
    }

    public FhirPractitionerDetails getFhirPractitionerDetails() {
        return fhirPractitionerDetails;
    }

    public void setFhirPractitionerDetails(FhirPractitionerDetails fhirPractitionerDetails) {
        this.fhirPractitionerDetails = fhirPractitionerDetails;
    }


}
