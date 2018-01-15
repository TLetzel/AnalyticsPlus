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

package de.hswt.anap.ui.vaadin.views.components.quiz;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.hswt.anap.model.Answer;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.components.CustomListComponent;
import de.hswt.anap.ui.vaadin.components.CustomListComponentContentGenerator;
import de.hswt.anap.ui.vaadin.components.FixedContainerComponent;

@SpringComponent
@ViewScope
public class QuizHistoryComponent extends FixedContainerComponent {

	private static final long serialVersionUID = 1L;

	private CssLayout contentLayout;

	private CustomListComponent<Answer> listComponent;

	private List<Answer> answerList = new ArrayList<Answer>();

	@Override
	protected Component getContent() {

		contentLayout = new CssLayout();
		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF_TOP);
		contentLayout.setSizeFull();

		listComponent = new CustomListComponent<>(new SubResultGenerator());
		contentLayout.addComponent(listComponent);

		return contentLayout;
	}

	@EventBusListenerMethod
	private void handleQuizAnswers(Answer answer) {
		listComponent.removeAll();
		addToHistory(answer);
		listComponent.addAll(answerList);
	}

	// new answers get added to the top of quizHistory
	private void addToHistory(Answer answer) {
		List<Answer> helperList = new ArrayList<Answer>();
		helperList.add(answer);
		if (!answerList.isEmpty()) {
			helperList.addAll(answerList);
		}
		answerList = helperList;
	}

	private Label getColoredLabel(String correctness) {

		String style = "";
		switch (correctness) {
		case "correct":
			style = CustomValoTheme.BACKGROUND_COLOR_VALID;
			break;
		case "halfCorrect":
			style = CustomValoTheme.BACKGROUND_COLOR_WARNING;
			break;
		case "incorrect":
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

	@Override
	protected String getTitle() {
		return "Quiz History";
	}

	private class SubResultGenerator extends CustomListComponentContentGenerator<Answer> {

		@Override
		protected CssLayout getContent(Answer answer) {
			CssLayout detailsLayout = new CssLayout();
			detailsLayout.setSizeFull();
			detailsLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_ROW);
			detailsLayout.addComponent(getColoredLabel(answer.getCorrectness()));

			CssLayout contentLayout = new CssLayout();
			contentLayout.addStyleName(CustomValoTheme.PADDING_LEFT);
			contentLayout.addStyleName(CustomValoTheme.TABLE_CELL);
			detailsLayout.addComponent(contentLayout);

			Label headerLabel = new Label("Step " + answer.getStepNum() + ": " + answer.getText());
			headerLabel.addStyleName(CustomValoTheme.LABEL_VERY_BOLD);
			headerLabel.addStyleName(CustomValoTheme.LABEL_LARGE);
			contentLayout.addComponent(headerLabel);

			Label messageLabel = new Label(answer.getFeedback());
			contentLayout.addComponent(messageLabel);

			return detailsLayout;
		}
	}

	public void clearContent() {
		contentLayout.removeAllComponents();
		answerList.removeAll(answerList);
		listComponent = new CustomListComponent<>(new SubResultGenerator());
		contentLayout.addComponent(listComponent);
	}
}
