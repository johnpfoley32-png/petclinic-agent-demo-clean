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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Edge-case tests for {@link PetController} covering previously uncovered branches:
 * <ul>
 * <li>Owner not found in findOwner / findPet (orElseThrow lambdas)</li>
 * <li>processCreationForm with a pet whose name matches an existing but non-new pet</li>
 * <li>processCreationForm with null birthDate</li>
 * </ul>
 */
@WebMvcTest(value = PetController.class,
		includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE))
@DisabledInNativeImage
@DisabledInAotMode
class PetControllerEdgeCaseTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int MISSING_OWNER_ID = 999;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@MockitoBean
	private PetTypeRepository types;

	@BeforeEach
	void setup() {
		PetType cat = new PetType();
		cat.setId(3);
		cat.setName("hamster");
		given(this.types.findPetTypes()).willReturn(List.of(cat));

		// Owner not found for MISSING_OWNER_ID
		given(this.owners.findById(MISSING_OWNER_ID)).willReturn(Optional.empty());
	}

	@Nested
	class OwnerNotFound {

		@Test
		void testFindOwnerThrowsWhenOwnerNotFound() {
			// Exercises the orElseThrow lambda in findOwner (line 69-70)
			assertThatThrownBy(() -> mockMvc.perform(get("/owners/{ownerId}/pets/new", MISSING_OWNER_ID))).rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Owner not found with id: " + MISSING_OWNER_ID);
		}

		@Test
		void testFindPetThrowsWhenOwnerNotFoundForExistingPet() {
			// Exercises the orElseThrow lambda in findPet (line 83-84) when petId is
			// non-null
			assertThatThrownBy(
					() -> mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", MISSING_OWNER_ID, TEST_PET_ID)))
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Owner not found with id: " + MISSING_OWNER_ID);
		}

	}

	@Nested
	class ProcessCreationFormEdgeCases {

		@BeforeEach
		void setupOwnerWithSavedPet() {
			Owner owner = new Owner();
			Pet existingPet = new Pet();
			existingPet.setId(TEST_PET_ID); // saved pet (non-new)
			existingPet.setName("Buddy");
			owner.addPet(existingPet);
			// Since addPet only adds new pets, add directly to the list
			owner.getPets().add(existingPet);
			given(owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
		}

		@Test
		void testProcessCreationFormWithNameMatchingExistingSavedPet() throws Exception {
			// When pet.getName() matches an existing saved pet and the new pet isNew(),
			// getPet(name, true) returns the saved pet → duplicate rejection.
			// This exercises the "pet.isNew() && owner.getPet(pet.getName(), true) !=
			// null" branch
			mockMvc
				.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Buddy")
					.param("type", "hamster")
					.param("birthDate", "2015-02-12"))
				.andExpect(status().isOk());
		}

		@Test
		void testProcessCreationFormWithNullBirthDate() throws Exception {
			// Exercises the birthDate == null branch (line 113)
			mockMvc
				.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "NewPet")
					.param("type", "hamster"))
				.andExpect(status().isOk());
		}

	}

}
