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

/**
 * @author Fabian
 */
public class SolvingCompoundConfiguration {

	private String name;
	
	private double bestDisolvingZoneLowerLimit;

	private double bestDisolvingZoneUpperLimit;
	
	private double mediumDisolvingZoneLowerLimit;
	
	private double mediumDisolvingZoneUpperLimit;

	public SolvingCompoundConfiguration() {
	}

	public SolvingCompoundConfiguration(String solventName, double bestDisolvingZoneLowerLimit, double bestDisolvingZoneUpperLimit,
			double mediumDisolvingZoneLowerLimit, double mediumDisolvingZoneUpperLimit) {

		this.bestDisolvingZoneLowerLimit = bestDisolvingZoneLowerLimit;
		this.bestDisolvingZoneUpperLimit = bestDisolvingZoneUpperLimit;
		this.mediumDisolvingZoneLowerLimit = mediumDisolvingZoneLowerLimit;
		this.mediumDisolvingZoneUpperLimit = mediumDisolvingZoneUpperLimit;
		this.name = solventName;

	}

	public double getBestDisolvingZoneLowerLimit() {
		return bestDisolvingZoneLowerLimit;
	}

	public double getBestDisolvingZoneUpperLimit() {
		return bestDisolvingZoneUpperLimit;
	}

	public double getMediumDisolvingZoneLowerLimit() {
		return mediumDisolvingZoneLowerLimit;
	}

	public double getMediumDisolvingZoneUpperLimit() {
		return mediumDisolvingZoneUpperLimit;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
