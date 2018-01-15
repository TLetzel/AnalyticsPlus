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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CssLayout;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;

@SpringComponent
@UIScope
public class ParameterComponent extends CssLayout {

	private static final long serialVersionUID = 8301248735450440771L;

	@Autowired
	private CompoundSelectionComponent compoundSelectionComponent;

	@Autowired
	private MobilePhaseComponent mobilePhaseComponent;

	@Autowired
	private ChromatographicPropertiesComponent chromatographicPropertiesComponent;

	@Autowired
	private ColumnPropertiesComponent columnPropertiesComponent;

	@PostConstruct
	private void postConstruct() {
		addStyleName(CustomValoTheme.CSS_LAYOUT_SCROLLBAR);
		addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		addStyleName(CustomValoTheme.BLOCK);
		setSizeFull();

		initComponentLayout();
	}

	private void initComponentLayout() {
		mobilePhaseComponent.addStyleName(CustomValoTheme.MARGIN_BOTTOM);
		mobilePhaseComponent.setWidth("100%");
		addComponent(mobilePhaseComponent);

		chromatographicPropertiesComponent.addStyleName(CustomValoTheme.MARGIN_HALF_TOP);
		chromatographicPropertiesComponent.addStyleName(CustomValoTheme.MARGIN_BOTTOM);
		chromatographicPropertiesComponent.setWidth("100%");
		addComponent(chromatographicPropertiesComponent);

		columnPropertiesComponent.addStyleName(CustomValoTheme.MARGIN_HALF_TOP);
		columnPropertiesComponent.addStyleName(CustomValoTheme.MARGIN_BOTTOM);
		columnPropertiesComponent.setWidth("100%");
		addComponent(columnPropertiesComponent);

		compoundSelectionComponent.addStyleName(CustomValoTheme.MARGIN_HALF_TOP);
		compoundSelectionComponent.setWidth("100%");
		compoundSelectionComponent.addStyleName(CustomValoTheme.MARGIN_BOTTOM);
		addComponent(compoundSelectionComponent);
	}
}
