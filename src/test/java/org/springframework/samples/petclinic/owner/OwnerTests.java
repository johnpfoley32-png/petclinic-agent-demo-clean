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

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link Owner}.
 * <p>
 * Covers domain logic in getPet (by name and by id), addPet, addVisit, and toString.
 */
class OwnerTests {

	private Owner owner;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");
	}

	// --- getPet(String) and getPet(String, boolean) ---

	@Test
	void getPetByNameReturnsPetWhenFound() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);

		assertThat(owner.getPet("Buddy")).isSameAs(pet);
	}

	@Test
	void getPetByNameIsCaseInsensitive() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);

		assertThat(owner.getPet("buddy")).isSameAs(pet);
		assertThat(owner.getPet("BUDDY")).isSameAs(pet);
	}

	@Test
	void getPetByNameReturnsNullWhenNotFound() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);

		assertThat(owner.getPet("Max")).isNull();
	}

	@Test
	void getPetByNameReturnsNullForNullPetName() {
		Pet pet = new Pet();
		// pet name is null by default
		owner.addPet(pet);

		assertThat(owner.getPet("Buddy")).isNull();
	}

	@Test
	void getPetByNameWithIgnoreNewSkipsNewPets() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		// pet.id is null, so pet.isNew() == true
		owner.addPet(pet);

		// ignoreNew=false should find it
		assertThat(owner.getPet("Buddy", false)).isSameAs(pet);

		// ignoreNew=true should skip it because pet is new (no id)
		assertThat(owner.getPet("Buddy", true)).isNull();
	}

	@Test
	void getPetByNameWithIgnoreNewReturnsSavedPets() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		// add while new, then assign an id to simulate a persisted pet
		owner.addPet(pet);
		pet.setId(10);

		// ignoreNew=true should still return a saved pet
		assertThat(owner.getPet("Buddy", true)).isSameAs(pet);
	}

	// --- getPet(Integer) ---

	@Test
	void getPetByIdReturnsPetWhenFound() {
		Pet pet = new Pet();
		pet.setName("Max");
		// add while new, then assign an id to simulate a persisted pet
		owner.addPet(pet);
		pet.setId(5);

		assertThat(owner.getPet(Integer.valueOf(5))).isSameAs(pet);
	}

	@Test
	void getPetByIdReturnsNullWhenNotFound() {
		Pet pet = new Pet();
		pet.setName("Max");
		owner.addPet(pet);
		pet.setId(5);

		assertThat(owner.getPet(Integer.valueOf(99))).isNull();
	}

	@Test
	void getPetByIdSkipsNewPets() {
		Pet pet = new Pet();
		// pet.id is null → isNew() == true
		pet.setName("Max");
		owner.addPet(pet);

		assertThat(owner.getPet(Integer.valueOf(1))).isNull();
	}

	// --- addPet ---

	@Test
	void addPetAddsNewPetToList() {
		Pet pet = new Pet();
		pet.setName("Buddy");

		owner.addPet(pet);

		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets()).contains(pet);
	}

	@Test
	void addPetDoesNotAddNonNewPet() {
		Pet pet = new Pet();
		pet.setId(10);
		pet.setName("Buddy");

		owner.addPet(pet);

		// Pet with an id is not "new", so it should NOT be added
		assertThat(owner.getPets()).isEmpty();
	}

	// --- addVisit ---

	@Test
	void addVisitAddsToPet() {
		Pet pet = new Pet();
		pet.setName("Max");
		owner.addPet(pet);
		pet.setId(5);

		Visit visit = new Visit();
		visit.setDate(LocalDate.of(2025, 3, 1));
		visit.setDescription("checkup");

		owner.addVisit(5, visit);

		assertThat(pet.getVisits()).hasSize(1);
		assertThat(pet.getVisits().iterator().next().getDescription()).isEqualTo("checkup");
	}

	@Test
	void addVisitThrowsForNullPetId() {
		assertThatThrownBy(() -> owner.addVisit(null, new Visit())).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void addVisitThrowsForNullVisit() {
		assertThatThrownBy(() -> owner.addVisit(1, null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void addVisitThrowsForInvalidPetId() {
		Pet pet = new Pet();
		pet.setName("Max");
		owner.addPet(pet);
		pet.setId(5);

		assertThatThrownBy(() -> owner.addVisit(99, new Visit())).isInstanceOf(IllegalArgumentException.class);
	}

	// --- toString ---

	@Test
	void toStringContainsOwnerFields() {
		String result = owner.toString();

		assertThat(result).contains("George");
		assertThat(result).contains("Franklin");
		assertThat(result).contains("110 W. Liberty St.");
		assertThat(result).contains("Madison");
		assertThat(result).contains("6085551023");
	}

}
