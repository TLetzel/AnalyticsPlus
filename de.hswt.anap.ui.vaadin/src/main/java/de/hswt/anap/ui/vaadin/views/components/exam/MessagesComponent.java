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

package de.hswt.anap.ui.vaadin.views.components.exam;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.SubstanceConfiguration;
import de.hswt.anap.model.ValidationResult;
import de.hswt.anap.model.ValidationResult.Priority;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.ValidationResultWrapper;
import de.hswt.anap.ui.vaadin.components.CustomListComponent;
import de.hswt.anap.ui.vaadin.components.CustomListComponentContentGenerator;
import de.hswt.anap.ui.vaadin.components.FixedContainerComponent;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationResultPayload;

@SpringComponent
@ViewScope
public class MessagesComponent extends FixedContainerComponent {

	private static final long serialVersionUID = 1;

	CustomListComponent<ValidationResultWrapper> resultListComponent;

	CustomListComponent<ValidationResultWrapper> substanceResultListComponent;

	private static final Logger LOG = LoggerFactory.getLogger(MessagesComponent.class);

	private NumberFormat twoDigetsFormatter = new DecimalFormat("#0.0");

	private NumberFormat threeDigetsFormatter = new DecimalFormat("#0.00");

	@Override
	protected Component getContent() {
		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();
		contentLayout.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);

		resultListComponent = new CustomListComponent<>(new ValidationResultGenerator());
		contentLayout.addComponent(resultListComponent);

		return contentLayout;
	}

	@Override
	protected String getTitle() {
		return "Validation results";
	}

	@EventBusListenerMethod
	private void handleValidationResultUpdate(ValidationResultPayload payload) {
		LOG.debug("Receive event inside MessagesComponent with payload{} ", payload);
		resultListComponent.removeAll();

		Map<RuleType, List<ValidationResult>> result = payload.getValidationResults();

		List<ValidationResultWrapper> results = new ArrayList<>();
		for (RuleType ruleType : RuleType.values()) {
			if (!result.containsKey(ruleType)) {
				continue;
			}
			for (ValidationResult validationResult : result.get(ruleType)) {
				if (validationResult.getMessage() != null) {
					switch (validationResult.getPriority()) {

					case RESULT:
					case WARNING:
					case ERROR:
						results.add(new ValidationResultWrapper(ruleType, validationResult));

					default:
						break;
					}
				}
			}

		}
		resultListComponent.addAll(results);
	}

	private void initSubstanceEluentDetails(ValidationResultWrapper item, CssLayout containerLayout) {
		ValidationResult validationResult = item.getValidationResult();

		containerLayout.addComponent(getErrorLayout(validationResult,
				((SubstanceConfiguration) validationResult.getValidatedObject()).getName()));
	}

	private void initInjectionVolumeFlowRateDetails(ValidationResultWrapper item, CssLayout containerLayout) {
		ValidationResult validationResult = item.getValidationResult();

		if (!validationResult.isValid()) {
			containerLayout.addComponent(getErrorLayout(validationResult, "Invalid flow rate"));
		} else {
			containerLayout.addComponent(getErrorLayout(validationResult, "Valid flow rate"));
		}
	}

	private void initBackPressureDetails(ValidationResultWrapper item, CssLayout containerLayout) {
		String caption = "Back pressure";
		ValidationResult validationResult = item.getValidationResult();
		if (Priority.ERROR.equals(validationResult.getPriority())) {
			containerLayout.addComponent(getErrorLayout(validationResult,
					caption + ": " + threeDigetsFormatter.format(validationResult.getValidatedObject()) + " bar"));
			return;
		}
		CssLayout messageLayout = createCellLayout();

		Label headerLabel = new Label(caption);
		headerLabel.addStyleName(CustomValoTheme.LABEL_VERY_BOLD);
		headerLabel.addStyleName(CustomValoTheme.LABEL_LARGE);
		messageLayout.addComponent(headerLabel);

		Label messageLabel = new Label(
				threeDigetsFormatter.format(Float.parseFloat(validationResult.getValidatedObject().toString()))
						+ " bar");
		messageLayout.addComponent(messageLabel);

		containerLayout.addComponent(messageLayout);
	}

	private void initTheoreticalPlatesDetails(ValidationResultWrapper item, CssLayout containerLayout) {
		String caption = "Theoretical plates";
		ValidationResult validationResult = item.getValidationResult();
		if (Priority.ERROR.equals(validationResult.getPriority())) {
			containerLayout.addComponent(getErrorLayout(validationResult, caption + " plates"));
			return;
		}

		CssLayout messageLayout = createCellLayout();

		Label headerLabel = new Label(caption);

		headerLabel.addStyleName(CustomValoTheme.LABEL_VERY_BOLD);
		headerLabel.addStyleName(CustomValoTheme.LABEL_LARGE);
		messageLayout.addComponent(headerLabel);

		Label messageLabel = new Label(
				threeDigetsFormatter.format(Float.parseFloat(validationResult.getValidatedObject().toString()))
						+ " plates");
		messageLayout.addComponent(messageLabel);

		containerLayout.addComponent(messageLayout);
	}

	private CssLayout createCellLayout() {
		CssLayout cellLayout = new CssLayout();
		cellLayout.addStyleName(CustomValoTheme.PADDING_LEFT);
		cellLayout.addStyleName(CustomValoTheme.PADDING_HALF_BOTTOM);
		cellLayout.addStyleName(CustomValoTheme.TABLE_CELL);
		return cellLayout;
	}

	private CssLayout getErrorLayout(ValidationResult validationResult, String headerCaption) {
		CssLayout errorLayout = createCellLayout();

		Label headerLabel = new Label(headerCaption);
		headerLabel.addStyleName(CustomValoTheme.LABEL_VERY_BOLD);
		headerLabel.addStyleName(CustomValoTheme.LABEL_LARGE);
		errorLayout.addComponent(headerLabel);

		Label messageLabel = new Label(validationResult.getMessage());
		errorLayout.addComponent(messageLabel);

		if (validationResult.getPriority() != Priority.RESULT) {
			headerLabel = new Label("Hint");
			headerLabel.addStyleName(CustomValoTheme.LABEL_VERY_BOLD);
			headerLabel.addStyleName(CustomValoTheme.LABEL_LARGE);
			errorLayout.addComponent(headerLabel);

			messageLabel = new Label(validationResult.getHelpMessage());
			errorLayout.addComponent(messageLabel);

		}
		return errorLayout;
	}

	private Label getColoredLabel(Priority priority) {
		String style = "";
		switch (priority) {
		case RESULT:
			style = CustomValoTheme.BACKGROUND_COLOR_VALID;
			break;
		case WARNING:
			style = CustomValoTheme.BACKGROUND_COLOR_WARNING;
			break;
		case ERROR:
			style = CustomValoTheme.BACKGROUND_COLOR_ERROR;
			break;
		default:
			break;
		}

		Label coloredLabel = new Label();
		coloredLabel.setWidth("5px");
		coloredLabel.addStyleName(style);
		coloredLabel.addStyleName(CustomValoTheme.TABLE_CELL);

		return coloredLabel;
	}

	private class ValidationResultGenerator extends CustomListComponentContentGenerator<ValidationResultWrapper> {

		@Override
		protected CssLayout getContent(ValidationResultWrapper item) {
			CssLayout detailsLayout = new CssLayout();
			detailsLayout.setSizeFull();
			detailsLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_ROW);
			detailsLayout.addComponent(getColoredLabel(item.getValidationResult().getPriority()));

			// use ????
			// CssLayout substanceDetailsLayout = new CssLayout();
			// substanceDetailsLayout
			// .addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_ROW);
			// substanceDetailsLayout.addComponent(getColoredLabel(item
			// .getValidationResult().getPriority()));

			switch (item.getRuleType()) {
			case COLUMN_CHANGE:
				break;
			case SUBSTANCE_ELUENT:
				initSubstanceEluentDetails(item, detailsLayout);
				break;
			case ELUTION_SELECTION:
				break;
			case INJECTION_VOLUME_FLOWRATE:
				initInjectionVolumeFlowRateDetails(item, detailsLayout);
				break;
			case BACK_PRESSURE:
				initBackPressureDetails(item, detailsLayout);
				break;
			case THEORETICAL_PLATES:
				initTheoreticalPlatesDetails(item, detailsLayout);
				break;
			default:
				break;
			}

			return detailsLayout;
		}
	}
}
