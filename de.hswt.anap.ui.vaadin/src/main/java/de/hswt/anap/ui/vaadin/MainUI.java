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

package de.hswt.anap.ui.vaadin;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.views.Lvl2NavigationView;
import de.hswt.anap.ui.vaadin.views.Lvl3NavigationView;

@SpringUI
@Title("Analytics +")
@Theme("anap-valo")
@Push(transport = Transport.LONG_POLLING)
@Widgetset("de.hswt.anap.ui.vaadin.widgetset.widgetset")
public class MainUI extends UI {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SpringViewProvider viewProvider;

	private CssLayout viewLayout;

	@Override
	protected void init(VaadinRequest request) {
		VaadinSession.getCurrent().setLocale(Locale.US);
		System.out.println(request.getLocale());
		CssLayout rootLayout = new CssLayout();
		rootLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_COLUMN);
		rootLayout.setSizeFull();

		rootLayout.addComponent(createHeaderBar());

		viewLayout = new CssLayout();
		viewLayout.setSizeFull();
		viewLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_COLUMN);
		viewLayout.addStyleName(CustomValoTheme.SKIP_HEADER_VIEW);
		rootLayout.addComponent(viewLayout);

		initNavigator();

		setContent(rootLayout);

		if (getNavigator().getState().isEmpty()) {
			getNavigator().navigateTo(Lvl2NavigationView.VIEW_NAME);
		}
	}

	private CssLayout createHeaderBar() {
		CssLayout navigatorLayout = new CssLayout();
		navigatorLayout.setHeight("2.8rem");
		navigatorLayout.setWidth("100%");
		navigatorLayout.addStyleName(CustomValoTheme.PADDING_HALF);
		navigatorLayout.addStyleName(CustomValoTheme.BACKGROUND_COLOR_GRADIENT_ALT2);

		Label titleLabel = new Label("Analytics +");
		titleLabel.addStyleName(CustomValoTheme.LABEL_VERY_LARGE);
		titleLabel.addStyleName(CustomValoTheme.LABEL_VERY_BOLD);
		titleLabel.addStyleName(CustomValoTheme.COLOR_ALT1);
		titleLabel.addStyleName(CustomValoTheme.MARGIN_HALF);
		titleLabel.setSizeUndefined();
		navigatorLayout.addComponent(titleLabel);

		CssLayout buttonLayout = new CssLayout();
		buttonLayout.addStyleName(CustomValoTheme.FLOAT_RIGHT);
		navigatorLayout.addComponent(buttonLayout);

		Button lvl2Button = new Button("Level 2");
		lvl2Button.addStyleName("round-corner");
		lvl2Button.addStyleName(CustomValoTheme.MARGIN_HALF);
		lvl2Button.addStyleName("higher-z-index");
		lvl2Button.addClickListener(e -> navigateTo(Lvl2NavigationView.VIEW_NAME));
		buttonLayout.addComponent(lvl2Button);

		Button lvl3Button = new Button("Level 3");
		lvl3Button.addStyleName("round-corner");
		lvl3Button.addStyleName(CustomValoTheme.MARGIN_HALF);
		lvl3Button.addStyleName("higher-z-index");
		lvl3Button.addClickListener(e -> navigateTo(Lvl3NavigationView.VIEW_NAME));
		buttonLayout.addComponent(lvl3Button);

		return navigatorLayout;
	}

	private void initNavigator() {
		final Navigator navigator = new Navigator(this, new ViewDisplay() {

			private static final long serialVersionUID = 1L;

			@Override
			public void showView(View view) {
				viewLayout.removeAllComponents();
				viewLayout.addComponent((com.vaadin.ui.Component) view);
			}
		});
		navigator.setErrorView(new ErrorView());
		navigator.addProvider(viewProvider);
		setNavigator(navigator);
	}

	private void navigateTo(String viewName) {
		getNavigator().navigateTo(viewName);
	}

	private class ErrorView extends VerticalLayout implements View {

		private static final long serialVersionUID = 1L;
		private Label message;

		ErrorView() {
			setMargin(true);
			addComponent(message = new Label());
		}

		@Override
		public void enter(ViewChangeListener.ViewChangeEvent event) {
			message.setValue(String.format("No such view: %s", event.getViewName()));
		}
	}

	public Navigator getNavigatorThis() {
		return getNavigator();
	}
}
