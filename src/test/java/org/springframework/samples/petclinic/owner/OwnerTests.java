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
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Owner}.
 * <p>
 * Focuses on domain logic in {@code addPet}, {@code getPet(Integer)},
 * {@code getPet(String, boolean)}, and {@code addVisit}.
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

	// ── addPet ──────────────────────────────────────────────────────────

	@Test
	void addPetShouldAddNewPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		// pet has no id → isNew() == true
		owner.addPet(pet);
		assertThat(owner.getPets()).contains(pet);
	}

	@Test
	void addPetShouldNotAddExistingPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setId(42); // non-null id → isNew() == false
		owner.addPet(pet);
		assertThat(owner.getPets()).doesNotContain(pet);
	}

	// ── getPet(Integer) ─────────────────────────────────────────────────

	@Test
	void getPetByIdShouldReturnMatchingPet() {
		Pet pet = new Pet();
		pet.setName("Leo");
		owner.addPet(pet);
		pet.setId(7);

		assertThat(owner.getPet(7)).isSameAs(pet);
	}

	@Test
	void getPetByIdShouldReturnNullWhenNoMatch() {
		Pet pet = new Pet();
		pet.setName("Leo");
		owner.addPet(pet);
		pet.setId(7);

		assertThat(owner.getPet(999)).isNull();
	}

	@Test
	void getPetByIdShouldSkipNewPets() {
		Pet newPet = new Pet();
		newPet.setName("Ghost");
		// newPet has no id → isNew() == true
		owner.addPet(newPet);

		// Even though the pet is in the list, getPet(Integer) should skip it
		assertThat(owner.getPet(1)).isNull();
	}

	@Test
	void getPetByIdShouldReturnNullForEmptyPetsList() {
		assertThat(owner.getPet(1)).isNull();
	}

	// ── getPet(String) / getPet(String, boolean) ────────────────────────

	@Test
	void getPetByNameShouldReturnMatchingPet() {
		Pet pet = new Pet();
		pet.setName("Bella");
		owner.addPet(pet);

		assertThat(owner.getPet("Bella")).isSameAs(pet);
	}

	@Test
	void getPetByNameShouldBeCaseInsensitive() {
		Pet pet = new Pet();
		pet.setName("Bella");
		owner.addPet(pet);

		assertThat(owner.getPet("bella")).isSameAs(pet);
		assertThat(owner.getPet("BELLA")).isSameAs(pet);
	}

	@Test
	void getPetByNameShouldReturnNullWhenNoMatch() {
		Pet pet = new Pet();
		pet.setName("Bella");
		owner.addPet(pet);

		assertThat(owner.getPet("Max")).isNull();
	}

	@Test
	void getPetByNameShouldSkipPetsWithNullName() {
		Pet pet = new Pet();
		// name is null by default
		owner.addPet(pet);

		assertThat(owner.getPet("anything")).isNull();
	}

	@Test
	void getPetByNameIgnoreNewShouldSkipNewPets() {
		Pet newPet = new Pet();
		newPet.setName("Shadow");
		// newPet has no id → isNew() == true
		owner.addPet(newPet);

		// ignoreNew = true should skip unsaved pets
		assertThat(owner.getPet("Shadow", true)).isNull();
	}

	@Test
	void getPetByNameIgnoreNewShouldReturnSavedPet() {
		Pet savedPet = new Pet();
		savedPet.setName("Shadow");
		owner.addPet(savedPet);
		savedPet.setId(10); // mark as saved

		assertThat(owner.getPet("Shadow", true)).isSameAs(savedPet);
	}

	// ── addVisit ────────────────────────────────────────────────────────

	@Test
	void addVisitShouldAttachVisitToCorrectPet() {
		Pet pet = new Pet();
		pet.setName("Rex");
		owner.addPet(pet);
		pet.setId(5);

		Visit visit = new Visit();
		visit.setDescription("annual checkup");

		owner.addVisit(5, visit);

		assertThat(pet.getVisits()).contains(visit);
	}

	@Test
	void addVisitShouldThrowWhenPetIdIsNull() {
		assertThatThrownBy(() -> owner.addVisit(null, new Visit())).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void addVisitShouldThrowWhenVisitIsNull() {
		assertThatThrownBy(() -> owner.addVisit(1, null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void addVisitShouldThrowWhenPetNotFound() {
		assertThatThrownBy(() -> owner.addVisit(999, new Visit())).isInstanceOf(IllegalArgumentException.class);
	}

	// ── toString ────────────────────────────────────────────────────────

	@Test
	void toStringShouldContainOwnerFields() {
		String result = owner.toString();
		assertThat(result).contains("lastName", "Franklin");
		assertThat(result).contains("firstName", "George");
		assertThat(result).contains("address", "110 W. Liberty St.");
		assertThat(result).contains("city", "Madison");
		assertThat(result).contains("telephone", "6085551023");
	}

}
