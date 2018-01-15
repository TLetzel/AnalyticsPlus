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

package de.hswt.anap.ui.vaadin.factories;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;

@org.springframework.stereotype.Component
public class ComponentFactory {

	public Button createButton(String caption, String description) {
		return createButton(caption, null, description, true);
	}

	public Button createButton(String caption) {
		return createButton(caption, null, null, true);
	}

	public Button createButton(String caption, Resource icon) {
		return createButton(caption, icon, null, true);
	}

	public Button createButton(String caption, Resource icon,
			String description) {
		return createButton(caption, icon, description, true);
	}

	public Button createButton(Resource icon) {
		return createButton(null, icon, null, true);
	}

	public Button createButton(Resource icon, String description) {
		return createButton(null, icon, description, true);
	}

	public Button createButton(Resource icon, boolean withMargin) {
		return createButton(null, icon, null, withMargin);
	}

	public Button createButton(Resource icon, String description,
			boolean withMargin) {
		return createButton(null, icon, description, withMargin);
	}

	public Button createButton(String caption, boolean withRightMargin) {
		return createButton(caption, null, null, withRightMargin);
	}

	public Button createButton(String caption, String description,
			boolean withRightMargin) {
		return createButton(caption, null, withRightMargin);
	}

	public Button createButton(String caption, Resource icon,
			String description, boolean withRightMargin) {
		Button button = new Button();
		button.addStyleName(CustomValoTheme.BACKGROUND_COLOR_ALT3);
		button.addStyleName(CustomValoTheme.BORDER_NONE);

		if (caption != null) {
			button.setCaption(caption);
		}
		if (icon != null) {
			button.setIcon(icon);
		}
		if (description != null) {
			button.setDescription(description);
		}
		if (withRightMargin) {
			button.addStyleName(CustomValoTheme.MARGIN_HALF_RIGHT);
		}

		return button;
	}
	
	public OptionGroup createOptionGroup(String caption) {
		OptionGroup optionGroup = new OptionGroup(caption);
		optionGroup.setImmediate(true);
		return optionGroup;
	}

	public ComboBox createComboBox(String caption) {
		ComboBox comboBox = new ComboBox(caption);
		comboBox.setTextInputAllowed(false);
		comboBox.setNullSelectionAllowed(false);
		comboBox.setImmediate(true);
		comboBox.setWidth("100%");
		return comboBox;
	}
	
	public CssLayout createRowLayout() {
		CssLayout rowLayout = new CssLayout();
		rowLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_ROW);
		return rowLayout;
	}

	public CssLayout createRowLayout(Component... components) {
		CssLayout rowLayout = createRowLayout();
		rowLayout.addStyleName(CustomValoTheme.MARGIN_HALF_VERTICAL);
		rowLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_ROW);
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (component.getCaption() != null) {
				CssLayout tempLayout = new CssLayout();
				tempLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_COLUMN);
				tempLayout.addComponent(component);
				component = tempLayout;
			}
			component.addStyleName(CustomValoTheme.MARGIN_HALF_HORIZONTAL);
			if (i == 0) {
				component.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);
			} else {
				component.addStyleName(CustomValoTheme.FLEX_ITEM);
			}
			rowLayout.addComponent(component);
		}
		return rowLayout;
	}
	
	public TextField createTextField(String caption) {
		TextField textField = new TextField();
		textField.setCaption(caption);
		textField.setWidth("100%");
		textField.setNullRepresentation("");
		
		return textField;
	}
	
	public TextField createTextField(String caption, int maxLength) {
		TextField textField = new TextField();
		textField.setCaption(caption + " (max." + maxLength + ")");
		textField.setWidth("100%");
		textField.setNullRepresentation("");
		textField.setMaxLength(maxLength);
		return textField;
	}
	
	public TextArea createTextArea(String caption, int maxLength, boolean wordWrap) {
		TextArea textArea = new TextArea();
		textArea.setCaption(caption + " (max." + maxLength + ")");
		textArea.setWidth("100%");
		textArea.setNullRepresentation("");
		textArea.setMaxLength(maxLength);
		textArea.setWordwrap(wordWrap);
		return textArea;
	}

	public Label createSpacer() {
		Label spacerLabel = new Label();
		spacerLabel.setHeight("1rem");
		return spacerLabel;
	}
	
	public Label createSpacer(String height) {
		Label spacerLabel = new Label();
		spacerLabel.setHeight(height);
		return spacerLabel;
	}

	public Label createDividingLine() {
		Label line = new Label();
		line.setHeight("1px");
		line.setPrimaryStyleName(CustomValoTheme.HORIZONTAL_LINE);
		line.addStyleName(CustomValoTheme.MARGIN_TOP);
		line.addStyleName(CustomValoTheme.MARGIN_BOTTOM);
		return line;
	}
}