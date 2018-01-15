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

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.model.SolvingCompoundConfiguration;
import de.hswt.anap.model.SubstanceConfiguration;
import de.hswt.anap.service.standards.api.IStandardsService;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.ExamFlowController;
import de.hswt.anap.ui.vaadin.components.ContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationRequestPayload;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationResultPayload;

@SpringComponent
@ViewScope
public class SubstanceSelectionComponent extends ContainerComponent {

	private static final long serialVersionUID = -6482170378126271837L;

	@Autowired
	private IStandardsService standardsService;

	@Autowired
	private SharedModelProvider sharedModelProvider;

	private CssLayout contentLayout;

	private Grid solventsGrid;

	private Grid substanceGrid;

	private Experiment experiment;

	private BeanContainer<String, SubstanceConfiguration> substanceContainer;

	private BeanContainer<String, SolvingCompoundConfiguration> solventContainer;

	private static final Logger LOG = LoggerFactory.getLogger(SubstanceSelectionComponent.class);

	@Override
	protected Component getContent() {
		experiment = sharedModelProvider.getExperiment();

		contentLayout = new CssLayout();
		contentLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_FLEX_ROW);

		initSubstancesGrid();

		initSolventsGrid();

		return contentLayout;
	}

	private void initSubstancesGrid() {

		substanceContainer = new BeanContainer<>(SubstanceConfiguration.class);
		substanceContainer.setBeanIdProperty("name");

		CssLayout layout = new CssLayout();
		layout.addStyleName(CustomValoTheme.MARGIN_HALF);
		contentLayout.addComponent(layout);

		substanceGrid = new Grid("Available substances");
		substanceGrid.setContainerDataSource(substanceContainer);
		substanceGrid.setHeight("15rem");
		substanceGrid.setWidth("15rem");
		substanceGrid.setSelectionMode(SelectionMode.MULTI);
		substanceGrid.removeAllColumns();
		substanceGrid.addColumn("name").setHeaderCaption("Substance");
		substanceGrid.addColumn("logD").setHeaderCaption("logD");
		substanceGrid.setColumnOrder("name", "logD");
		substanceGrid.addStyleName(CustomValoTheme.BLOCK);
		substanceGrid.addSelectionListener(e -> handleValidation());

		layout.addComponent(substanceGrid);

		initSubstances();
	}

	private void initSolventsGrid() {
		solventContainer = new BeanContainer<>(SolvingCompoundConfiguration.class);
		solventContainer.setBeanIdProperty("name");
		solventContainer.addAll(standardsService.getAvailableSolvingCompunds());

		CssLayout layout = new CssLayout();
		layout.addStyleName(CustomValoTheme.MARGIN_HALF);
		contentLayout.addComponent(layout);

		solventsGrid = new Grid("Solving compounds");
		solventsGrid.setContainerDataSource(solventContainer);
		solventsGrid.setHeight("15rem");
		solventsGrid.setWidth("10rem");
		solventsGrid.setSelectionMode(SelectionMode.SINGLE);
		solventsGrid.removeAllColumns();
		solventsGrid.addColumn("name").setHeaderCaption("Solvent");
		solventsGrid.removeHeaderRow(0);
		solventsGrid.addStyleName(CustomValoTheme.BLOCK);
		solventsGrid.addSelectionListener(e -> handleValidation());
		layout.addComponent(solventsGrid);
	}

	private void initSubstances() {
		ArrayList<SubstanceConfiguration> list = new ArrayList<SubstanceConfiguration>();
		for (SubstanceConfiguration substance : standardsService.getAvailableSubstances()) {
			if (substance.getColumnName().equals(experiment.getColumn().getName())) {
				list.add(substance);
			}

		}
		substanceContainer.addAll(list);
	}

	private void handleValidation() {
		if (!updateExperiment()) {
			return;
		}

		if (experiment.getSubstances().isEmpty() || !experiment.getSolvent().isPresent()) {
			return;
		}
		LOG.debug("Publish event inside SubstanceSelectionComponent with payload{}", experiment);
		eventBus.publish(this, new ValidationRequestPayload(experiment, RuleType.SUBSTANCE_ELUENT));
	}

	private boolean updateExperiment() {

		Collection<Object> substances = substanceGrid.getSelectedRows();

		BeanItem<SolvingCompoundConfiguration> solvent = solventContainer.getItem(solventsGrid.getSelectedRow());
		if (solvent == null) {
			return false;
		}
		experiment.setSolvent(solvent.getBean());

		ArrayList<SubstanceConfiguration> temp = new ArrayList<>();
		substances.forEach(o -> temp.add(substanceContainer.getItem(o).getBean()));

		experiment.setSubstances(temp);

		return true;
	}

	@EventBusListenerMethod
	private void handleValidationResultUpdate(ValidationResultPayload payload) {
		LOG.debug("Receive event inside SubstanceSelectionComponent experiemt changed with payload{}", payload);
		boolean enabled = ExamFlowController.isEnabled(payload.getValidationResults(), RuleType.SUBSTANCE_ELUENT,
				payload.getCurrentRuleType());

		updateEnabled(enabled);

		Collection<Object> substances = substanceGrid.getSelectedRows();

		ArrayList<SubstanceConfiguration> temp = new ArrayList<>();
		substances.forEach(substance -> temp.add(substanceContainer.getItem(substance).getBean()));

		if (!filterSubstances().containsAll(temp)) {
			substanceGrid.getSelectionModel().reset();
			substanceContainer.removeAllItems();
		}

		ArrayList<SubstanceConfiguration> list = new ArrayList<SubstanceConfiguration>();
		substanceContainer.removeAllItems();
		for (SubstanceConfiguration substance : standardsService.getAvailableSubstances()) {
			if (substance.getColumnName().equals(experiment.getColumn().getName())) {
				list.add(substance);
			}
		}
		substanceContainer.addAll(list);
	}

	private ArrayList<SubstanceConfiguration> filterSubstances() {
		ArrayList<SubstanceConfiguration> list = new ArrayList<SubstanceConfiguration>();
		for (SubstanceConfiguration substance : standardsService.getAvailableSubstances()) {
			if (substance.getColumnName().equals(experiment.getColumn().getName())) {
				list.add(substance);
			}
		}
		return list;
	}

	private void updateEnabled(boolean enabled) {
		contentLayout.setEnabled(enabled);
		setHeaderColorStyle(CustomValoTheme.BACKGROUND_COLOR_DISABLED, !enabled);
	}

	@Override
	protected String getTitle() {
		return "Substance selection";
	}
}
