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

package de.hswt.anap.ui.vaadin.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import de.hswt.anap.ui.vaadin.common.CompoundGenerator;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.handler.ChromatogramUpdateHandler;
import de.hswt.anap.ui.vaadin.handler.ValidationHandler;
import de.hswt.anap.ui.vaadin.views.components.exam.ChromatographicComponent;
import de.hswt.anap.ui.vaadin.views.components.exam.ColumSelectionComponent;
import de.hswt.anap.ui.vaadin.views.components.exam.ColumnComponent;
import de.hswt.anap.ui.vaadin.views.components.exam.ElutionComponent;
import de.hswt.anap.ui.vaadin.views.components.exam.MessagesComponent;
import de.hswt.anap.ui.vaadin.views.components.exam.SubstanceSelectionComponent;
import de.hswt.anap.ui.vaadin.views.components.tutorial.ChromatogramComponent;

@SpringView(name = HplcSimLvl3View.VIEW_NAME)
@UIScope
public class HplcSimLvl3View extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "hplcSimLvl3";

	@Autowired
	private ColumSelectionComponent columnSelectionComponent;

	@Autowired
	private SubstanceSelectionComponent substanceSelectionComponent;

	@Autowired
	private ChromatographicComponent chromatographicComponent;

	@Autowired
	private ColumnComponent columnComponent;

	@Autowired
	private MessagesComponent messagesComponent;

	@Autowired
	private ChromatogramComponent chromatogramComponent;

	@Autowired
	private ElutionComponent eluationComponent;

	@Autowired
	private CompoundGenerator compoundGenerator;

	// not used here, but must be in the view context initialized
	@Autowired
	private ValidationHandler validationHandler;

	// not used here, but must be in the view context initialized
	@Autowired
	private ChromatogramUpdateHandler chromatogramUpdateHandler;

	private CssLayout wrapperLayout;

	@PostConstruct
	private void postConstruct() {
		addStyleName(CustomValoTheme.SKIP_HEADER_VIEW);
		setSizeFull();

		wrapperLayout = new CssLayout();
		wrapperLayout.addStyleName(CustomValoTheme.PADDING_VERTICAL);
		wrapperLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		wrapperLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_ROW);
		wrapperLayout.setHeight("100%");
		addComponent(wrapperLayout);

		initDataLayout();

		initResultsLayout();
	}

	private void initDataLayout() {
		CssLayout dataLayout = new CssLayout();
		dataLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_SCROLLBAR);
		dataLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		dataLayout.addStyleName(CustomValoTheme.BLOCK);
		dataLayout.setWidth("30rem");
		wrapperLayout.addComponent(dataLayout);

		columnSelectionComponent.addStyleName(CustomValoTheme.MARGIN_BOTTOM);
		columnSelectionComponent.setWidth("100%");
		dataLayout.addComponent(columnSelectionComponent);

		initStyle(substanceSelectionComponent);
		dataLayout.addComponent(substanceSelectionComponent);

		initStyle(eluationComponent);
		dataLayout.addComponent(eluationComponent);

		initStyle(chromatographicComponent);
		dataLayout.addComponent(chromatographicComponent);

		initStyle(columnComponent);
		dataLayout.addComponent(columnComponent);
	}

	private void initResultsLayout() {
		CssLayout resultsLayout = new CssLayout();
		resultsLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_COLUMN);
		resultsLayout.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);
		resultsLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_SCROLLBAR);
		resultsLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		wrapperLayout.addComponent(resultsLayout);

		messagesComponent.addStyleName(CustomValoTheme.MARGIN_BOTTOM);
		messagesComponent.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);
		messagesComponent.addStyleName(""); // min-height2 = 25rem
		resultsLayout.addComponent(messagesComponent);

		chromatogramComponent.addStyleName(CustomValoTheme.MARGIN_HALF_VERTICAL);
		chromatogramComponent.setHeight("25rem");
		resultsLayout.addComponent(chromatogramComponent);
	}

	private void initStyle(Component component) {
		component.addStyleName(CustomValoTheme.MARGIN_HALF_TOP);
		component.addStyleName(CustomValoTheme.MARGIN_BOTTOM);
		component.addStyleName(CustomValoTheme.FLEX_ITEM);
		component.setWidth("100%");
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
