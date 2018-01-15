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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.ValidationResult;
import de.hswt.anap.service.rulesolver.api.RuleSolver;
import de.hswt.anap.service.rulesolver.impl.rules.TheoreticalPlatesRule;
import de.hswt.anap.service.standards.api.IStandardsService;
import de.hswt.anap.service.rulesolver.impl.rules.BackPressureRule;
import de.hswt.anap.service.rulesolver.impl.rules.ColumnChangeRule;
import de.hswt.anap.service.rulesolver.impl.rules.EluationRule;
import de.hswt.anap.service.rulesolver.impl.rules.InjectionVolumeRule;
import de.hswt.anap.service.rulesolver.impl.rules.SubstanceSolventRule;

@Component
public class RuleSolverImpl implements RuleSolver {

	@Autowired
	private IStandardsService standardsService;

	private Validator validator;

	private Map<RuleType, Rule> rules;

	@PostConstruct
	private void postConstruct() {
		validator = new Validator();

		rules = new HashMap<>();
		Rule rule = new ColumnChangeRule();
		rules.put(rule.getRuleType(), rule);
		rule = new SubstanceSolventRule();
		rules.put(rule.getRuleType(), rule);
		rule = new EluationRule();
		rules.put(rule.getRuleType(), rule);
		rule = new InjectionVolumeRule();
		rules.put(rule.getRuleType(), rule);
		rule = new BackPressureRule();
		rules.put(rule.getRuleType(), rule);
		rule = new TheoreticalPlatesRule();
		rules.put(rule.getRuleType(), rule);
	}

	@Override
	public Map<RuleType, List<ValidationResult>> validate(
			Experiment experiment, RuleType ruleType) {
		Map<RuleType, List<ValidationResult>> result = new HashMap<>();

		if (experiment == null) {
			return result;
		}

		RuleType currentRuleType = getCurrentRuleType(experiment, ruleType);
		RuleType lastRuleType = RuleType.values()[0];
		for (RuleType type : RuleType.values()) {
			if (type.ordinal() > currentRuleType.ordinal()) {
				break;
			}
			lastRuleType = type;
			List<ValidationResult> validationResults = validator.validate(
					experiment, rules.get(type));
			result.put(type, validationResults);
			if (validationResults.stream().anyMatch(r -> !r.isValid())) {
				break;
			}
		}
		experiment.setLastUsedRule(lastRuleType);
		if (experiment.getLastGeneralUsedRule().ordinal() < lastRuleType
				.ordinal()) {
			experiment.setLastGeneralUsedRule(lastRuleType);
		}
		return result;
	}

	private RuleType getCurrentRuleType(Experiment experiment,
			RuleType requestedRuleType) {
		if (experiment.getLastUsedRule() == null) {
			return requestedRuleType;
		} else if (experiment.getLastUsedRule().ordinal() > requestedRuleType
				.ordinal()) {
			return experiment.getLastUsedRule();
		}
		return requestedRuleType;
	}
}
