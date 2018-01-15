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

package de.hswt.anap.service.rulesolver.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.ValidationResult;

public class Validator {

	public Map<RuleType, List<ValidationResult>> validate(
			Experiment experiment, List<Rule> rules) {
		Map<RuleType, List<ValidationResult>> results = new HashMap<>();

		for (Rule rule : rules) {
			List<ValidationResult> validationResults = validate(experiment,
					rule);
			if (!validationResults.isEmpty()) {
				addResult(results, rule.getRuleType(), validationResults);
			}
			if (validationResults.stream().anyMatch(r -> !r.isValid())) {
				// found invalid result => end of validation
				break;
			}
		}
		return results;
	}

	public List<ValidationResult> validate(Experiment experiment, Rule rule) {
		return rule.validate(experiment);
	}

	private void addResult(Map<RuleType, List<ValidationResult>> results,
			RuleType ruleType, List<ValidationResult> result) {
		if (!results.containsKey(ruleType)) {
			results.put(ruleType, new ArrayList<>());
		}
		results.get(ruleType).addAll(result);
	}
}
