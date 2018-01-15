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
import de.hswt.anap.model.SolvingCompoundConfiguration;
import de.hswt.anap.model.SubstanceConfiguration;
import de.hswt.anap.model.ValidationResult;
import de.hswt.anap.model.ValidationResult.Priority;
import de.hswt.anap.service.rulesolver.impl.Rule;

public class SubstanceSolventRule implements Rule {

	private static final String MESSAGE_ERROR = "Sample does not dissolve! Please try a different setup.";

	private static final String MESSAGE_HELP = "If you're having trouble finding matching items, you can look for help at our moodle webpage!";

	private static final String MESSAGE_ADVICE = "Sample does not dissolve completely.";

	private static final String MESSAGE_VALID = "Sample dissolves completely.";

	private SolvingCompoundConfiguration solvent;

	@Override
	public List<ValidationResult> validate(Experiment experiment) {
		List<ValidationResult> result = new ArrayList<>();

		String message = null;

		boolean valid;

		this.solvent = experiment.getSolvent().get();

		Priority priority = Priority.STANDARD;

		if (experiment.getSubstances() == null) {
			result.add(new ValidationResult(false));
			return result;
		}

		for (SubstanceConfiguration substance : experiment.getSubstances()) {
			if (checkIfGoodSolving(substance)) {
				valid = true;
				// Added feedback, if selected items are correct
				message = MESSAGE_VALID;
				priority = Priority.RESULT;
			} else if (checkIfMediumSolving(substance)) {
				message = MESSAGE_ADVICE;
				valid = true;
				priority = Priority.WARNING;
			} else {
				message = MESSAGE_ERROR;
				valid = false;
				priority = Priority.ERROR;
			}

			System.out.println(substance.getName());
			if (message != null) {
				result.add(new ValidationResult(substance, valid, message,
						MESSAGE_HELP, priority));
			} else {
				result.add(new ValidationResult(substance, valid, priority));
			}
		}

		return result;
	}

	private boolean checkIfMediumSolving(SubstanceConfiguration substance) {
		double lowerLimit = solvent.getMediumDisolvingZoneLowerLimit();
		double upperLimit = solvent.getMediumDisolvingZoneUpperLimit();
		double polaritySubstance = substance.getLogD();

		return polaritySubstance <= upperLimit
				&& polaritySubstance >= lowerLimit;
	}

	private boolean checkIfGoodSolving(SubstanceConfiguration substance) {
		double lowerLimit = solvent.getBestDisolvingZoneLowerLimit();
		double upperLimit = solvent.getBestDisolvingZoneUpperLimit();
		double logD = substance.getLogD();

		return logD <= upperLimit && logD >= lowerLimit;
	}

	@Override
	public RuleType getRuleType() {
		return RuleType.SUBSTANCE_ELUENT;
	}
}
