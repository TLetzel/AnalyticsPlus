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

import java.util.List;

import de.hswt.hplcsl.Column;
import de.hswt.hplcsl.Compound;
import de.hswt.hplcsl.Definition.Solvent;

public class CompoundsDeclaration {

	private Column column;
	
	private Solvent solvent;
	
	private List<Compound> compounds;

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Solvent getSolvent() {
		return solvent;
	}

	public void setSolvent(Solvent solvent) {
		this.solvent = solvent;
	}

	public List<Compound> getCompounds() {
		return compounds;
	}

	public void setCompounds(List<Compound> compounds) {
		this.compounds = compounds;
	}
}
