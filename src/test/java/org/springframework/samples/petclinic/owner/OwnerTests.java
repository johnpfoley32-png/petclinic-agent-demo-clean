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
 * Unit tests for {@link Owner} domain logic — specifically the pet-lookup and
 * visit-addition methods that carry branching logic.
 */
class OwnerTests {

	private Owner owner;

	private Pet savedPet;

	@BeforeEach
	void setup() {
		owner = new Owner();
		owner.setFirstName("George");
		owner.setLastName("Franklin");

		savedPet = new Pet();
		savedPet.setName("Leo");
		// Pet starts as "new" (id == null), so addPet accepts it
		owner.addPet(savedPet);
		// Simulate persistence by assigning an id
		savedPet.setId(1);
	}

	// ---- addPet ----

	@Test
	void addPetShouldAddNewPet() {
		Pet newPet = new Pet();
		newPet.setName("Milo");
		// newPet.isNew() == true → should be added
		owner.addPet(newPet);
		assertThat(owner.getPets()).contains(newPet);
	}

	@Test
	void addPetShouldIgnoreAlreadyPersistedPet() {
		Pet persisted = new Pet();
		persisted.setName("Rex");
		persisted.setId(99);
		// persisted.isNew() == false → should NOT be added
		int sizeBefore = owner.getPets().size();
		owner.addPet(persisted);
		assertThat(owner.getPets()).hasSize(sizeBefore);
	}

	// ---- getPet(Integer id) ----

	@Test
	void getPetByIdShouldReturnMatchingPet() {
		assertThat(owner.getPet(1)).isSameAs(savedPet);
	}

	@Test
	void getPetByIdShouldReturnNullForUnknownId() {
		assertThat(owner.getPet(999)).isNull();
	}

	@Test
	void getPetByIdShouldSkipNewPets() {
		Pet unsaved = new Pet();
		unsaved.setName("Ghost");
		owner.addPet(unsaved);
		// unsaved has no id → isNew() == true → skipped by getPet(Integer)
		assertThat(owner.getPet((Integer) null)).isNull();
	}

	// ---- getPet(String name) / getPet(String name, boolean ignoreNew) ----

	@Test
	void getPetByNameShouldReturnMatchIgnoringCase() {
		assertThat(owner.getPet("leo")).isSameAs(savedPet);
		assertThat(owner.getPet("LEO")).isSameAs(savedPet);
	}

	@Test
	void getPetByNameShouldReturnNullWhenNotFound() {
		assertThat(owner.getPet("nonexistent")).isNull();
	}

	@Test
	void getPetByNameShouldIgnoreNewPetsWhenFlagIsTrue() {
		Pet unsaved = new Pet();
		unsaved.setName("Phantom");
		owner.addPet(unsaved);
		// ignoreNew = true → unsaved pet should be skipped
		assertThat(owner.getPet("Phantom", true)).isNull();
	}

	@Test
	void getPetByNameShouldReturnNewPetsWhenFlagIsFalse() {
		Pet unsaved = new Pet();
		unsaved.setName("Phantom");
		owner.addPet(unsaved);
		// ignoreNew = false → unsaved pet should be returned
		assertThat(owner.getPet("Phantom", false)).isSameAs(unsaved);
	}

	@Test
	void getPetByNameShouldSkipPetsWithNullName() {
		Pet nameless = new Pet();
		// name is null
		owner.addPet(nameless);
		assertThat(owner.getPet("Leo")).isSameAs(savedPet);
		assertThat(owner.getPet((String) null)).isNull();
	}

	// ---- addVisit ----

	@Test
	void addVisitShouldAttachVisitToPet() {
		Visit visit = new Visit();
		visit.setDescription("checkup");
		owner.addVisit(1, visit);
		assertThat(savedPet.getVisits()).contains(visit);
	}

	@Test
	void addVisitShouldThrowForNullPetId() {
		Visit visit = new Visit();
		assertThatThrownBy(() -> owner.addVisit(null, visit)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void addVisitShouldThrowForNullVisit() {
		assertThatThrownBy(() -> owner.addVisit(1, null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void addVisitShouldThrowForInvalidPetId() {
		Visit visit = new Visit();
		assertThatThrownBy(() -> owner.addVisit(999, visit)).isInstanceOf(IllegalArgumentException.class);
	}

	// ---- toString ----

	@Test
	void toStringShouldContainOwnerFields() {
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");
		String result = owner.toString();
		assertThat(result).contains("Franklin").contains("George").contains("Madison");
	}

}
