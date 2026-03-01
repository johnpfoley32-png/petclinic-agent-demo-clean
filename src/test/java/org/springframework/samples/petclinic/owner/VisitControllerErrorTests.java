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

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Tests for {@link VisitController} error paths that are not covered by
 * {@link VisitControllerTests}.
 * <p>
 * Specifically tests the IllegalArgumentException paths in loadPetWithVisit when the
 * owner is not found and when the pet is not found on the owner.
 */
@WebMvcTest(VisitController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerErrorTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@Test
	void loadPetWithVisitThrowsWhenOwnerNotFound() {
		given(this.owners.findById(999)).willReturn(Optional.empty());

		assertThatThrownBy(() -> mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", 999, 1))).rootCause()
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Owner not found with id: 999");
	}

	@Test
	void loadPetWithVisitThrowsWhenPetNotFoundOnOwner() {
		Owner owner = new Owner();
		owner.setId(1);
		// owner has no pets
		given(this.owners.findById(1)).willReturn(Optional.of(owner));

		assertThatThrownBy(() -> mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", 1, 99))).rootCause()
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Pet with id 99 not found");
	}

}
