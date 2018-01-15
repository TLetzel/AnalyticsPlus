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

package de.hswt.anap.service.chromatogram.impl;

import org.springframework.stereotype.Component;

import de.hswt.anap.service.chromatogram.api.IChromatogramService;
import de.hswt.hplcsl.Chromatogram;
import de.hswt.hplcsl.Definition;
import de.hswt.hplcsl.HplcSimulator;

@Component
public class ChromatogramSerivceImpl implements IChromatogramService {

	private HplcSimulator hplcSimulator = new HplcSimulator();
	
	@Override
	public Definition createDefinition() {
		return hplcSimulator.createDefinition();
	}

	@Override
	public Chromatogram getChromatogram(Definition definition) {
		return hplcSimulator.calculateChromatogram(definition);
	}

	@Override
	public boolean isDefinitionValid(Definition definition) {
		// TODO check if all needed parameters are set
		return true;
	}
}
