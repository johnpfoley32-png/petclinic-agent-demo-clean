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
package org.springframework.samples.petclinic.vet;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Vet} specialty management and {@link Vets} list wrapper.
 * <p>
 * Covers addSpecialty, getSpecialties (sorted), getNrOfSpecialties, and the lazy
 * initialization of the internal specialties set. Also covers the Vets wrapper.
 */
class VetSpecialtyTests {

	// --- Vet specialty management ---

	@Test
	void newVetHasNoSpecialties() {
		Vet vet = new Vet();
		assertThat(vet.getNrOfSpecialties()).isZero();
		assertThat(vet.getSpecialties()).isEmpty();
	}

	@Test
	void addSpecialtyIncreasesCount() {
		Vet vet = new Vet();
		Specialty surgery = new Specialty();
		surgery.setName("surgery");

		vet.addSpecialty(surgery);

		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
		assertThat(vet.getSpecialties()).hasSize(1);
		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("surgery");
	}

	@Test
	void addMultipleSpecialties() {
		Vet vet = new Vet();
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		Specialty dentistry = new Specialty();
		dentistry.setName("dentistry");
		Specialty radiology = new Specialty();
		radiology.setName("radiology");

		vet.addSpecialty(surgery);
		vet.addSpecialty(dentistry);
		vet.addSpecialty(radiology);

		assertThat(vet.getNrOfSpecialties()).isEqualTo(3);
	}

	@Test
	void getSpecialtiesReturnsSortedByName() {
		Vet vet = new Vet();
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		Specialty dentistry = new Specialty();
		dentistry.setName("dentistry");
		Specialty radiology = new Specialty();
		radiology.setName("radiology");

		vet.addSpecialty(surgery);
		vet.addSpecialty(dentistry);
		vet.addSpecialty(radiology);

		List<Specialty> sorted = vet.getSpecialties();
		assertThat(sorted).extracting(Specialty::getName).containsExactly("dentistry", "radiology", "surgery");
	}

	@Test
	void getSpecialtiesReturnsNewListEachTime() {
		Vet vet = new Vet();
		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		vet.addSpecialty(surgery);

		List<Specialty> first = vet.getSpecialties();
		List<Specialty> second = vet.getSpecialties();

		assertThat(first).isNotSameAs(second);
		assertThat(first).isEqualTo(second);
	}

	// --- Vets wrapper ---

	@Test
	void vetsGetVetListReturnsEmptyListByDefault() {
		Vets vets = new Vets();
		assertThat(vets.getVetList()).isNotNull();
		assertThat(vets.getVetList()).isEmpty();
	}

	@Test
	void vetsGetVetListIsLazilyInitialized() {
		Vets vets = new Vets();
		List<Vet> list1 = vets.getVetList();
		List<Vet> list2 = vets.getVetList();

		// Should return the same list instance after initialization
		assertThat(list1).isSameAs(list2);
	}

	@Test
	void vetsGetVetListCanBePopulated() {
		Vets vets = new Vets();
		Vet vet = new Vet();
		vet.setFirstName("James");
		vet.setLastName("Carter");
		vets.getVetList().add(vet);

		assertThat(vets.getVetList()).hasSize(1);
		assertThat(vets.getVetList().get(0).getFirstName()).isEqualTo("James");
	}

}
