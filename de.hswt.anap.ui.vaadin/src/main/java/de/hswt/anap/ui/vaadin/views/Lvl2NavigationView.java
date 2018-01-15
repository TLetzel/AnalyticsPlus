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

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;

@SpringView(name = Lvl2NavigationView.VIEW_NAME)
@UIScope
public class Lvl2NavigationView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "lvl2 navigation";

	private CssLayout wrapperLayout;

	private Navigator navigator;

	@PostConstruct
	private void postConstruct() {
		navigator = UI.getCurrent().getNavigator();

		addStyleName(CustomValoTheme.SKIP_HEADER_VIEW);
		setSizeFull();

		wrapperLayout = new CssLayout();
		wrapperLayout.addStyleName(CustomValoTheme.PADDING_VERTICAL);
		wrapperLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		wrapperLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_STYLE);
		wrapperLayout.setSizeFull();

		initLeftLayout();
		initRightLayout();

		addComponent(wrapperLayout);

	}

	private void initLeftLayout() {
		CssLayout leftLayout = new CssLayout();
		leftLayout.setHeight("100%");
		leftLayout.setWidth("50%");
		leftLayout.setStyleName(CustomValoTheme.PADDING_HALF);

		Button hplcButton = createNavButton("HPLC Simulator");
		hplcButton.addClickListener(e -> navigateTo(TutorialView.VIEW_NAME));
		leftLayout.addComponent(hplcButton);

		wrapperLayout.addComponent(leftLayout);
	}

	private void initRightLayout() {
		CssLayout rightLayout = new CssLayout();
		rightLayout.setHeight("100%");
		rightLayout.setWidth("50%");
		rightLayout.setStyleName(CustomValoTheme.PADDING_HALF);

		Button speButton = createNavButton("Sample preparation");
		speButton.addClickListener(e -> navigateTo(QuizView.VIEW_NAME));
		rightLayout.addComponent(speButton);

		wrapperLayout.addComponent(rightLayout);
	}

	private Button createNavButton(String name) {
		Button navButton = new Button(name);
		navButton.addStyleName(CustomValoTheme.BUTTON_LARGE);
		navButton.setSizeFull();
		return navButton;
	}

	private void navigateTo(String name) {
		navigator.navigateTo(name);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}
}
