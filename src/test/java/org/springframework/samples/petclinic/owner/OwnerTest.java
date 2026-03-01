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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link Owner}.
 */
class OwnerTest {

	@Test
	void testAddPetAndRetrieve() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");

		owner.addPet(pet);

		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets().get(0).getName()).isEqualTo("Buddy");
	}

	@Test
	void testAddPetIgnoresExistingPet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setId(1); // Not new — has an ID

		owner.addPet(pet);

		assertThat(owner.getPets()).isEmpty();
	}

	@Test
	void testGetPetByName() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);

		Pet found = owner.getPet("Buddy");
		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("Buddy");
	}

	@Test
	void testGetPetByNameCaseInsensitive() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);

		Pet found = owner.getPet("buddy");
		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("Buddy");
	}

	@Test
	void testGetPetByNameNotFound() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);

		Pet found = owner.getPet("Max");
		assertThat(found).isNull();
	}

	@Test
	void testGetPetByNameIgnoreNew() {
		Owner owner = new Owner();
		Pet newPet = new Pet();
		newPet.setName("Buddy");
		owner.addPet(newPet);

		// ignoreNew = true should skip pets without an id
		Pet found = owner.getPet("Buddy", true);
		assertThat(found).isNull();

		// ignoreNew = false should find the pet
		Pet foundNotIgnoring = owner.getPet("Buddy", false);
		assertThat(foundNotIgnoring).isNotNull();
	}

	@Test
	void testGetPetById() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		// Simulate persistence by setting an ID after adding
		pet.setId(7);

		Pet found = owner.getPet(Integer.valueOf(7));
		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("Buddy");
	}

	@Test
	void testGetPetByIdNotFound() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		pet.setId(7);

		Pet found = owner.getPet(Integer.valueOf(999));
		assertThat(found).isNull();
	}

	@Test
	void testAddVisit() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		pet.setId(1);

		Visit visit = new Visit();
		visit.setDate(LocalDate.of(2025, 3, 15));
		visit.setDescription("annual checkup");

		owner.addVisit(1, visit);

		assertThat(pet.getVisits()).hasSize(1);
		assertThat(pet.getVisits().iterator().next().getDescription()).isEqualTo("annual checkup");
	}

	@Test
	void testAddVisitWithNullPetIdThrows() {
		Owner owner = new Owner();

		Visit visit = new Visit();
		visit.setDescription("checkup");

		assertThatThrownBy(() -> owner.addVisit(null, visit)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testAddVisitWithNullVisitThrows() {
		Owner owner = new Owner();

		assertThatThrownBy(() -> owner.addVisit(1, null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testAddVisitWithInvalidPetIdThrows() {
		Owner owner = new Owner();

		Visit visit = new Visit();
		visit.setDescription("checkup");

		assertThatThrownBy(() -> owner.addVisit(999, visit)).isInstanceOf(IllegalArgumentException.class);
	}

}
