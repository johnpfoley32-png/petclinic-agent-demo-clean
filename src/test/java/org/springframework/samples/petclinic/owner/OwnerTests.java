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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Owner}.
 * <p>
 * Focuses on branch coverage for getPet(Integer), getPet(String, boolean), addPet, and
 * addVisit methods.
 */
class OwnerTests {

	private Owner owner;

	@BeforeEach
	void setup() {
		owner = new Owner();
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");
	}

	@Nested
	class GetPetById {

		@Test
		void returnsPetWhenIdMatches() {
			Pet pet = new Pet();
			owner.addPet(pet);
			pet.setId(7);

			assertThat(owner.getPet(7)).isEqualTo(pet);
		}

		@Test
		void returnsNullWhenIdDoesNotMatch() {
			Pet pet = new Pet();
			owner.addPet(pet);
			pet.setId(7);

			assertThat(owner.getPet(99)).isNull();
		}

		@Test
		void skipsNewPets() {
			// A "new" pet has no id set, so getPet(Integer) should skip it
			Pet newPet = new Pet();
			owner.addPet(newPet);

			assertThat(owner.getPet(1)).isNull();
		}

		@Test
		void returnsNullForEmptyPetsList() {
			assertThat(owner.getPet(1)).isNull();
		}

	}

	@Nested
	class GetPetByName {

		@Test
		void returnsPetWhenNameMatchesCaseInsensitive() {
			Pet pet = new Pet();
			pet.setName("Buddy");
			owner.addPet(pet);

			assertThat(owner.getPet("buddy")).isEqualTo(pet);
			assertThat(owner.getPet("BUDDY")).isEqualTo(pet);
		}

		@Test
		void returnsNullWhenNameDoesNotMatch() {
			Pet pet = new Pet();
			pet.setName("Buddy");
			owner.addPet(pet);

			assertThat(owner.getPet("Rex")).isNull();
		}

		@Test
		void returnsNullWhenPetNameIsNull() {
			Pet pet = new Pet();
			// Pet name is null by default
			owner.addPet(pet);

			assertThat(owner.getPet("Buddy")).isNull();
		}

	}

	@Nested
	class GetPetByNameIgnoreNew {

		@Test
		void returnsNewPetWhenIgnoreNewIsFalse() {
			Pet newPet = new Pet();
			newPet.setName("Buddy");
			owner.addPet(newPet);

			// ignoreNew=false should return the pet even though it's new
			assertThat(owner.getPet("Buddy", false)).isEqualTo(newPet);
		}

		@Test
		void skipsNewPetWhenIgnoreNewIsTrue() {
			Pet newPet = new Pet();
			newPet.setName("Buddy");
			owner.addPet(newPet);

			// ignoreNew=true should skip new pets (those without an id)
			assertThat(owner.getPet("Buddy", true)).isNull();
		}

		@Test
		void returnsSavedPetWhenIgnoreNewIsTrue() {
			Pet savedPet = new Pet();
			savedPet.setName("Buddy");
			owner.addPet(savedPet);
			savedPet.setId(5);

			// ignoreNew=true should still return a saved pet
			assertThat(owner.getPet("Buddy", true)).isEqualTo(savedPet);
		}

	}

	@Nested
	class AddPet {

		@Test
		void addsNewPet() {
			Pet pet = new Pet();
			owner.addPet(pet);

			assertThat(owner.getPets()).hasSize(1);
			assertThat(owner.getPets()).contains(pet);
		}

		@Test
		void doesNotAddPetWithExistingId() {
			Pet existingPet = new Pet();
			existingPet.setId(10);

			owner.addPet(existingPet);

			// Pet is not new (has an id), so it should NOT be added
			assertThat(owner.getPets()).isEmpty();
		}

	}

	@Nested
	class AddVisit {

		@Test
		void addsVisitToExistingPet() {
			Pet pet = new Pet();
			owner.addPet(pet);
			pet.setId(3);

			Visit visit = new Visit();
			owner.addVisit(3, visit);

			assertThat(pet.getVisits()).hasSize(1);
			assertThat(pet.getVisits()).contains(visit);
		}

		@Test
		void throwsWhenPetIdIsNull() {
			assertThatThrownBy(() -> owner.addVisit(null, new Visit())).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		void throwsWhenVisitIsNull() {
			assertThatThrownBy(() -> owner.addVisit(1, null)).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		void throwsWhenPetNotFound() {
			assertThatThrownBy(() -> owner.addVisit(999, new Visit())).isInstanceOf(IllegalArgumentException.class);
		}

	}

	@Test
	void toStringContainsOwnerDetails() {
		owner.setId(1);
		String result = owner.toString();

		assertThat(result).contains("lastName")
			.contains("Franklin")
			.contains("firstName")
			.contains("George")
			.contains("address")
			.contains("110 W. Liberty St.")
			.contains("city")
			.contains("Madison")
			.contains("telephone")
			.contains("6085551023");
	}

}
