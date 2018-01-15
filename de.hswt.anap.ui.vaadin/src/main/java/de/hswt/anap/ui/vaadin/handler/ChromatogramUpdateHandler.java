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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import de.hswt.anap.service.chromatogram.api.IChromatogramService;
import de.hswt.anap.ui.vaadin.handler.payload.ResetPayload;
import de.hswt.hplcsl.Chromatogram;
import de.hswt.hplcsl.Definition;

@SpringComponent
@ViewScope
public class ChromatogramUpdateHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ChromatogramUpdateHandler.class);
	
	@Autowired
	private ViewEventBus eventBus;

	@Autowired
	private IChromatogramService chromatogramService;
	
	@PostConstruct
	private void postConstruct() {
		eventBus.subscribe(this);
	}
	
	@PreDestroy
	private void preDestroy() {
		eventBus.unsubscribe(this);
	}

	@EventBusListenerMethod
	private void handleDefinitionUpdate(Definition definition) {
		LOG.debug("receive event inside handleDefinitionUpdate with payload {}", definition);

		if ( !chromatogramService.isDefinitionValid(definition)) {
			return;
		}
		
		Chromatogram result = chromatogramService.getChromatogram(
				definition);
				
		if (result != null) {
			LOG.debug("publish event inside handleDefinitionUpdate with payload {}", 
					result);
			eventBus.publish(this, result);
		} else {
			eventBus.publish(this, new ResetPayload());
		}
	}
}
