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

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
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

import de.hswt.anap.ui.vaadin.common.CustomValoTheme;
import de.hswt.anap.ui.vaadin.common.UiConstants;
import de.hswt.anap.ui.vaadin.common.UiContentConfiguration;
import de.hswt.anap.ui.vaadin.components.ContainerComponent;
import de.hswt.anap.ui.vaadin.configuration.SharedModelProvider;
import de.hswt.anap.ui.vaadin.converter.RangedStringToDoubleConverter;
import de.hswt.anap.ui.vaadin.factories.ComponentFactory;
import de.hswt.hplcsl.Definition;
import de.hswt.hplcsl.GradientDefinition;

@SpringComponent
@ViewScope
public class MobilePhaseComponent extends ContainerComponent {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ColumnPropertiesComponent.class);

	private static final int ROWS_VISIBLE = 3;

	private static final String GRADIENT_TIME = "time";

	private static final String GRADIENT_FRACTION = "fraction";

	private enum ElutionMode {
		GRADIENT, ISOCRATIC
	}

	@Autowired
	private ViewEventBus eventBus;

	@Autowired
	private ComponentFactory componentFactory;

	@Autowired
	private SharedModelProvider definitionProvider;

	@Autowired
	private UiContentConfiguration uiContentConfiguration;

	@PropertyId("solventA")
	private ComboBox solventAComboBox;

	@PropertyId("solventB")
	private ComboBox solventBComboBox;

	@PropertyId("solventBFraction")
	private Slider solventBFractionSlider;

	@PropertyId("mixingVolume")
	private TextField mixingVolumeTextField;

	@PropertyId("nonMixingVolume")
	private TextField nonMixingVolumeTextField;

	private OptionGroup mode;

	private Grid gradientGrid;

	private CssLayout gradientModeLayout;

	private CssLayout isocraticModeLayout;

	private CssLayout contentLayout;

	private BeanFieldGroup<Definition> fieldGroup;

	private BeanItemContainer<GradientDefinition> gradientDefinitionContainer;

	private List<GradientDefinition> gradientDefinitions;

	private boolean initialized = false;

	@Override
	protected Component getContent() {
		contentLayout = new CssLayout();
		contentLayout.addStyleName(CustomValoTheme.BLOCK);
		contentLayout.setSizeFull();

		fieldGroup = new BeanFieldGroup<>(Definition.class);
		fieldGroup.setReadOnly(false);

		gradientDefinitions = uiContentConfiguration.getGradientDefinitions();

		gradientDefinitionContainer = new BeanItemContainer<>(GradientDefinition.class);
		gradientDefinitionContainer.addAll(gradientDefinitions);

		solventAComboBox = componentFactory.createComboBox("Solvent A:");
		solventBComboBox = componentFactory.createComboBox("Solvent B:");
		mode = new OptionGroup();

		fieldGroup.setItemDataSource(definitionProvider.getDefinition());

		solventAComboBox.addItem(Definition.Solvent.WATER);
		solventAComboBox.setItemCaption(Definition.Solvent.WATER, "Water");
		solventAComboBox.select(Definition.Solvent.WATER);
		solventAComboBox.addValueChangeListener(e -> handleSolventAValueChanged());
		contentLayout.addComponent(componentFactory.createRowLayout(solventAComboBox));

		solventBComboBox.addItem(Definition.Solvent.ACETONITRILE);
		solventBComboBox.setItemCaption(Definition.Solvent.ACETONITRILE, "Acetonitrile");
		solventBComboBox.addItem(Definition.Solvent.METHANOL);
		solventBComboBox.setItemCaption(Definition.Solvent.METHANOL, "Methanol");
		solventBComboBox.select(Definition.Solvent.ACETONITRILE);
		solventBComboBox.addValueChangeListener(e -> handleSolventBValueChanged());
		contentLayout.addComponent(componentFactory.createRowLayout(solventBComboBox));

		mode.setItemCaption(mode.addItem(ElutionMode.ISOCRATIC), "Isocratic");
		mode.setItemCaption(mode.addItem(ElutionMode.GRADIENT), "Gradient");
		mode.select(ElutionMode.ISOCRATIC);
		mode.setImmediate(true);
		mode.addStyleName(CustomValoTheme.OPTIONGROUP_HORIZONTAL);
		mode.addStyleName(CustomValoTheme.MARGIN_HALF_HORIZONTAL);
		mode.addValueChangeListener(e -> updateMode());

		CssLayout borderLayout = new CssLayout();
		borderLayout.setCaption("Elution mode");
		borderLayout.addStyleName(CustomValoTheme.LAYOUT_BORDER);
		borderLayout.addStyleName(CustomValoTheme.CSS_LAYOUT_CONTENT_BOX);
		borderLayout.addComponent(mode);

		contentLayout.addComponent(componentFactory.createRowLayout(borderLayout));

		initIsocraticMode();
		contentLayout.addComponent(isocraticModeLayout);

		initGradientMode();
		contentLayout.addComponent(gradientModeLayout);
		gradientModeLayout.setVisible(false);

		fieldGroup.bindMemberFields(this);

		// must be after binding
		mixingVolumeTextField.setReadOnly(true);
		nonMixingVolumeTextField.setReadOnly(true);

		initialized = true;

		return contentLayout;
	}

	private void updateMode() {
		boolean gradientMode = false;
		if (mode.getValue().equals(ElutionMode.ISOCRATIC)) {
			gradientMode = false;
		} else if (mode.getValue().equals(ElutionMode.GRADIENT) && !gradientModeLayout.isVisible()) {
			gradientMode = true;
		}

		isocraticModeLayout.setVisible(!gradientMode);
		gradientModeLayout.setVisible(gradientMode);

		definitionProvider.getDefinition().setGradient(gradientMode);
		handleValueChanged();
	}

	private void initGradientMode() {
		gradientModeLayout = new CssLayout();
		gradientModeLayout.setSizeFull();

		Label preColumn = new Label("<u>Pre-column volume:<u>", ContentMode.HTML);
		gradientModeLayout.addComponent(componentFactory.createRowLayout(preColumn));

		mixingVolumeTextField = new TextField("Mixing volume");
		mixingVolumeTextField.setSizeFull();
		mixingVolumeTextField.setConverter(new RangedStringToDoubleConverter(mixingVolumeTextField, 0.01, 999999.0));
		mixingVolumeTextField.addValueChangeListener(e -> handleMixingVolumeValueChanged());
		mixingVolumeTextField.setImmediate(true);
		mixingVolumeTextField.setDescription("Fixed value, not changeable");

		Label mixingVolumeUnitLabel = new Label(UiConstants.UTF8_MICRO + "L");
		mixingVolumeUnitLabel.setWidth("2rem");
		mixingVolumeUnitLabel.setCaption(""); // layout issue

		gradientModeLayout.addComponent(componentFactory.createRowLayout(mixingVolumeTextField, mixingVolumeUnitLabel));

		nonMixingVolumeTextField = new TextField("Non-mixing volume");
		nonMixingVolumeTextField.setSizeFull();
		nonMixingVolumeTextField
				.setConverter(new RangedStringToDoubleConverter(nonMixingVolumeTextField, 0.01, 999999.0));
		nonMixingVolumeTextField.addValueChangeListener(e -> handleNonMixingVolumeValueChanged());
		nonMixingVolumeTextField.setImmediate(true);
		nonMixingVolumeTextField.setDescription("Fixed value, not changeable");

		Label nonMixingVolumeUnitLabel = new Label(UiConstants.UTF8_MICRO + "L");
		nonMixingVolumeUnitLabel.setWidth("2rem");
		nonMixingVolumeUnitLabel.setCaption(""); // layout issue

		gradientModeLayout
				.addComponent(componentFactory.createRowLayout(nonMixingVolumeTextField, nonMixingVolumeUnitLabel));

		gradientGrid = new Grid();
		gradientGrid.setContainerDataSource(gradientDefinitionContainer);
		gradientGrid.setHeightMode(HeightMode.ROW);
		gradientGrid.setHeightByRows(ROWS_VISIBLE);
		gradientGrid.addStyleName(CustomValoTheme.LAYOUT_BORDER);
		gradientGrid.setWidth("100%");
		gradientGrid.setCaption("Gradient values");
		gradientGrid.setColumnOrder(GRADIENT_TIME, GRADIENT_FRACTION);
		gradientGrid.getColumn(GRADIENT_TIME).setHeaderCaption("Time (min)");
		gradientGrid.getColumn(GRADIENT_FRACTION).setHeaderCaption("Solvent B fraction");
		gradientGrid.setEditorEnabled(true);
		gradientGrid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
			private static final long serialVersionUID = -855959810641348646L;

			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {
			}

			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				handleGradientValuesChanged();
			}
		});
		gradientModeLayout.addComponent(componentFactory.createRowLayout(gradientGrid));
	}

	private void initIsocraticMode() {
		isocraticModeLayout = new CssLayout();
		isocraticModeLayout.setSizeFull();
		isocraticModeLayout.addStyleName(CustomValoTheme.FLEX_ITEM_EXPAND);

		solventBFractionSlider = new Slider("Solvent B fraction in %", 0, 100);
		solventBFractionSlider.setConverter(percentageConverter);
		solventBFractionSlider.setSizeFull();
		solventBFractionSlider.setOrientation(SliderOrientation.HORIZONTAL);
		solventBFractionSlider.addValueChangeListener(e -> handleSolventBFractionSliderValueChanged());

		isocraticModeLayout.addComponent(componentFactory.createRowLayout(solventBFractionSlider));
	}

	private void handleGradientValuesChanged() {
		definitionProvider.getDefinition().setGradientDefinition(gradientDefinitions);
		handleValueChanged();
		return;
	}

	private void handleSolventAValueChanged() {
		if (!initialized) {
			return;
		}
		solventAComboBox.commit();
		handleValueChanged();
	}

	public void handleSolventBValueChanged() {
		if (!initialized) {
			return;
		}
		solventBComboBox.commit();
		handleValueChanged();
	}

	private void handleMixingVolumeValueChanged() {
		if (!initialized) {
			return;
		}
		mixingVolumeTextField.commit();
		handleValueChanged();
	}

	private void handleNonMixingVolumeValueChanged() {
		if (!initialized) {
			return;
		}
		nonMixingVolumeTextField.commit();
		handleValueChanged();
	}

	private void handleSolventBFractionSliderValueChanged() {
		if (!initialized) {
			return;
		}
		solventBFractionSlider.commit();
		handleValueChanged();
	}

	private void handleValueChanged() {
		LOG.debug("publish event inside handleValueChanged with payload {}", definitionProvider.getDefinition());
		eventBus.publish(this, definitionProvider.getDefinition());
	}

	@Override
	protected String getTitle() {
		return "Mobile phase composition";
	}

	private Converter<Double, Double> percentageConverter = new Converter<Double, Double>() {
		private static final long serialVersionUID = -7523437694464432695L;

		@Override
		public Double convertToModel(Double value, Class<? extends Double> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			return value;
		}

		@Override
		public Double convertToPresentation(Double value, Class<? extends Double> targetType, Locale locale)
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
}
