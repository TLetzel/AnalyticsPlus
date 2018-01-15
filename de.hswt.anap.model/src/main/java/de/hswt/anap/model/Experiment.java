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

package de.hswt.anap.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import de.hswt.hplcsl.Column;
import de.hswt.hplcsl.Definition;
import de.hswt.hplcsl.HplcSimulator;
import de.hswt.hplcsl.Definition.Solvent;

public class Experiment {

	private Definition definition = initDefinition();

	@PostConstruct
	private void PostConstruct() {
		Column column = new Column();
		definition.setColumn(column);
	}

	private ColumnConfiguration column;

	private List<SubstanceConfiguration> substances = new ArrayList<>();

	private Optional<SolvingCompoundConfiguration> solvingCompound;

	private Double columnLength;

	private Double columnDiameter;

	private Double particleSize;

	private Double flowRate;

	private Double injectionVolume;

	private Solvent solventA;

	private Solvent solventB;

	private Double solventBFraction;

	private Double mixingVolume;

	private Double nonMixingVolume;

	private HplcSimulator hplcSimulator;

	private RuleType lastUsedRule;

	private RuleType lastGeneralUsedRule;

	public Double getMixingVolume() {
		return mixingVolume;
	}

	public void setMixingVolume(Double mixingVolume) {
		this.mixingVolume = mixingVolume;
		if (mixingVolume != null) {
			definition.setMixingVolume(mixingVolume);
		} else {
			definition.setMixingVolume(200);
		}
	}

	public Double getNonMixingVolume() {
		return nonMixingVolume;
	}

	public void setNonMixingVolume(Double nonMixingVolume) {
		this.nonMixingVolume = nonMixingVolume;
		if (nonMixingVolume != null) {
			definition.setNonMixingVolume(nonMixingVolume);
		} else {
			definition.setNonMixingVolume(200);
		}
	}

	public Solvent getSolventA() {
		return solventA;
	}

	public void setSolventA(Solvent solventA) {
		this.solventA = solventA;
		definition.setSolventA(solventA);
	}

	public Solvent getSolventB() {
		return solventB;
	}

	public void setSolventB(Solvent solventB) {
		this.solventB = solventB;
		definition.setSolventB(solventB);
	}

	public Double getSolventBFraction() {
		return solventBFraction;
	}

	public void setSolventBFraction(Double solventBFraction) {
		this.solventBFraction = solventBFraction;
		if (solventBFraction != null) {
			definition.setSolventBFraction(solventBFraction);
		} else {
			definition.setSolventBFraction(35);
		}
	}

	public Experiment() {
	}

	public List<SubstanceConfiguration> getSubstances() {
		return substances;
	}

	public void setSubstances(List<SubstanceConfiguration> substances) {
		this.substances = substances;
	}

	public Optional<SolvingCompoundConfiguration> getSolvent() {
		return solvingCompound;
	}

	public void setSolvent(SolvingCompoundConfiguration solvent) {
		this.solvingCompound = Optional.ofNullable(solvent);
	}

	public ColumnConfiguration getColumn() {
		return column;
	}

	public void setColumn(ColumnConfiguration column) {
		this.column = column;
	}

	public Double getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(Double columnLength) {
		this.columnLength = columnLength;
		setDefinitionColumn();
	}

	public Double getColumnDiameter() {
		return columnDiameter;
	}

	public void setColumnDiameter(Double columnDiameter) {
		this.columnDiameter = columnDiameter;
		setDefinitionColumn();
	}

	public Double getParticleSize() {
		return particleSize;
	}

	public void setParticleSize(Double particleSize) {
		this.particleSize = particleSize;
		setDefinitionColumn();
	}

	public Double getFlowRate() {
		return flowRate;
	}

	public void setFlowRate(Double flowRate) {
		this.flowRate = flowRate;
		definition.setFlowRate(flowRate);
	}

	public Double getInjectionVolume() {
		return injectionVolume;
	}

	public void setInjectionVolume(Double injectionVolume) {
		this.injectionVolume = injectionVolume;
		definition.setInjectionVolume(injectionVolume);
	}

	public void setLastGeneralUsedRule(RuleType lastGeneralUsedRule) {
		this.lastGeneralUsedRule = lastGeneralUsedRule;
	}

	public RuleType getLastGeneralUsedRule() {
		return lastGeneralUsedRule;
	}

	public RuleType getLastUsedRule() {
		return lastUsedRule;
	}

	public void setLastUsedRule(RuleType lastCurrentRule) {
		this.lastUsedRule = lastCurrentRule;
	}

	private void setDefinitionColumn() {
		if (column.getName() == null || columnLength == null
				|| columnDiameter == null || particleSize == null) {
			return;
		} else {
			Column column = new Column(this.column.getName(), particleSize,
					columnDiameter, columnLength);
			definition.setColumn(column);
		}
	}

	public Definition getDefinition() {
		return definition;
	}

	private Definition initDefinition() {
		hplcSimulator = new HplcSimulator();
		Definition definition = hplcSimulator.createDefinition();

		return definition;
	}

	public boolean isComplete() {
		if (substances != null) {
			if (column.getName() != null
					&& columnLength != null
					&& column.getDiameters() != null
					&& column.getLengths() != null
					&& column.getParticleSizes() != null
					&& (definition.getGradientDefinition() != null || solventBFraction != null)
					&& flowRate != null && injectionVolume != null
					&& solventA != null && solventB != null
					&& substances.size() > 0) {
				return true;
			}
		}
		return false;
	}
}
