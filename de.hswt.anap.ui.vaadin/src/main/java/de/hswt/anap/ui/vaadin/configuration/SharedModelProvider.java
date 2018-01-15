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

package de.hswt.anap.ui.vaadin.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.spring.annotation.ViewScope;

import de.hswt.anap.model.Experiment;
import de.hswt.anap.service.chromatogram.api.IChromatogramService;
import de.hswt.hplcsl.Definition;

@Component
@ViewScope
public class SharedModelProvider {

	@Autowired
	private IChromatogramService chromatogramService;
	
	private Definition definition;
	
	private Experiment experiment;
	
	public Definition getDefinition() {
		if (definition == null) {
			definition = chromatogramService.createDefinition();
		}
		
		return definition;
	}
	
	public Experiment getExperiment() {
		if (experiment == null) {
			experiment = new Experiment();
		}
		return experiment;
	}
	
}
