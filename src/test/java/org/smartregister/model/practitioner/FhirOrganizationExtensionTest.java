/*
 * Copyright 2022 Ona Systems, Inc
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

import org.hl7.fhir.r4.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FhirOrganizationExtensionTest {

    @Test
    public void testMapValues() {
        Organization organization = new Organization();
        List<Identifier> identifierList = new ArrayList<>();
        Identifier identifier = new Identifier();
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifierList.add(identifier);
        identifier.setValue("a2788d06-fdad-4a74-90ac-3ac4e243eee5");
        organization.setIdentifier(identifierList);
        organization.setId("");
        organization.setActive(true);
        List<CodeableConcept> typeList = new ArrayList<>();
        CodeableConcept type = new CodeableConcept();
        type.setId("Test type");
        List<Coding> codings = new ArrayList<>();
        Coding coding = new Coding();
        coding.setCode("A");
        codings.add(coding);
        type.setCoding(codings);
        typeList.add(type);
        organization.setType(typeList);
        organization.setName("Organization A");
        List<StringType> aliasList = new ArrayList<>();
        StringType alias = new StringType();
        alias.setValue("Organization A");
        aliasList.add(alias);
        organization.setAlias(aliasList);

        FhirOrganizationExtension fhirOrganizationExtension = new FhirOrganizationExtension();
        fhirOrganizationExtension = fhirOrganizationExtension.mapValues(organization);

        assertNotNull(fhirOrganizationExtension);
        assertEquals(organization.getId(), fhirOrganizationExtension.getId());
        assertEquals(organization.getIdentifier(), fhirOrganizationExtension.getIdentifier());
        assertEquals(organization.getActive(), fhirOrganizationExtension.getActive());
        assertEquals(organization.getType(), fhirOrganizationExtension.getType());
        assertEquals(organization.getName(), fhirOrganizationExtension.getName());
        assertEquals(organization.getAlias(), fhirOrganizationExtension.getAlias());
        assertEquals(organization.getTelecom(), fhirOrganizationExtension.getTelecom());
        assertEquals(organization.getAddress(), fhirOrganizationExtension.getAddress());
        assertEquals(organization.getPartOf(), fhirOrganizationExtension.getPartOf());
        assertEquals(organization.getContact(), fhirOrganizationExtension.getContact());
        assertEquals(organization.getEndpoint(), fhirOrganizationExtension.getEndpoint());
    }
}
