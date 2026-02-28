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
 * Unit tests for {@link Owner} domain object, focusing on branch coverage for
 * {@code addPet}, {@code getPet(Integer)}, and {@code getPet(String, boolean)}.
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
	void addPetShouldNotAddWhenPetIsNotNew() {
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Buddy");

		owner.addPet(pet);

		assertThat(owner.getPets()).doesNotContain(pet);
	}

	@Test
	void addPetShouldAddWhenPetIsNew() {
		Pet pet = new Pet();
		pet.setName("Buddy");

		owner.addPet(pet);

		assertThat(owner.getPets()).contains(pet);
	}

	@Test
	void getPetByIdShouldReturnNullWhenNoPetsMatch() {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setId(1);
		owner.getPets().add(pet);

		assertThat(owner.getPet(999)).isNull();
	}

	@Test
	void getPetByIdShouldSkipNewPets() {
		Pet newPet = new Pet();
		newPet.setName("NewPet");
		// newPet has no id set, so isNew() returns true
		owner.getPets().add(newPet);

		assertThat(owner.getPet(1)).isNull();
	}

	@Test
	void getPetByIdShouldReturnMatchingPet() {
		Pet pet = new Pet();
		pet.setId(7);
		pet.setName("Lucky");
		owner.getPets().add(pet);

		assertThat(owner.getPet(7)).isEqualTo(pet);
	}

	@Test
	void getPetByNameShouldReturnNullWhenPetNameIsNull() {
		Pet pet = new Pet();
		// pet.getName() is null
		owner.getPets().add(pet);

		assertThat(owner.getPet("Buddy", false)).isNull();
	}

	@Test
	void getPetByNameWithIgnoreNewShouldSkipNewPets() {
		Pet newPet = new Pet();
		newPet.setName("Buddy");
		// newPet has no id, so isNew() returns true
		owner.getPets().add(newPet);

		// ignoreNew = true should skip this new pet
		assertThat(owner.getPet("Buddy", true)).isNull();
	}

	@Test
	void getPetByNameWithIgnoreNewShouldReturnSavedPet() {
		Pet savedPet = new Pet();
		savedPet.setId(1);
		savedPet.setName("Buddy");
		owner.getPets().add(savedPet);

		// ignoreNew = true should still return a saved pet
		assertThat(owner.getPet("Buddy", true)).isEqualTo(savedPet);
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
		// No pets added, so getPet(1) returns null
		assertThatThrownBy(() -> owner.addVisit(1, new Visit())).isInstanceOf(IllegalArgumentException.class);
	}

}
