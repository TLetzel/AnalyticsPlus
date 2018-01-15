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
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.views.components.quiz.ExaminationComponent;
import de.hswt.anap.ui.vaadin.views.components.quiz.QuizHistoryComponent;
import de.hswt.anap.ui.vaadin.views.components.quiz.QuizInfoComponent;

@SpringView(name = SamplePrepExamView.VIEW_NAME)
@UIScope
public class SamplePrepExamView extends CssLayout implements View {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "samplePrepExam";

	@Autowired
	private ExaminationComponent quizComponent;

	@Autowired
	private QuizHistoryComponent quizHistoryComponent;

	@Autowired
	private QuizInfoComponent quizInfoComponent;

	private CssLayout wrapperLayout;

	private Button nextWorkflow = new Button("Next Workflow");

	private Button prevWorkflow = new Button("Previous Workflow");

	@PostConstruct
	private void postConstruct() {

		addStyleName(CustomValoTheme.SKIP_HEADER_VIEW);
		setSizeFull();

		wrapperLayout = new CssLayout();
		wrapperLayout.addStyleName(CustomValoTheme.PADDING_VERTICAL);
		wrapperLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		wrapperLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_STYLE);
		wrapperLayout.setSizeFull();
		addComponent(wrapperLayout);

		initNavigationLayout();
		initTopLayout();
		initBottomLayout();
	}

	private void initNavigationLayout() {
		CssLayout paddingLayout = new CssLayout();
		paddingLayout.addStyleName(CustomValoTheme.PADDING_HALF_BOTTOM);
		paddingLayout.setWidth("100%");
		wrapperLayout.addComponent(paddingLayout);

		prevWorkflow.addClickListener(e -> previousWorkflow());
		prevWorkflow.addStyleName(CustomValoTheme.MARGIN_HALF_BOTTOM);
		prevWorkflow.addStyleName(CustomValoTheme.MARGIN_HALF_LEFT);
		prevWorkflow.addStyleName("round-corner");
		paddingLayout.addComponent(prevWorkflow);

		nextWorkflow.addClickListener(e -> nextWorkflow());
		nextWorkflow.addStyleName(CustomValoTheme.MARGIN_HALF_BOTTOM);
		nextWorkflow.addStyleName(CustomValoTheme.MARGIN_HALF_LEFT);
		nextWorkflow.addStyleName("round-corner");
		paddingLayout.addComponent(nextWorkflow);

		Button clearHistory = new Button("Clear History");
		clearHistory.addClickListener(e -> clearHistory());
		clearHistory.addStyleName(CustomValoTheme.MARGIN_HALF_BOTTOM);
		clearHistory.addStyleName(CustomValoTheme.MARGIN_HALF_LEFT);
		clearHistory.addStyleName("round-corner");
		paddingLayout.addComponent(clearHistory);
	}

	private void initTopLayout() {
		CssLayout rowLayout = new CssLayout();
		rowLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_ROW);
		rowLayout.setWidth("100%");
		rowLayout.setHeight("20%");
		wrapperLayout.addComponent(rowLayout);

		CssLayout paddingLayout = new CssLayout();
		paddingLayout.addStyleName(CustomValoTheme.PADDING_HALF_BOTTOM);
		paddingLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		paddingLayout.setSizeFull();
		rowLayout.addComponent(paddingLayout);

		quizInfoComponent.setSizeFull();
		paddingLayout.addComponent(quizInfoComponent);

	}

	private void initBottomLayout() {

		CssLayout bottomLayout = new CssLayout();
		bottomLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_TABLE_ROW);
		bottomLayout.setHeight("100%");

		CssLayout paddingLayout = new CssLayout();
		paddingLayout.addStyleName(CustomValoTheme.PADDING_HALF_TOP);
		paddingLayout.setSizeFull();
		bottomLayout.addComponent(paddingLayout);

		initQuizLayout(paddingLayout);
		initHistoryLayout(paddingLayout);

		wrapperLayout.addComponent(bottomLayout);
	}

	private void initQuizLayout(CssLayout layout) {
		CssLayout wrapperLayout = new CssLayout();
		wrapperLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		wrapperLayout.setHeight("100%");
		wrapperLayout.setWidth("50%");
		layout.addComponent(wrapperLayout);

		quizComponent.setSizeFull();
		quizComponent.addStyleName("min-height2");
		wrapperLayout.addComponent(quizComponent);
	}

	private void initHistoryLayout(CssLayout layout) {
		CssLayout wrapperLayout = new CssLayout();
		wrapperLayout.addStyleName(CustomValoTheme.PADDING_HALF_HORIZONTAL);
		wrapperLayout.setHeight("100%");
		wrapperLayout.setWidth("50%");
		layout.addComponent(wrapperLayout);

		quizHistoryComponent.setWidth("100%");
		quizHistoryComponent.addStyleName("min-height2");
		wrapperLayout.addComponent(quizHistoryComponent);
	}

	private void nextWorkflow() {
		prevWorkflow.setEnabled(true);
		quizHistoryComponent.clearContent();
		quizInfoComponent.buildNextWorkflow();
		quizComponent.buildNextWorkflow();
		if (!quizComponent.hasNextWorkflow()) {
			nextWorkflow.setEnabled(false);
		}
	}

	private void previousWorkflow() {
		nextWorkflow.setEnabled(true);
		quizHistoryComponent.clearContent();
		quizInfoComponent.buildPrevWorkflow();
		quizComponent.buildPrevWorkflow();
		if (!quizComponent.hasPrevWorkflow()) {
			prevWorkflow.setEnabled(false);
		}
	}

	private void clearHistory() {
		quizHistoryComponent.clearContent();
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}
}
