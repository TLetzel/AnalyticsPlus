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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColumnConfiguration implements Serializable{
	
	private static final long serialVersionUID = 683764297249433521L;

	private String name;
	private List<Double> particleSizes = new ArrayList<Double>();
	private List<Double> diameters = new ArrayList<Double>();
	private List<Double> lengths = new ArrayList<Double>();
	
	public ColumnConfiguration() {}
	
	public ColumnConfiguration(String name, List<Double> particleSizes, List<Double> diameters, List<Double> lengths){
		this.name = name;
		this.particleSizes = particleSizes;
		this.diameters = diameters;
		this.lengths = lengths;
	}
	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Double> getParticleSizes() {
		return particleSizes;
	}

	public void setParticleSizes(List<Double> particleSizes) {
		this.particleSizes = particleSizes;
	}
	
	public List<Double> getDiameters() {
		return diameters;
	}

	public void setDiameters(List<Double> diameters) {
		this.diameters = diameters;
	}
	
	public List<Double> getLengths() {
		return lengths;
	}
	
	public void setLengths(List<Double> lengths) {
		this.lengths = lengths;
	}
	
	public String getCaption() {
		return name;
	}

}
