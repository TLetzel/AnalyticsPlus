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

package de.hswt.anap.ui.vaadin.components;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;

public abstract class ContainerComponent extends CssLayout {

	private static final long serialVersionUID = 1L;

	private final String headerColorStyle = CustomValoTheme.BACKGROUND_COLOR_ALT3;

	protected abstract Component getContent();

	protected abstract String getTitle();

	@Autowired
	protected ViewEventBus eventBus;

	private CssLayout headerLayout;

	public ContainerComponent() {
		addStyleName(CustomValoTheme.CSS_SHADOW_BORDER);
//		addStyleName(CustomValoTheme.RELATIVE);
//		addStyleName(CustomValoTheme.BLOCK);
		addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_STYLE);
	}

	@PostConstruct
	private void postConstruct() {
		initHeader();
		initContentContainer();

		eventBus.subscribe(this);
	}

	@PreDestroy
	private void preDestroy() {
		eventBus.unsubscribe(this);
	}

	private void initHeader() {
		CssLayout rowWrapperLayout = new CssLayout();
		rowWrapperLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_ROW);
		rowWrapperLayout.setWidth("100%");
		addComponent(rowWrapperLayout);

		headerLayout = new CssLayout();
		headerLayout.addStyleName(CustomValoTheme.PADDING_HORIZONTAL);
		headerLayout.addStyleName(CustomValoTheme.HIDDEN_OVERFLOW);
		headerLayout.addStyleName(headerColorStyle);
		headerLayout.setWidth("100%");
		headerLayout.setHeight("2rem");
		rowWrapperLayout.addComponent(headerLayout);

		Label titleLabel = new Label(getTitle());
		titleLabel.addStyleName(CustomValoTheme.MARGIN_HALF_VERTICAL);
		titleLabel.addStyleName(CustomValoTheme.COLOR_ALT1);
		titleLabel.addStyleName(CustomValoTheme.LABEL_LARGE);

		headerLayout.addComponent(titleLabel);
	}

	private void initContentContainer() {
		CssLayout rowWrapperLayout = new CssLayout();
		rowWrapperLayout.setSizeFull();
		rowWrapperLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_ROW);
		addComponent(rowWrapperLayout);

		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();
		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF);
		rowWrapperLayout.addComponent(contentLayout);

		contentLayout.addComponent(getContent());
	}

	protected void setHeaderColorStyle(String style, boolean add) {
		headerLayout.setStyleName(style, add);
		headerLayout.setStyleName(headerColorStyle, !add);
	}
}
