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
import com.vaadin.ui.CssLayout;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.UiConstants;
import de.hswt.anap.ui.vaadin.handler.ChromatogramUpdateHandler;
import de.hswt.anap.ui.vaadin.views.components.tutorial.ChromatogramComponent;
import de.hswt.anap.ui.vaadin.views.components.tutorial.CompoundsComponent;
import de.hswt.anap.ui.vaadin.views.components.tutorial.ParameterComponent;
import de.hswt.anap.ui.vaadin.views.components.tutorial.ResultsComponent;

@SpringView(name = TutorialView.VIEW_NAME)
@UIScope
public class TutorialView extends CssLayout implements View {

	private static final long serialVersionUID = 1638341779105260460L;

	public static final String VIEW_NAME = "tutorial";

	@Autowired
	private ParameterComponent parameterComponent;

	@Autowired
	private ChromatogramComponent chromatogramComponent;

	@Autowired
	private CompoundsComponent compoundsComponent;

	@Autowired
	private ResultsComponent resultsComponent;

	// not used here, but needed to get handler into view context
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

		initLeftLayout();
		initRightLayout();
	}

	private void initLeftLayout() {
		parameterComponent.setWidth(UiConstants.WIDTH_PARAMETER);
		wrapperLayout.addComponent(parameterComponent);
	}

	private void initRightLayout() {
		CssLayout rightLayout = new CssLayout();
		rightLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_COLUMN);
		rightLayout.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);
		rightLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_SCROLLBAR);
		rightLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		wrapperLayout.addComponent(rightLayout);

		chromatogramComponent.setHeight(UiConstants.HEIGHT_CHROMATOGRAM);
		chromatogramComponent.addStyleName(CustomValoTheme.MARGIN_HALF_BOTTOM);
		rightLayout.addComponent(chromatogramComponent);

		CssLayout lowerLayout = new CssLayout();
		lowerLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_ROW);
		lowerLayout.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);
		lowerLayout.addStyleName(CustomValoTheme.PADDING_HALF_VERTICAL);
		rightLayout.addComponent(lowerLayout);

		compoundsComponent.setHeight(UiConstants.HEIGHT_RESULTS);
		compoundsComponent.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);
		compoundsComponent.addStyleName(CustomValoTheme.MARGIN_HALF_RIGHT);
		lowerLayout.addComponent(compoundsComponent);

		resultsComponent.setWidth(UiConstants.WIDTH_RESULTS);
		resultsComponent.setHeight(UiConstants.HEIGHT_RESULTS);
		resultsComponent.addStyleName(CustomValoTheme.MARGIN_HALF_LEFT);
		lowerLayout.addComponent(resultsComponent);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
