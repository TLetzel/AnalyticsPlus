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

package de.hswt.anap.ui.vaadin.handler;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.ValidationResult;
import de.hswt.anap.service.rulesolver.api.RuleSolver;
import de.hswt.anap.ui.vaadin.common.CompoundGenerator;
import de.hswt.anap.ui.vaadin.handler.payload.ResetPayload;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationRequestPayload;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationResultPayload;

@SpringComponent
@ViewScope
public class ValidationHandler {

	@Autowired
	private ViewEventBus eventBus;

	@Autowired
	private RuleSolver ruleSolver;

	@Autowired
	private CompoundGenerator compoundGenerator;

	private boolean hasErrors;
	
	private static final Logger LOG = LoggerFactory
			.getLogger(ValidationHandler.class);

	@PostConstruct
	private void postConstruct() {
		eventBus.subscribe(this);
	}

	@PreDestroy
	private void preDestroy() {
		eventBus.unsubscribe(this);
	}

	@EventBusListenerMethod
	private void handleExperimentUpdate(ValidationRequestPayload payload) {
		LOG.debug("Receive event inside ValidationHandler with payload{} ", payload);
		Experiment experiment = payload.getExperiment();
		if (experiment == null) {
			return;
		}
		if (experiment.isComplete()) {
			experiment.getDefinition().setCompounds(
					compoundGenerator.getCompounds(experiment));
		}

		if(experiment.getLastGeneralUsedRule() == null){
			experiment.setLastGeneralUsedRule(payload.getInitialRuleType());
		}
		Map<RuleType, List<ValidationResult>> results = ruleSolver.validate(
				experiment, experiment.getLastGeneralUsedRule());

		hasErrors = false;
		results.values().stream()
				.forEach(e -> e.stream().forEach(f -> checkForErrors(f)));

		Map<RuleType, List<ValidationResult>> validationResults = ruleSolver
				.validate(experiment, payload.getInitialRuleType());

		if (!experiment.isComplete() && !hasErrors) {
			LOG.debug("Publish event inside ValidationHandler with payload{} ", validationResults);
			eventBus.publish(this, new ValidationResultPayload(
					validationResults, experiment.getLastGeneralUsedRule()));
		} else if (hasErrors) {
			LOG.debug("Publish reset event inside ValidationHandler");
			LOG.debug("Publish event inside ValidationHandler with payload{} ", validationResults);
			eventBus.publish(this, new ResetPayload());
			eventBus.publish(this, new ValidationResultPayload(
					validationResults, experiment.getLastUsedRule()));
		} else {
			LOG.debug("Publsh event inside ValidationHandler with payload{} ", experiment.getDefinition());
			LOG.debug("Publish event inside ValidationHandler with payload{} ", validationResults);
			eventBus.publish(this, new ValidationResultPayload(
					validationResults, experiment.getLastGeneralUsedRule()));
			eventBus.publish(this, experiment.getDefinition());
		}
	}

	private void checkForErrors(ValidationResult f) {
		if (f.isValid()) {
			return;
		}
		hasErrors = true;
	}

}
