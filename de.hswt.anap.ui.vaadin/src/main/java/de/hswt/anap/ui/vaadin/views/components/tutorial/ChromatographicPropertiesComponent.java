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

package de.hswt.anap.ui.vaadin.views.components.tutorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Slider;

import de.hswt.anap.ui.vaadin.common.UiConstants;
import de.hswt.anap.ui.vaadin.components.ContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.factories.ComponentFactory;
import de.hswt.hplcsl.Definition;

@SpringComponent
@ViewScope
public class ChromatographicPropertiesComponent extends ContainerComponent {

	private static final long serialVersionUID = -1486371016168618926L;
	
	private static final Logger LOG = LoggerFactory.getLogger(ChromatographicPropertiesComponent.class);


	@Autowired
	private ComponentFactory componentFactory;
	
	@Autowired
	private SharedModelProvider definitionProvider;

	@PropertyId("temperature")
	private Slider temperatureSlider;

	@PropertyId("injectionVolume")
	private ComboBox injectionVolumeComboBox;

	@PropertyId("flowRate")
	private ComboBox flowRateComboBox;
	
	private BeanFieldGroup<Definition> fieldGroup;
	
	private boolean initialized = false;
	
	@Override
	protected Component getContent() {
		
		fieldGroup = new BeanFieldGroup<>(Definition.class);
		fieldGroup.setReadOnly(false);
		
		temperatureSlider = new Slider("Temperature in " + UiConstants.UTF8_DEGREES , 10, 150);
		injectionVolumeComboBox = componentFactory.createComboBox("Injection volume");
		Label injectionVolumnUnitLabel = new Label(UiConstants.UTF8_MICRO + "L");
		flowRateComboBox = componentFactory.createComboBox("Flow rate");
		Label flowRateUnitLabel = new Label(UiConstants.UTF8_MICRO + "L/min");

		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(definitionProvider.getDefinition());
		
		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();
	
		temperatureSlider.setOrientation(SliderOrientation.HORIZONTAL);
		temperatureSlider.setValue(definitionProvider.getDefinition().getTemperature());
		temperatureSlider.addValueChangeListener(
				e -> handleTemperatureSliderValueChanged());
		contentLayout.addComponent(componentFactory.createRowLayout(
				temperatureSlider));
		
		for (int i = 1; i < 11; i++){
			double j = i * 10;
			injectionVolumeComboBox.addItem(j);
			injectionVolumeComboBox.setItemCaption(j, String.valueOf(j));
		}
		
		injectionVolumeComboBox.addValueChangeListener(
				e -> handleInjectionVolumeComboBoxValueChanged());	
		injectionVolumnUnitLabel.setCaption(""); // layout issue
		injectionVolumnUnitLabel.setWidth("2.5rem");
		contentLayout.addComponent(componentFactory.createRowLayout(
				injectionVolumeComboBox, injectionVolumnUnitLabel));
		
		for(int i = 1; i < 201; i++){
			double j = i * 10;
			flowRateComboBox.addItem(j);
			flowRateComboBox.setItemCaption(j, String.valueOf(j));
		}
		flowRateComboBox.setConverter(Double.class);
		flowRateComboBox.addValueChangeListener(e -> handleFlowRateComboBoxValueChanged());
		flowRateComboBox.setWidth("100%");
		flowRateUnitLabel.setCaption(""); // layout issue
		flowRateUnitLabel.setWidth("2.5rem");
		contentLayout.addComponent(componentFactory.createRowLayout(
				flowRateComboBox, flowRateUnitLabel));
		
		initialized = true;
		
		return contentLayout;
	}

	private void handleFlowRateComboBoxValueChanged() {
		if (!initialized) {
			return;
		}
		flowRateComboBox.commit();
		validateInput();
		handleValueChanged();
	}
	
	private void handleInjectionVolumeComboBoxValueChanged() {
		if(!initialized){
			return;
		}
		injectionVolumeComboBox.commit();
		validateInput();
		handleValueChanged();
	}

	private void handleTemperatureSliderValueChanged() {
		if(!initialized){
			return;
		}
		temperatureSlider.commit();
		validateInput();
		handleValueChanged();
	}

	@Override
	protected String getTitle() {
		return "Chromatographic properties";
	}
	
	private void handleValueChanged() {
		LOG.debug("publish event inside handleValueChanged with payload {}", 
				definitionProvider.getDefinition());
		eventBus.publish(this, definitionProvider.getDefinition());
		
	}
	
	private void validateInput(){
		if(Double.parseDouble(injectionVolumeComboBox.getValue().toString()) > (0.1 * Double.parseDouble(flowRateComboBox.getValue().toString()))){
			Notification.show("Injection volume does not match flowrate", Type.WARNING_MESSAGE);
		}else if(temperatureSlider.getValue() > 70){
			Notification.show("Temperature might be too high", Type.WARNING_MESSAGE);
		}
	}


}
