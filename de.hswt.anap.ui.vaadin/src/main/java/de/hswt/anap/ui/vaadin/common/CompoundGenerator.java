/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hswt.anap.ui.vaadin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import de.hswt.anap.model.CompoundsDeclaration;
import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.SubstanceConfiguration;
import de.hswt.anap.service.standards.api.IStandardsService;
import de.hswt.hplcsl.Compound;

@SpringComponent
@ViewScope
public class CompoundGenerator {

	@Autowired
	private IStandardsService standardsService;

	public List<Compound> getCompounds(Experiment experiment) {

		List<Compound> choosenCompounds = new ArrayList<>();

		List<CompoundsDeclaration> declaration = standardsService
				.getCompoundDeclarations();
		Optional<CompoundsDeclaration> columnSubstances = declaration
				.stream()
				.filter(e -> e.getColumn().getName()
						.equals(experiment.getColumn().getName())
						&& e.getSolvent().equals(experiment.getSolventB()))
				.findFirst();

		for (SubstanceConfiguration substance : experiment.getSubstances()) {
			for (Compound compound : columnSubstances.get().getCompounds()) {
				if (substance.getName().equals(compound.getCompoundName())) {
					compound.setConcentration(30);
					choosenCompounds.add(compound);
				}
			}
		}

		return choosenCompounds;
	}
}
