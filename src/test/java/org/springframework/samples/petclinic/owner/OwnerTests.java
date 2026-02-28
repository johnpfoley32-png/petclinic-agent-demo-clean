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
 * Unit tests for {@link Owner} domain logic, focusing on pet lookup and visit management.
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

	@Test
	void addPetShouldAddNewPet() {
		Pet pet = new Pet();
		pet.setName("Fido");
		owner.addPet(pet);
		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets().get(0).getName()).isEqualTo("Fido");
	}

	@Test
	void addPetShouldNotAddExistingPet() {
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Fido");
		owner.addPet(pet);
		assertThat(owner.getPets()).isEmpty();
	}

	@Test
	void getPetByIdShouldReturnMatchingPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		pet.setId(7);
		assertThat(owner.getPet(7)).isSameAs(pet);
	}

	@Test
	void getPetByIdShouldReturnNullWhenNoMatch() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		pet.setId(7);
		assertThat(owner.getPet(99)).isNull();
	}

	@Test
	void getPetByIdShouldSkipNewPets() {
		Pet newPet = new Pet();
		newPet.setName("Newcomer");
		owner.addPet(newPet);
		// newPet has no id set, so isNew() returns true
		assertThat(owner.getPet(1)).isNull();
	}

	@Test
	void getPetByNameShouldReturnMatchingPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		assertThat(owner.getPet("Buddy")).isSameAs(pet);
	}

	@Test
	void getPetByNameShouldBeCaseInsensitive() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		assertThat(owner.getPet("buddy")).isSameAs(pet);
		assertThat(owner.getPet("BUDDY")).isSameAs(pet);
	}

	@Test
	void getPetByNameShouldReturnNullWhenNoMatch() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		assertThat(owner.getPet("Unknown")).isNull();
	}

	@Test
	void getPetByNameShouldSkipPetsWithNullName() {
		Pet pet = new Pet();
		// name is null
		owner.addPet(pet);
		assertThat(owner.getPet("Buddy")).isNull();
	}

	@Test
	void getPetByNameIgnoreNewShouldSkipNewPetsWhenFlagSet() {
		Pet newPet = new Pet();
		newPet.setName("Newcomer");
		// no id, so isNew() == true
		owner.addPet(newPet);
		assertThat(owner.getPet("Newcomer", true)).isNull();
	}

	@Test
	void getPetByNameIgnoreNewShouldReturnNewPetsWhenFlagNotSet() {
		Pet newPet = new Pet();
		newPet.setName("Newcomer");
		owner.addPet(newPet);
		assertThat(owner.getPet("Newcomer", false)).isSameAs(newPet);
	}

	@Test
	void getPetByNameIgnoreNewShouldReturnSavedPetWhenFlagSet() {
		Pet savedPet = new Pet();
		savedPet.setName("Saved");
		owner.addPet(savedPet);
		savedPet.setId(10);
		assertThat(owner.getPet("Saved", true)).isSameAs(savedPet);
	}

	@Test
	void addVisitShouldAddVisitToCorrectPet() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		pet.setId(5);

		Visit visit = new Visit();
		visit.setDescription("checkup");
		owner.addVisit(5, visit);

		assertThat(pet.getVisits()).hasSize(1);
		assertThat(pet.getVisits().iterator().next().getDescription()).isEqualTo("checkup");
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

	@Test
	void toStringShouldContainOwnerFields() {
		owner.setId(1);
		String result = owner.toString();
		assertThat(result).contains("George");
		assertThat(result).contains("Franklin");
		assertThat(result).contains("110 W. Liberty St.");
		assertThat(result).contains("Madison");
		assertThat(result).contains("6085551023");
	}

}
