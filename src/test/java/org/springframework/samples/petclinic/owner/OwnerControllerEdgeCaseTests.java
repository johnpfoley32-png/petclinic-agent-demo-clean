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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Edge-case tests for {@link OwnerController} covering previously uncovered branches:
 * <ul>
 * <li>Owner not found in findOwner @ModelAttribute (orElseThrow lambda)</li>
 * <li>Owner not found in showOwner (orElseThrow lambda)</li>
 * </ul>
 */
@WebMvcTest(OwnerController.class)
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerEdgeCaseTests {

	private static final int MISSING_OWNER_ID = 999;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@BeforeEach
	void setup() {
		given(this.owners.findById(MISSING_OWNER_ID)).willReturn(Optional.empty());
	}

	@Test
	void testFindOwnerThrowsWhenOwnerNotFound() {
		// Exercises the orElseThrow lambda in findOwner (line 68-69)
		// Triggered by any endpoint that resolves @ModelAttribute("owner") with a
		// non-null ownerId that doesn't exist
		assertThatThrownBy(() -> mockMvc.perform(get("/owners/{ownerId}/edit", MISSING_OWNER_ID))).rootCause()
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Owner not found with id: " + MISSING_OWNER_ID);
	}

	@Test
	void testShowOwnerThrowsWhenOwnerNotFound() {
		// Exercises the orElseThrow lambda in showOwner (line 170-171)
		assertThatThrownBy(() -> mockMvc.perform(get("/owners/{ownerId}", MISSING_OWNER_ID))).rootCause()
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Owner not found with id: " + MISSING_OWNER_ID);
	}

}
