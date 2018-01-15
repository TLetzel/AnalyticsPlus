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

public class SubstanceConfiguration {

	private double logD;
	private String name;
	
	private String columnName;
	
	private boolean selected = false;
	
	public SubstanceConfiguration() {
	}
	 
	public SubstanceConfiguration(String substanceName, double logD, String columnName) {
		this.logD =logD;
		this.name = substanceName;
		this.columnName = columnName;
	}
	
	public void setLogD(double logD) {
		this.logD = logD;
	}

	public double getLogD() {
		return logD;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
}
