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

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;

import de.hswt.anap.model.Experiment;
import de.hswt.anap.model.RuleType;
import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.ExamFlowController;
import de.hswt.anap.ui.vaadin.common.UiConstants;
import de.hswt.anap.ui.vaadin.common.UiContentConfiguration;
import de.hswt.anap.ui.vaadin.components.ContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.factories.ComponentFactory;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationRequestPayload;
import de.hswt.anap.ui.vaadin.handler.payload.ValidationResultPayload;
import de.hswt.hplcsl.Definition;
import de.hswt.hplcsl.GradientDefinition;

@SpringComponent
@ViewScope
public class ElutionComponent extends ContainerComponent {

	private static final long serialVersionUID = -9177146359682258178L;

	private static final int ROWS_VISIBLE = 3;

	private static final String GRADIENT_TIME = "time";

	private static final String GRADIENT_FRACTION = "fraction";

	private enum ElutionMode {
		GRADIENT, ISOCRATIC
	}

	@Autowired
	private ComponentFactory componentFactory;

	@Autowired
	private SharedModelProvider experimentProvider;

	@Autowired
	private UiContentConfiguration uiContentConfiguration;

	private Experiment experiment;

	@PropertyId("solventA")
	private ComboBox solventAComboBox;

	@PropertyId("solventB")
	private ComboBox solventBComboBox;

	@PropertyId("solventBFraction")
	private Slider solventBFractionSlider;

	@PropertyId("mixingVolume")
	private TextField mixingVolumeTestField;

	@PropertyId("nonMixingVolume")
	private TextField nonMixingVolumeTextField;

	private OptionGroup mode;

	private Grid gradientGrid;

	private CssLayout contentLayout;

	private CssLayout modeLayout;

	private CssLayout isocraticModeLayout;

	private CssLayout gradientModeLayout;

	private BeanFieldGroup<Experiment> fieldGroup;

	private BeanItemContainer<GradientDefinition> gradientDefinitionContainer;

	private List<GradientDefinition> gradientDefinitions;

	private static final Logger LOG = LoggerFactory
			.getLogger(ElutionComponent.class);

	@Override
	protected Component getContent() {

		experiment = experimentProvider.getExperiment();

		fieldGroup = new BeanFieldGroup<>(Experiment.class);
		fieldGroup.setReadOnly(false);

		gradientDefinitions = uiContentConfiguration.getGradientDefinitions();

		gradientDefinitionContainer = new BeanItemContainer<>(
				GradientDefinition.class);
		gradientDefinitionContainer.addAll(gradientDefinitions);

		contentLayout = new CssLayout();
		contentLayout.setWidth("100%");
//		contentLayout.addStyleName(CustomValoTheme.PADDING_HALF);


		// FIXME just a workaround for now
		// Setting solvent A comboBox
		solventAComboBox = componentFactory.createComboBox("Solvent A:");
		solventAComboBox.addItem(Definition.Solvent.WATER);
		solventAComboBox.setItemCaption(Definition.Solvent.WATER, "Water");
		solventAComboBox
				.select(solventAComboBox.getItemIds().iterator().next());
		solventAComboBox
				.addValueChangeListener(e -> handleSolventAValueChange());
		contentLayout.addComponent(componentFactory.createRowLayout(solventAComboBox));

		// Setting solvent B comboBox
		solventBComboBox = componentFactory.createComboBox("Solvent B:");
		solventBComboBox.addItem(Definition.Solvent.ACETONITRILE);
		solventBComboBox.setItemCaption(Definition.Solvent.ACETONITRILE,
				"Acetonitrile");
		solventBComboBox.addItem(Definition.Solvent.METHANOL);
		solventBComboBox
				.setItemCaption(Definition.Solvent.METHANOL, "Methanol");
		solventBComboBox
				.select(solventBComboBox.getItemIds().iterator().next());
		solventBComboBox
				.addValueChangeListener(e -> handleSolventBValueChange());
		contentLayout.addComponent(componentFactory.createRowLayout(solventBComboBox));

		CssLayout borderLayout = new CssLayout();
		borderLayout.setCaption("Elution mode");
		borderLayout.addStyleName(CustomValoTheme.LAYOUT_BORDER);
		borderLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_CONTENT_BOX);

		// Setting OptionGroupe mode
		mode = componentFactory.createOptionGroup("");
		mode.setItemCaption(mode.addItem(ElutionMode.ISOCRATIC), "Isocratic");
		mode.setItemCaption(mode.addItem(ElutionMode.GRADIENT), "Gradient");
		mode.select(mode.getItemIds().iterator().next());
		mode.addStyleName(CustomValoTheme.OPTIONGROUP_HORIZONTAL);
		mode.addStyleName(CustomValoTheme.MARGIN_HALF_HORIZONTAL);
		mode.addValueChangeListener(e -> updateMode());
		borderLayout.addComponent(mode);

		initIsocraticMode();
		initGradientMode();

		modeLayout = new CssLayout();
		modeLayout.setWidth("100%");
		modeLayout.addComponent(isocraticModeLayout);

		contentLayout.addComponent(componentFactory
				.createRowLayout(borderLayout));
		contentLayout.addComponent(modeLayout);

		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(experiment);

		updateEnabled(false);

		// must be after binding
		mixingVolumeTestField.setReadOnly(true);
		nonMixingVolumeTextField.setReadOnly(true);

		return contentLayout;
	}

	private void initGradientMode() {

		gradientModeLayout = new CssLayout();
		gradientModeLayout.setSizeFull();

		Label preColumn = new Label("<u>Pre-column volume:<u>",
				ContentMode.HTML);
		gradientModeLayout.addComponent(componentFactory
				.createRowLayout(preColumn));

		initMixingVolumeTextField();

		initNonMixingVolumeTextField();

		initGradientGrid();
	}

	private void initGradientGrid() {

		gradientGrid = new Grid();
		// TODO when given same values for time , calculator will use the value
		// defined later( is this ok, or should it throw an error?)
		gradientGrid.setContainerDataSource(gradientDefinitionContainer);
		gradientGrid.setHeightMode(HeightMode.ROW);
		gradientGrid.setHeightByRows(ROWS_VISIBLE);
		gradientGrid.addStyleName(CustomValoTheme.LAYOUT_BORDER);
		gradientGrid.setWidth("100%");
		gradientGrid.setCaption("Gradient values");
		gradientGrid.setColumnOrder(GRADIENT_TIME, GRADIENT_FRACTION);
		gradientGrid.getColumn(GRADIENT_TIME).setHeaderCaption("Time (min)");
		gradientGrid.getColumn(GRADIENT_FRACTION).setHeaderCaption(
				"Solvent B fraction");
		gradientGrid.setEditorEnabled(true);
		gradientGrid.getEditorFieldGroup().addCommitHandler(
				new CommitHandler() {

					private static final long serialVersionUID = -5501804310968059179L;

					@Override
					public void preCommit(CommitEvent commitEvent)
							throws CommitException {
					}

					@Override
					public void postCommit(CommitEvent commitEvent)
							throws CommitException {
						if (experiment.getDefinition().getCompounds() == null) {
							return;
						}
						handleGradientValuesChange();
					}
				});

		gradientModeLayout.addComponent(componentFactory
				.createRowLayout(gradientGrid));

	}

	private void handleGradientValuesChange() {
		experiment.getDefinition().setGradientDefinition(gradientDefinitions);
		handleValueChanged();
	}

	private void initNonMixingVolumeTextField() {

		nonMixingVolumeTextField = new TextField("Non-mixing volume");
		nonMixingVolumeTextField.setSizeFull();
		nonMixingVolumeTextField.setNullSettingAllowed(false);
		nonMixingVolumeTextField.setNullRepresentation("200");
		nonMixingVolumeTextField.setImmediate(true);
		nonMixingVolumeTextField.setDescription("Fixed value, not changeable");

		Label nonMixingVolumeUnitLabel = new Label(UiConstants.UTF8_MICRO + "L");
		nonMixingVolumeUnitLabel.setWidth("2rem");
		nonMixingVolumeUnitLabel.setCaption("");

		gradientModeLayout.addComponent(componentFactory.createRowLayout(
				nonMixingVolumeTextField, nonMixingVolumeUnitLabel));

		nonMixingVolumeTextField
				.addValueChangeListener(e -> handleNonMixingVolumeValueChange());

	}

	private void handleNonMixingVolumeValueChange() {
		nonMixingVolumeTextField.commit();
		handleValueChanged();
	}

	private void initMixingVolumeTextField() {

		mixingVolumeTestField = new TextField("Mixing volume");
		mixingVolumeTestField.setSizeFull();
		mixingVolumeTestField.setNullSettingAllowed(false);
		// TODO maybe move this (and similar) values to another place
		mixingVolumeTestField.setNullRepresentation("200");
		mixingVolumeTestField
				.addValueChangeListener(e -> handleMixingVolumeValueChange());
		mixingVolumeTestField.setImmediate(true);
		mixingVolumeTestField.setDescription("Fixed value, not changeable");

		Label mixingVolumeUnitLabel = new Label(UiConstants.UTF8_MICRO + "L");
		mixingVolumeUnitLabel.setWidth("2rem");
		mixingVolumeUnitLabel.setCaption("");

		gradientModeLayout.addComponent(componentFactory.createRowLayout(
				mixingVolumeTestField, mixingVolumeUnitLabel));

	}

	private void handleMixingVolumeValueChange() {

		mixingVolumeTestField.commit();
		handleValueChanged();
	}

	private void initIsocraticMode() {
		isocraticModeLayout = new CssLayout();
		isocraticModeLayout.setSizeFull();

		solventBFractionSlider = new Slider("Solvent B fraction in %", 0, 100);
		solventBFractionSlider.setConverter(percentageConverter);
		solventBFractionSlider.setSizeFull();
		solventBFractionSlider.setOrientation(SliderOrientation.HORIZONTAL);
		solventBFractionSlider
				.addValueChangeListener(e -> handleSolventBFractionSliderValueChange());

		isocraticModeLayout.addComponent(componentFactory
				.createRowLayout(solventBFractionSlider));

	}

	private void handleSolventBFractionSliderValueChange() {
		solventBFractionSlider.commit();
		handleValueChanged();
	}

	private void handleValueChanged() {

		if (solventAComboBox.getValue() == null || solventBComboBox == null) {
			return;
		}
		if (mode.getValue().equals(ElutionMode.ISOCRATIC)
				&& solventBFractionSlider.getValue() == null) {
			return;
		}
		if (mode.getValue().equals(ElutionMode.GRADIENT)
				&& gradientDefinitions == null) {
			return;
		}

		LOG.debug("Publish event inside ElutionComponent with payload{}",
				experiment);
		eventBus.publish(this, new ValidationRequestPayload(experiment,
				RuleType.ELUTION_SELECTION));

	}

	private void updateMode() {

		boolean gradientMode = false;
		if (mode.getValue().equals(ElutionMode.GRADIENT)) {
			gradientMode = true;
		}

		System.out.println(gradientMode);

		experiment.getDefinition().setGradient(gradientMode);

		if (gradientMode && modeLayout.getComponent(0).equals(isocraticModeLayout)) {
			modeLayout.replaceComponent(isocraticModeLayout, gradientModeLayout);
		} else if (!gradientMode && modeLayout.getComponent(0).equals(gradientModeLayout)) {
			modeLayout.replaceComponent(gradientModeLayout, isocraticModeLayout);
		}

		handleValueChanged();
	}

	private void handleSolventBValueChange() {
		solventBComboBox.commit();
		handleValueChanged();
	}

	private void handleSolventAValueChange() {
		solventAComboBox.commit();
		handleValueChanged();
	}

	@Override
	protected String getTitle() {
		return "Elution selection";
	}

	private Converter<Double, Double> percentageConverter = new Converter<Double, Double>() {
		private static final long serialVersionUID = -7523437694464432695L;

		@Override
		public Double convertToModel(Double value,
				Class<? extends Double> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			return value;
		}

		@Override
		public Double convertToPresentation(Double value,
				Class<? extends Double> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			return value;
		}

		@Override
		public Class<Double> getModelType() {
			return Double.class;
		}

		@Override
		public Class<Double> getPresentationType() {
			return Double.class;
		}

	};

	private void updateEnabled(boolean enabled) {

		contentLayout.setEnabled(enabled);
		solventBFractionSlider.setEnabled(enabled);
		setHeaderColorStyle(CustomValoTheme.BACKGROUND_COLOR_DISABLED, !enabled);
	}

	@EventBusListenerMethod
	private void handleValidationResultUpdate(ValidationResultPayload payload) {
		LOG.debug(
				"Receive event inside ElutionComponent experiment has changed with payload{}",
				experiment);
		boolean enabled = ExamFlowController.isEnabled(
				payload.getValidationResults(), RuleType.ELUTION_SELECTION,
				payload.getCurrentRuleType());

		updateEnabled(enabled);
	}

}
