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

import java.util.List;
import java.util.Map;

import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.ValidationResult;

public class ExamFlowController {

	public static boolean isEnabled(
			Map<RuleType, List<ValidationResult>> validationResults, 
			RuleType targetRuleType,
			RuleType initialRuleType) {
		
		List<ValidationResult> results = validationResults.get(initialRuleType);
		
		// if target == initial
		// if target < initial
		// if target == (initial -1) and non failed results
		
		int target = targetRuleType.ordinal();
		int initial = initialRuleType.ordinal();
		
		if (validationResults.isEmpty()) {
			return false;
		}
		
		if (target <= initial ||
			(target - 1) == initial	&& !results.stream().anyMatch(r -> !r.isValid())) {
			return true;
		}
		
		return false;
	}
	
}
