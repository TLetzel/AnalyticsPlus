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
import java.util.Iterator;
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
import com.vaadin.ui.TabSheet.Tab;

import de.hswt.anap.model.Answer;
import de.hswt.anap.model.Question;
import de.hswt.anap.model.Quiz;
import de.hswt.anap.model.Workflow;
import de.hswt.anap.service.quiz.impl.QuizFileServiceImpl;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.components.FixedContainerComponent;

@SpringComponent
@ViewScope
public class ExaminationComponent extends FixedContainerComponent {
	private static final long serialVersionUID = 1L;

	@Autowired
	private QuizFileServiceImpl service;

	@Autowired
	private ViewEventBus eventBus;

	private ListIterator<Workflow> workflowIterator;

	private CssLayout contentLayout;

	private boolean nextWasCalled;

	private boolean prevWasCalled;

	private Accordion contentAccordion = new Accordion();

	private Iterator<Question> questionIterator;

	private int stepCount = 1;

	@Override
	protected Component getContent() {
		contentLayout = new CssLayout();
		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF);
		contentLayout.setSizeFull();

		initQuestions();

		return contentLayout;
	}

	@Override
	protected String getTitle() {
		return "Quiz Content";
	}

	private void initQuestions() {
		Quiz quiz = service.getQuizFromFile();
		workflowIterator = (ListIterator<Workflow>) quiz.getWorkflows().iterator();
		Workflow firstWorkflow = getNextWorkflow();
		contentLayout.addComponent(buildAccordion(firstWorkflow));
	}

	private Accordion buildAccordion(Workflow workflow) {
		questionIterator = workflow.getQuestions().iterator();
		contentAccordion.setWidth("100%");

		if (questionIterator.hasNext()) {
			Question question = (Question) questionIterator.next();
			contentAccordion.addTab(getQuestionComponent(question), "Step 1");
		}

		return contentAccordion;
	}

	private BeanItemContainer<Answer> answerSetToBeanItemContainer(LinkedHashSet<Answer> set) {
		BeanItemContainer<Answer> container = new BeanItemContainer<>(Answer.class);
		set.forEach(container::addBean);
		return container;
	}

	// if an answer gets clicked this event is fired
	// used to send data to QuizHistory
	private void valueChange(ValueChangeEvent e) {
		Answer thisAnswer = (Answer) e.getProperty().getValue();
		String correct = "correct";
		eventBus.publish(this, thisAnswer);
		if (thisAnswer.getCorrectness().equals(correct)) {
			tabChange();
			e.getProperty().setReadOnly(true);
		}
	}

	private void tabChange() {
		if (questionIterator.hasNext()) {
			stepCount++;
			Question question = (Question) questionIterator.next();
			Tab newTab = contentAccordion.addTab(getQuestionComponent(question), "Step " + stepCount);
			contentAccordion.setSelectedTab(newTab);

			Component selectedTab = contentAccordion.getSelectedTab();
			selectedTab.addStyleName(CustomValoTheme.BORDER_COLOR_ALT1);
		}
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

		OptionGroup answers = new OptionGroup();
		Set<Answer> answersData = new LinkedHashSet<Answer>(question.getAnswers());
		for (Answer answer : answersData) {
			answer.setStepNum(stepCount);
		}
		LinkedHashSet<Answer> answerDataRandom = shuffleSet((LinkedHashSet<Answer>) answersData);

		answers.setWidth("100%");
		answers.addStyleName(CustomValoTheme.PADDING_LEFT);
		answers.addStyleName(CustomValoTheme.PADDING_HALF_RIGHT);
		answers.addStyleName(CustomValoTheme.PADDING_HALF_BOTTOM);
		if (!answerDataRandom.isEmpty())
			answers.setContainerDataSource(answerSetToBeanItemContainer((LinkedHashSet<Answer>) answerDataRandom));
		answers.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		answers.setItemCaptionPropertyId("text");
		answers.addValueChangeListener(this::valueChange);
		questionLayout.addComponent(answers);

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
			contentAccordion.removeAllComponents();
			contentLayout.addComponent(buildAccordion(getNextWorkflow()));
		}
	}

	public void buildPrevWorkflow() {
		if (workflowIterator.hasPrevious()) {
			clearContent();
			resetStepCount();
			contentAccordion.removeAllComponents();
			contentLayout.addComponent(buildAccordion(getPrevWorkflow()));
		}
	}
}
