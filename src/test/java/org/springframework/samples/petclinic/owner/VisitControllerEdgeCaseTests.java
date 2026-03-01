/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Edge-case tests for {@link VisitController} covering previously uncovered branches:
 * <ul>
 * <li>Owner not found in loadPetWithVisit (orElseThrow lambda)</li>
 * <li>Pet not found for owner in loadPetWithVisit (null pet check)</li>
 * </ul>
 */
@WebMvcTest(VisitController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerEdgeCaseTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	private static final int MISSING_OWNER_ID = 999;

	private static final int MISSING_PET_ID = 888;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@BeforeEach
	void setup() {
		// Owner exists but only has pet with TEST_PET_ID
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(TEST_PET_ID);
		owner.addPet(pet);
		// addPet only adds new pets, so add directly since pet has an id
		owner.getPets().add(pet);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));

		// Owner not found for MISSING_OWNER_ID
		given(this.owners.findById(MISSING_OWNER_ID)).willReturn(Optional.empty());
	}

	@Test
	void testLoadPetWithVisitThrowsWhenOwnerNotFound() {
		// Exercises the orElseThrow lambda in loadPetWithVisit (line 66-67)
		assertThatThrownBy(
				() -> mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", MISSING_OWNER_ID, TEST_PET_ID)))
			.rootCause()
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Owner not found with id: " + MISSING_OWNER_ID);
	}

	@Test
	void testLoadPetWithVisitThrowsWhenPetNotFound() {
		// Exercises the pet == null branch in loadPetWithVisit (lines 70-73)
		// Owner exists but pet with MISSING_PET_ID does not
		assertThatThrownBy(
				() -> mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, MISSING_PET_ID)))
			.rootCause()
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Pet with id " + MISSING_PET_ID + " not found for owner with id " + TEST_OWNER_ID);
	}

}
