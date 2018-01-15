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

package de.hswt.anap.service.rulesolver.impl.rules;

import java.util.ArrayList;
import java.util.List;

import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.ValidationResult;
import de.hswt.anap.model.ValidationResult.Priority;
import de.hswt.anap.service.rulesolver.impl.Rule;

public class InjectionVolumeRule implements Rule {

	private String MESSAGE_ERROR = "Injection volume does not match flowrate. Injection volume should not exceed 1/10 of the flow rate volume";

	private String MESSAGE_HELP = "If you're having trouble finding matching items, you can look for help at our moodle webpage!";

	private String MESSAGE_VALID = "Injection volume matches flowrate.";

	@Override
	public List<ValidationResult> validate(Experiment experiment) {

		List<ValidationResult> result = new ArrayList<>();

		if (experiment.getFlowRate() != null
				&& experiment.getInjectionVolume() != null) {
			double flowrate = experiment.getFlowRate();
			double injectionVolume = experiment.getInjectionVolume();

			if (injectionVolume > (0.1 * flowrate)) {
				result.add(new ValidationResult(false, MESSAGE_ERROR,
						MESSAGE_HELP, Priority.ERROR));
			} else {
				result.add(new ValidationResult(true, MESSAGE_VALID,
						Priority.RESULT));
			}
		}
		return result;
	}

	@Override
	public RuleType getRuleType() {
		return RuleType.INJECTION_VOLUME_FLOWRATE;
	}
}
