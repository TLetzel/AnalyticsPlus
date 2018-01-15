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

import java.util.Iterator;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.hswt.anap.model.Quiz;
import de.hswt.anap.model.Workflow;
import de.hswt.anap.service.quiz.impl.QuizFileServiceImpl;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.components.FixedContainerComponent;

@SpringComponent
@ViewScope
public class QuizInfoComponent extends FixedContainerComponent {

	private static final long serialVersionUID = 1L;

	private CssLayout contentLayout;

	@Autowired
	private QuizFileServiceImpl service;

	private ListIterator<Workflow> workflowIterator;

	private boolean nextWasCalled;

	private boolean prevWasCalled;

	@Override
	protected Component getContent() {
		contentLayout = new CssLayout();
		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF);
		contentLayout.setSizeFull();

		initInfoLayout();

		return contentLayout;
	}

	@Override
	protected String getTitle() {
		return "Quiz Info";
	}

	private void initInfoLayout() {
		Quiz quiz = service.getQuizFromFile();
		workflowIterator = (ListIterator<Workflow>) quiz.getWorkflows().iterator();
		Workflow firstWorkflow = getNextWorkflow();
		contentLayout.addComponent(buildContent(firstWorkflow));
	}

	private Component buildContent(Workflow workflow) {
		Iterator<String> contentIterator = workflow.getInfoContents().iterator();
		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();

		for (String infoLabel : workflow.getInfoLabels()) {
			CssLayout subLayout = getRowLayout();
			String tmpContent = contentIterator.next();
			subLayout.addComponent(new Label(infoLabel));
			subLayout.addComponent(getBoldLabel(tmpContent));

			contentLayout.addComponent(subLayout);
		}
		return contentLayout;
	}

	private Component getBoldLabel(String string) {
		Label boldLabel = new Label(string);
		boldLabel.addStyleName(CustomValoTheme.LABEL_VERY_BOLD);
		return boldLabel;
	}

	private CssLayout getRowLayout() {
		CssLayout cellLayout = new CssLayout();
		cellLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_ROW);
		cellLayout.addStyleName(CustomValoTheme.MARGIN_HALF_LEFT);
		return cellLayout;
	}

	private Workflow getNextWorkflow() {
		nextWasCalled = true;
		if (prevWasCalled) {
			prevWasCalled = false;
			workflowIterator.next();
		}
		return workflowIterator.next();
	}

	private Workflow getPrevWorkflow() {
		prevWasCalled = true;
		if (nextWasCalled) {
			nextWasCalled = false;
			workflowIterator.previous();
		}
		return workflowIterator.previous();
	}

	private void clearContent() {
		contentLayout.removeAllComponents();
	}

	public void buildPrevWorkflow() {
		if (workflowIterator.hasPrevious()) {
			clearContent();
			contentLayout.addComponent(buildContent(getPrevWorkflow()));
		}
	}

	public void buildNextWorkflow() {
		if (workflowIterator.hasNext()) {
			clearContent();
			contentLayout.addComponent(buildContent(getNextWorkflow()));
		}
	}
}
