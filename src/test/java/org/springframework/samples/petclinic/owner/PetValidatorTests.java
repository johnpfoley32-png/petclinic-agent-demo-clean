/*
 * Copyright 2012-2024 the original author or authors.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link PetValidator}
 *
 * @author Wick Dynex
 */
@ExtendWith(MockitoExtension.class)
@DisabledInNativeImage
public class PetValidatorTests {

	private PetValidator petValidator;

	private Pet pet;

	private PetType petType;

	private Errors errors;

	private static final String petName = "Buddy";

	private static final String petTypeName = "Dog";

	private static final LocalDate petBirthDate = LocalDate.of(1990, 1, 1);

	@BeforeEach
	void setUp() {
		petValidator = new PetValidator();
		pet = new Pet();
		petType = new PetType();
		errors = new MapBindingResult(new HashMap<>(), "pet");
	}

	@Test
	void testValidPet() {
		petType.setName(petTypeName);
		pet.setName(petName);
		pet.setType(petType);
		pet.setBirthDate(petBirthDate);

		petValidator.validate(pet, errors);

		assertFalse(errors.hasErrors());
	}

	@Test
	void testValidPetWithFutureBirthDate() {
		petType.setName(petTypeName);
		pet.setName(petName);
		pet.setType(petType);
		pet.setBirthDate(LocalDate.now().plusYears(1));

		petValidator.validate(pet, errors);

		// PetValidator only checks for null birthDate, not future dates
		assertFalse(errors.hasFieldErrors("birthDate"));
	}

	@Test
	void testValidPetWithDuplicateName() {
		// Set up an owner with an existing pet named "Buddy"
		Owner owner = new Owner();
		Pet existingPet = new Pet();
		existingPet.setName(petName);
		owner.addPet(existingPet);

		// Create a new pet with the same name
		petType.setName(petTypeName);
		pet.setName(petName);
		pet.setType(petType);
		pet.setBirthDate(petBirthDate);
		owner.addPet(pet);

		petValidator.validate(pet, errors);

		// PetValidator does not check for duplicate names within an owner
		assertFalse(errors.hasErrors());
	}

	@Nested
	class ValidateHasErrors {

		@Test
		void testValidateWithBlankPetName() {
			petType.setName(petTypeName);
			pet.setName("");
			pet.setType(petType);
			pet.setBirthDate(petBirthDate);

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("name"));
		}

		@Test
		void testValidateWithNullPetName() {
			petType.setName(petTypeName);
			pet.setType(petType);
			pet.setBirthDate(petBirthDate);
			// name is null by default

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("name"));
		}

		@Test
		void testValidateNewPetWithNullType() {
			pet.setName(petName);
			pet.setType(null);
			pet.setBirthDate(petBirthDate);

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("type"));
		}

		@Test
		void testValidateWithNullBirthDate() {
			petType.setName(petTypeName);
			pet.setName(petName);
			pet.setType(petType);
			pet.setBirthDate(null);

			petValidator.validate(pet, errors);

			assertTrue(errors.hasFieldErrors("birthDate"));
		}

	}

	@Nested
	class ValidateExistingPet {

		@Test
		void testExistingPetWithNullTypeDoesNotProduceTypeError() {
			// An existing pet (id set) with null type should NOT get a type error
			// because PetValidator only requires type for new pets
			pet.setId(1);
			pet.setName(petName);
			pet.setType(null);
			pet.setBirthDate(petBirthDate);

			petValidator.validate(pet, errors);

			assertFalse(errors.hasFieldErrors("type"));
		}

	}

	@Nested
	class Supports {

		@Test
		void testSupportsPetClass() {
			assertTrue(petValidator.supports(Pet.class));
		}

		@Test
		void testDoesNotSupportNonPetClass() {
			assertFalse(petValidator.supports(Owner.class));
		}

	}

}
