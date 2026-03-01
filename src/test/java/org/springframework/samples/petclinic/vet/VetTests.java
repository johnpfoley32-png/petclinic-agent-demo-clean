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
import org.springframework.util.SerializationUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 */
class VetTests {

	@Test
	void testSerialization() {
		Vet vet = new Vet();
		vet.setFirstName("Zaphod");
		vet.setLastName("Beeblebrox");
		vet.setId(123);
		@SuppressWarnings("deprecation")
		Vet other = (Vet) SerializationUtils.deserialize(SerializationUtils.serialize(vet));
		assertThat(other.getFirstName()).isEqualTo(vet.getFirstName());
		assertThat(other.getLastName()).isEqualTo(vet.getLastName());
		assertThat(other.getId()).isEqualTo(vet.getId());
	}

	@Test
	void testAddSpecialty() {
		Vet vet = new Vet();
		Specialty surgery = new Specialty();
		surgery.setName("surgery");

		vet.addSpecialty(surgery);

		assertThat(vet.getSpecialties()).hasSize(1);
		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("surgery");
	}

	@Test
	void testGetNrOfSpecialties() {
		Vet vet = new Vet();
		assertThat(vet.getNrOfSpecialties()).isEqualTo(0);

		Specialty surgery = new Specialty();
		surgery.setName("surgery");
		vet.addSpecialty(surgery);
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);

		Specialty radiology = new Specialty();
		radiology.setName("radiology");
		vet.addSpecialty(radiology);
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
	}

	@Test
	void testSpecialtiesAreSortedByName() {
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

		List<Specialty> specialties = vet.getSpecialties();
		assertThat(specialties).hasSize(3);
		assertThat(specialties.get(0).getName()).isEqualTo("dentistry");
		assertThat(specialties.get(1).getName()).isEqualTo("radiology");
		assertThat(specialties.get(2).getName()).isEqualTo("surgery");
	}

}
