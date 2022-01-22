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

import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FhirCareTeamExtensionTest {

    @Test
    public void testMapValues() {

        CareTeam careTeam = new CareTeam();
        List<Identifier> identifierList = new ArrayList<>();
        Identifier identifier = new Identifier();
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifierList.add(identifier);
        identifier.setValue("a2788d06-fdad-4a74-90ac-3ac4e243eee5");
        careTeam.setIdentifier(identifierList);
        careTeam.setName("Care Team A");
        careTeam.setStatus(CareTeam.CareTeamStatus.ACTIVE);
        Reference subjectRef = new Reference();
        subjectRef.setDisplay("Demo FHIR Groups");
        subjectRef.setReference("Group/206");
        careTeam.setSubject(subjectRef);
        List<CareTeam.CareTeamParticipantComponent> participantComponents = new ArrayList<>();
        CareTeam.CareTeamParticipantComponent careTeamParticipantComponent =
                new CareTeam.CareTeamParticipantComponent();
        Reference member = new Reference();
        member.setReference("Practitioner/152");
        member.setDisplay("Test Practitioner");
        careTeamParticipantComponent.setMember(member);
        participantComponents.add(careTeamParticipantComponent);
        careTeam.setParticipant(participantComponents);

        FhirCareTeamExtension fhirCareTeamExtension = new FhirCareTeamExtension();
        fhirCareTeamExtension = fhirCareTeamExtension.mapValues(careTeam);

        assertNotNull(fhirCareTeamExtension);
        assertEquals(careTeam.getId(), fhirCareTeamExtension.getId());
        assertEquals(careTeam.getIdentifier(), fhirCareTeamExtension.getIdentifier());
        assertEquals(careTeam.getStatus(), fhirCareTeamExtension.getStatus());
        assertEquals(careTeam.getCategory(), fhirCareTeamExtension.getCategory());
        assertEquals(careTeam.getName(), fhirCareTeamExtension.getName());
        assertEquals(careTeam.getSubject(), fhirCareTeamExtension.getSubject());
        assertEquals(careTeam.getEncounter(), fhirCareTeamExtension.getEncounter());
        assertEquals(careTeam.getPeriod(), fhirCareTeamExtension.getPeriod());
        assertEquals(careTeam.getParticipant(), fhirCareTeamExtension.getParticipant());
        assertEquals(careTeam.getReasonCode(), fhirCareTeamExtension.getReasonCode());
        assertEquals(careTeam.getReasonReference(), fhirCareTeamExtension.getReasonReference());
        assertEquals(
                careTeam.getManagingOrganization(),
                fhirCareTeamExtension.getManagingOrganization());
        assertEquals(careTeam.getTelecom(), fhirCareTeamExtension.getTelecom());
        assertEquals(careTeam.getNote(), fhirCareTeamExtension.getNote());
    }
}
