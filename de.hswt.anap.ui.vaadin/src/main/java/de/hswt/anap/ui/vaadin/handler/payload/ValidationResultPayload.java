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

package de.hswt.anap.ui.vaadin.handler.payload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.ValidationResult;

public class ValidationResultPayload {

	private Map<RuleType, List<ValidationResult>> validationResults;

	private RuleType currentRuleType;
	
	public ValidationResultPayload(
			Map<RuleType, List<ValidationResult>> validationResults, 
			RuleType currentRuleType) {
		this.validationResults = validationResults;
		this.currentRuleType = currentRuleType;
	}
	
	public Map<RuleType, List<ValidationResult>> getValidationResults() {
		return new HashMap<>(validationResults);
	}
	
	public RuleType getCurrentRuleType() {
		return currentRuleType;
	}
}
