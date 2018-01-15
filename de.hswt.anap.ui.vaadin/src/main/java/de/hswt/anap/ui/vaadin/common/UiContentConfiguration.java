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

import de.hswt.hplcsl.GradientDefinition;

public class UiContentConfiguration {

	private final List<String> availableCompounds = new ArrayList<>();
	
	private final List<Double> listedConcentrations = new ArrayList<>();
	
	private List<GradientDefinition> gradientDefinitions;
	
	public UiContentConfiguration() {

		availableCompounds.add("acetophenone");
		availableCompounds.add("benzophenone");

		listedConcentrations.add(30.0);
		listedConcentrations.add(40.0);
		
		gradientDefinitions = new ArrayList<>();
		gradientDefinitions.add(new GradientDefinition(0, 5));
		gradientDefinitions.add(new GradientDefinition(5, 55));
		gradientDefinitions.add(new GradientDefinition(10, 95));
	}

	public List<String> getAvailableCompounds() {
		return new ArrayList<>(availableCompounds);
	}
	
	public List<Double> getConcentration(){
		return new ArrayList<>(listedConcentrations);
	}
	
	public List<GradientDefinition> getGradientDefinitions(){
		return new ArrayList<>(gradientDefinitions);
	}
}
