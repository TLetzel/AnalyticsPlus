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

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;

/**
 * @author Marco Luthardt
 */
public abstract class CustomListComponentContentGenerator<T> {

	protected abstract Component getContent(T item);
	
	protected CssLayout getComponentContent(T item) {
		CssLayout content = new CssLayout();
		content.addStyleName(CustomValoTheme.FLEX_ITEM);
		content.addStyleName(CustomValoTheme.MARGIN_HALF_TOP);
		content.addStyleName(CustomValoTheme.PADDING_HALF_BOTTOM);
		content.addStyleName(CustomValoTheme.BORDER_BOTTOM_COLOR_ALT3);
		content.setWidth("100%");
		content.addComponent(getContent(item));
		
		return content;
	}
}
