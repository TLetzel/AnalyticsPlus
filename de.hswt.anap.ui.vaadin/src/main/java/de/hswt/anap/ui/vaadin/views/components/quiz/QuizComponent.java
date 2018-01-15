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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

import de.hswt.anap.model.Answer;
import de.hswt.anap.model.Question;
import de.hswt.anap.model.Quiz;
import de.hswt.anap.model.Workflow;
import de.hswt.anap.service.quiz.impl.QuizFileServiceImpl;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.components.FixedContainerComponent;

@SpringComponent
@ViewScope
public class QuizComponent extends FixedContainerComponent {

	private static final long serialVersionUID = 1L;

	@Autowired
	private QuizFileServiceImpl service;

	@Autowired
	private ViewEventBus eventBus;

	private ListIterator<Workflow> workflowIterator;

	private CssLayout contentLayout;

	private boolean nextWasCalled;

	private boolean prevWasCalled;

	private int stepCount = 1;

	@Override
	protected Component getContent() {
		contentLayout = new CssLayout();
		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF);
		contentLayout.setSizeFull();

		initQuestionAccordion();

		return contentLayout;
	}

	@Override
	protected String getTitle() {
		return "Quiz Content";
	}

	private void initQuestionAccordion() {
		Quiz quiz = service.getQuizFromFile();
		workflowIterator = (ListIterator<Workflow>) quiz.getWorkflows().iterator();
		Workflow firstWorkflow = getNextWorkflow();
		contentLayout.addComponent(buildAccordion(firstWorkflow));
	}

	private Accordion buildAccordion(Workflow workflow) {
		Accordion contentAccordion = new Accordion();
		contentAccordion.setWidth("100%");
		contentAccordion.addSelectedTabChangeListener(this::tabChange);

		for (Question question : workflow.getQuestions()) {
			for (Answer answer : question.getAnswers()) {
				answer.setStepNum(stepCount);
			}
			contentAccordion.addTab(getQuestionComponent(question), "Step " + (stepCount));
			stepCount++;
		}
		return contentAccordion;
	}

	private BeanItemContainer<Answer> answerSetToBeanItemContainer(LinkedHashSet<Answer> set) {
		BeanItemContainer<Answer> container = new BeanItemContainer<>(Answer.class);
		set.forEach(container::addBean);
		return container;
	}

	private void valueChange(ValueChangeEvent e) {
		eventBus.publish(this, e.getProperty().getValue());
	}

	private void tabChange(SelectedTabChangeEvent e) {
		Component selectedTab = e.getTabSheet().getSelectedTab();
		selectedTab.addStyleName(CustomValoTheme.BORDER_COLOR_ALT1);
	}

	private Component getQuestionComponent(Question question) {
		CssLayout questionLayout = new CssLayout();
		questionLayout.setSizeFull();

		Label currentQuestionTextLabel = new Label(question.getText());
		currentQuestionTextLabel.setWidth("100%");
		currentQuestionTextLabel.addStyleName(CustomValoTheme.LABEL_VERY_BOLD);
		currentQuestionTextLabel.addStyleName(CustomValoTheme.PADDING_HALF_LEFT);
		currentQuestionTextLabel.addStyleName(CustomValoTheme.PADDING_HALF_RIGHT);
		currentQuestionTextLabel.addStyleName(CustomValoTheme.PADDING_HALF_TOP);
		questionLayout.addComponent(currentQuestionTextLabel);

		OptionGroup answersOptionGroup = new OptionGroup();
		Set<Answer> answersData = new LinkedHashSet<Answer>(question.getAnswers());
		LinkedHashSet<Answer> answerDataRandom = shuffleSet((LinkedHashSet<Answer>) answersData);

		answersOptionGroup.setWidth("100%");
		answersOptionGroup.addStyleName(CustomValoTheme.PADDING_LEFT);
		answersOptionGroup.addStyleName(CustomValoTheme.PADDING_HALF_RIGHT);
		answersOptionGroup.addStyleName(CustomValoTheme.PADDING_HALF_BOTTOM);
		if (!answerDataRandom.isEmpty())
			answersOptionGroup
					.setContainerDataSource(answerSetToBeanItemContainer((LinkedHashSet<Answer>) answerDataRandom));
		answersOptionGroup.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		answersOptionGroup.setItemCaptionPropertyId("text");
		answersOptionGroup.addValueChangeListener(this::valueChange);
		questionLayout.addComponent(answersOptionGroup);

		return questionLayout;
	}

	private LinkedHashSet<Answer> shuffleSet(LinkedHashSet<Answer> set) {
		// Answers get converted to List so they can be shuffled, afterwards
		// they are converted back
		List<Answer> shuffleList = new ArrayList<Answer>();
		shuffleList.addAll(set);
		Collections.shuffle(shuffleList);
		set.clear();
		set.addAll(shuffleList);
		return set;
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

	private void resetStepCount() {
		stepCount = 1;
	}

	public boolean hasNextWorkflow() {
		return workflowIterator.hasNext();
	}

	public boolean hasPrevWorkflow() {
		return workflowIterator.hasPrevious();
	}

	public void buildNextWorkflow() {
		if (workflowIterator.hasNext()) {
			clearContent();
			resetStepCount();
			contentLayout.addComponent(buildAccordion(getNextWorkflow()));
		}
	}

	public void buildPrevWorkflow() {
		if (workflowIterator.hasPrevious()) {
			clearContent();
			resetStepCount();
			contentLayout.addComponent(buildAccordion(getPrevWorkflow()));
		}
	}

}
