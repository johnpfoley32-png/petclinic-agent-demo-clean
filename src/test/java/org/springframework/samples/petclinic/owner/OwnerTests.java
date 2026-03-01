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
 * Unit tests for {@link Owner} domain logic.
 * <p>
 * Covers branch paths in {@code addPet}, {@code getPet(String)},
 * {@code getPet(String, boolean)}, {@code getPet(Integer)}, {@code addVisit}, and
 * {@code toString}.
 */
class OwnerTests {

	private Owner owner;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");
	}

	// --- addPet ---

	@Test
	void addPetShouldAddNewPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		// pet has no id, so isNew() == true
		owner.addPet(pet);

		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets()).contains(pet);
	}

	@Test
	void addPetShouldNotAddExistingPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setId(7); // not new
		owner.addPet(pet);

		assertThat(owner.getPets()).doesNotContain(pet);
	}

	// --- getPet(String) delegates to getPet(String, false) ---

	@Test
	void getPetByNameShouldReturnMatchingPet() {
		Pet pet = new Pet();
		pet.setName("Rosy");
		owner.addPet(pet);

		Pet found = owner.getPet("Rosy");
		assertThat(found).isSameAs(pet);
	}

	@Test
	void getPetByNameShouldBeCaseInsensitive() {
		Pet pet = new Pet();
		pet.setName("Rosy");
		owner.addPet(pet);

		Pet found = owner.getPet("rosy");
		assertThat(found).isSameAs(pet);
	}

	@Test
	void getPetByNameShouldReturnNullWhenNotFound() {
		Pet pet = new Pet();
		pet.setName("Rosy");
		owner.addPet(pet);

		assertThat(owner.getPet("Max")).isNull();
	}

	@Test
	void getPetByNameShouldReturnNullForEmptyList() {
		assertThat(owner.getPet("Anything")).isNull();
	}

	@Test
	void getPetByNameShouldSkipPetsWithNullName() {
		Pet petNoName = new Pet();
		// name is null by default
		owner.addPet(petNoName);

		assertThat(owner.getPet("Rosy")).isNull();
	}

	// --- getPet(String, boolean ignoreNew) ---

	@Test
	void getPetByNameIgnoreNewShouldReturnNullForNewPet() {
		Pet pet = new Pet();
		pet.setName("Rosy");
		// pet.isNew() == true (no id set)
		owner.addPet(pet);

		Pet found = owner.getPet("Rosy", true);
		assertThat(found).isNull();
	}

	@Test
	void getPetByNameIgnoreNewShouldReturnSavedPet() {
		Pet pet = new Pet();
		pet.setName("Rosy");
		pet.setId(5); // not new
		// Must add directly since addPet() skips non-new pets
		owner.getPets().add(pet);

		Pet found = owner.getPet("Rosy", true);
		assertThat(found).isSameAs(pet);
	}

	@Test
	void getPetByNameDoNotIgnoreNewShouldReturnNewPet() {
		Pet pet = new Pet();
		pet.setName("Rosy");
		// pet.isNew() == true
		owner.addPet(pet);

		Pet found = owner.getPet("Rosy", false);
		assertThat(found).isSameAs(pet);
	}

	// --- getPet(Integer) ---

	@Test
	void getPetByIdShouldReturnMatchingPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setId(10);
		owner.getPets().add(pet);

		Pet found = owner.getPet(10);
		assertThat(found).isSameAs(pet);
	}

	@Test
	void getPetByIdShouldReturnNullWhenNotFound() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setId(10);
		owner.getPets().add(pet);

		assertThat(owner.getPet(99)).isNull();
	}

	@Test
	void getPetByIdShouldSkipNewPets() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		// id is null => isNew() == true
		owner.addPet(pet);

		assertThat(owner.getPet(1)).isNull();
	}

	@Test
	void getPetByIdShouldReturnNullForEmptyList() {
		assertThat(owner.getPet(1)).isNull();
	}

	// --- addVisit ---

	@Test
	void addVisitShouldAddVisitToCorrectPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setId(3);
		owner.getPets().add(pet);

		Visit visit = new Visit();
		visit.setDescription("checkup");
		owner.addVisit(3, visit);

		assertThat(pet.getVisits()).hasSize(1);
		assertThat(pet.getVisits().iterator().next().getDescription()).isEqualTo("checkup");
	}

	@Test
	void addVisitShouldThrowForNullPetId() {
		assertThatThrownBy(() -> owner.addVisit(null, new Visit())).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void addVisitShouldThrowForNullVisit() {
		assertThatThrownBy(() -> owner.addVisit(1, null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void addVisitShouldThrowForInvalidPetId() {
		assertThatThrownBy(() -> owner.addVisit(999, new Visit())).isInstanceOf(IllegalArgumentException.class);
	}

	// --- toString ---

	@Test
	void toStringShouldContainOwnerFields() {
		owner.setId(1);
		String result = owner.toString();

		assertThat(result).contains("id = 1");
		assertThat(result).contains("lastName = 'Franklin'");
		assertThat(result).contains("firstName = 'George'");
		assertThat(result).contains("address = '110 W. Liberty St.'");
		assertThat(result).contains("city = 'Madison'");
		assertThat(result).contains("telephone = '6085551023'");
	}

	// --- getters/setters ---

	@Test
	void gettersAndSettersShouldWork() {
		assertThat(owner.getAddress()).isEqualTo("110 W. Liberty St.");
		assertThat(owner.getCity()).isEqualTo("Madison");
		assertThat(owner.getTelephone()).isEqualTo("6085551023");

		owner.setAddress("new address");
		owner.setCity("new city");
		owner.setTelephone("9999999999");

		assertThat(owner.getAddress()).isEqualTo("new address");
		assertThat(owner.getCity()).isEqualTo("new city");
		assertThat(owner.getTelephone()).isEqualTo("9999999999");
	}

}
