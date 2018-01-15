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

package de.hswt.anap.ui.vaadin.views.components.tutorial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.hswt.anap.model.CompoundsDeclaration;
import de.hswt.anap.service.standards.api.IStandardsService;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.components.ContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.factories.ComponentFactory;
import de.hswt.hplcsl.Compound;
import de.hswt.hplcsl.Definition;
import de.hswt.hplcsl.Definition.Solvent;

@SpringComponent
@ViewScope
public class CompoundSelectionComponent extends ContainerComponent {

	private static final long serialVersionUID = -3175703529564696633L;

	private static final Logger LOG = LoggerFactory.getLogger(CompoundSelectionComponent.class);
	
	private static final int ROWS_VISIBLE = 10;

	@Autowired
	private ComponentFactory componentFactory;
	
	@Autowired
	private SharedModelProvider definitionProvider;

	@Autowired
	private IStandardsService standardsService;
	
	private Definition definition;
	
	private Grid compoundsGrid;
	
	private BeanItemContainer<Compound> compoundContainer;
	
	private String name;
	
	private Solvent solventB;
	
	private List<Double> concentrations;
	
	private List<Compound> compounds;

	public CompoundSelectionComponent(){
		super();
		
		compoundContainer = new BeanItemContainer<>(Compound.class);
		compoundContainer.addNestedContainerProperty("compoundName");
		compoundContainer.addNestedContainerProperty("concentration");
	}
	
	@Override
	protected Component getContent() {
		definition = definitionProvider.getDefinition();
		
		name = definition.getColumn().getName();
		solventB = definition.getSolventB();
		
		CssLayout contentLayout = new CssLayout();
		contentLayout.setSizeFull();
		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF);

		Label hintLabel = new Label();
		hintLabel.setValue("Double click a concentration value to edit.");
		hintLabel.addStyleName(CustomValoTheme.PADDING_BOTTOM);
		contentLayout.addComponent(hintLabel);

		compoundsGrid = new Grid(compoundContainer);
		compoundsGrid.setSizeFull();
		compoundsGrid.setHeightMode(HeightMode.ROW);
		compoundsGrid.setHeightByRows(ROWS_VISIBLE);
		compoundsGrid.setSelectionMode(SelectionMode.MULTI);
		compoundsGrid.removeAllColumns();
		compoundsGrid.addColumn("compoundName").setHeaderCaption("Available compounds").setExpandRatio(0);
		compoundsGrid.addColumn("concentration").setHeaderCaption("Concentration").setExpandRatio(0);
		compoundsGrid.setEditorEnabled(true);
		compoundsGrid.getColumn("compoundName").setEditable(false);
		compoundsGrid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
			private static final long serialVersionUID = -855959810641348646L;

				@Override
				public void preCommit(CommitEvent commitEvent) throws CommitException {
				}
			
				@Override
				public void postCommit(CommitEvent commitEvent) throws CommitException {
					handleCompoundsSelectionChanged();
				}
		});		
		
		compoundsGrid.addSelectionListener(e -> handleCompoundsSelectionChanged());
		contentLayout.addComponent(compoundsGrid);
		
		createFirst();
		
		
		
		return contentLayout;
	}
	
	private void createFirst(){
		List<CompoundsDeclaration> declarations = standardsService.getCompoundDeclarations();

		Optional<CompoundsDeclaration> result = declarations.stream().filter(
				e -> e.getColumn().getName().equals(definition.getColumn().getName()) && e.getSolvent().equals(definition.getSolventB())
		).findFirst();

		if ( !result.isPresent()) {
			return;
		}
		
		result.get().getCompounds().forEach(b -> b.setConcentration(30.0));
		result.get().getCompounds().forEach(c -> compoundContainer.addItem(c));

		handleCompoundsSelectionChanged();
	}

	private void handleCompoundsSelectionChanged() {
				
		compounds = new ArrayList<>();
		compoundsGrid.getSelectedRows().forEach(compound -> compounds.add((Compound) compound));
		concentrations = new ArrayList<>();
		compoundsGrid.getSelectedRows().forEach(concentration -> concentrations.add(((Compound) concentration).getConcentration()));
		for (int i = 0; i < concentrations.size(); i++) {
			compounds.get(i).setConcentration(concentrations.get(i));
		}
		definition.setCompounds(compounds);			
				
		handleSelectionChanged();
	}

	private void updateCompoundsSelection(Definition definition){
		compoundsGrid.getSelectionModel().reset();
				
		compoundContainer.removeAllItems();
			
		List<CompoundsDeclaration> declarations = standardsService.getCompoundDeclarations();

		Optional<CompoundsDeclaration> result = declarations.stream().filter(
				e -> e.getColumn().getName().equals(name) && e.getSolvent().equals(solventB)
		).findFirst();

		if ( !result.isPresent()) {
			return;
		}
		
		result.get().getCompounds().forEach(c -> {
			c.setConcentration(30.0);
			compoundContainer.addItem(c);
		});
	}
	
	private void updateDataCompoundSelection(Definition definition){
		this.definition = definition;
		List<CompoundsDeclaration> declaration = standardsService.getCompoundDeclarations();
		Optional<CompoundsDeclaration> result = declaration.stream().filter(
				e -> e.getColumn().getName().equals(name) && e.getSolvent().equals(solventB)
		).findFirst();
		
		if (!result.isPresent()){
			return;
		}
		
		List<Compound> compoundList = new ArrayList<>();
		List<CompoundsDeclaration> compoundsDeclaration = Collections.singletonList(result.get());
		for (int j = 0; j < compounds.size(); j++) {
			for (int k = 0; k < compoundsDeclaration.size(); k++) {
				for (int l = 0; l < compoundsDeclaration.get(k).getCompounds().size(); l++) {
					if (compounds.get(j).getCompoundName().equals(
							compoundsDeclaration.get(k).getCompounds().get(l).getCompoundName())){

						compoundsDeclaration.get(k).getCompounds().get(l).setConcentration(concentrations.get(j));
						compoundList.add(compoundsDeclaration.get(k).getCompounds().get(l));							
					}					
				}
			}
		}
		this.definition.setCompounds(compoundList);	
		handleSelectionChanged();
	}

	@Override
	protected String getTitle() {
		return "Compounds";
	}
	
	private void handleSelectionChanged(){
		LOG.debug("publish event inside updateDataCompoundSelection with payload {}", 
				definition);
		eventBus.publish(this, definition);
	}
	
	@EventBusListenerMethod
	private void handleDefinitionChange(org.vaadin.spring.events.Event<Definition> payload){
		LOG.debug("receive event inside handleDefinitionChanged column has changed with payload {}", payload.getPayload());
		if (payload.getSource().equals(this)) {
			return;
		}
		if(name != definition.getColumn().getName()){
			name = definition.getColumn().getName();
			updateCompoundsSelection(payload.getPayload());
		}
		if(solventB != definition.getSolventB()){
			solventB = definition.getSolventB();
			updateDataCompoundSelection(payload.getPayload());
		}
	}
}
