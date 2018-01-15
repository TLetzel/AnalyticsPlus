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

import java.util.List;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;


/**
 * @author Marco Luthardt
 */
public class CustomListComponent<T> extends CustomComponent {

	private static final long serialVersionUID = -4292365364151780541L;

	private CssLayout containerLayout;

	private CustomListComponentContentGenerator<T> generator;

	public CustomListComponent(CustomListComponentContentGenerator<T> generator) {
		if (generator == null) {
			throw new NullPointerException("Generator must not be null.");
		}

		this.generator = generator;

		addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_COLUMN);
		addStyleName(CustomValoTheme.CSS_LAYOUT_NO_SCROLLBAR);
		setSizeFull();

		containerLayout = new CssLayout();
		containerLayout.setSizeFull();
		containerLayout.addStyleName(CustomValoTheme.BLOCK);
		containerLayout.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);
		containerLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_SCROLLBAR);
		containerLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		setCompositionRoot(containerLayout);
	}
	
	public void removeAll() {
		containerLayout.removeAllComponents();
	}
	
	public void addAll(List<T> items) {
		// remove listeners
		containerLayout.removeAllComponents();

		// add new items
		for (T item : items) {
			containerLayout.addComponent(generator.getComponentContent(item));
		}
	}

	public void add(T item) {
		containerLayout.addComponent(generator.getComponentContent(item));
	}
}
