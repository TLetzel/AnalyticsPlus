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

package de.hswt.anap.ui.vaadin;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

import de.hswt.hplcsl.Results;

public class Warnings {
	
	private double temperatureValue;
	private double flowRate;
	private double injectionVolume;
	private double solventBValue;
	private Results results;
	
	public Warnings(double temperatureValue, Results results,
			double flowRate, double injectionVolume, double solventBValue){
		
		this.temperatureValue = temperatureValue;
		this.flowRate = flowRate;
		this.injectionVolume = injectionVolume;
		this.solventBValue = solventBValue;
		this.results = results;
	}
	
	
	public void setWarnings(double temperatureValue, Results results){
		if(results.getTheoreticalPlates() < 1000 && results.getBackpressure() > 400){
			Notification notif = new Notification(
					"Number of theoretical plates could be better and backpressure is too heigh!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());	
		}else if(results.getTheoreticalPlates() < 1000){
			Notification notif = new Notification(
					"Number of theoretical plates could be better!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());
		}else if(results.getBackpressure() > 400){
			Notification notif = new Notification(
					"Warning! Backpressure is too heigh!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());	
		}else if (temperatureValue >= 71) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}
	}
	public void validateRatio(double flowRate, double injectionVolume) {
		if ((flowRate / injectionVolume) <= 10) {
			Notification.show("richtig");
		} else {
			Notification notif = new Notification(
					"Warning, Ratio is not right! It must be lower than 10:1",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());
		}

	}
	public void setWarnings(Warnings warnings){
		if (warnings.getTemperatureValue() >= 71 && warnings.getResults().getTheoreticalPlates() < 1000 &&
				warnings.getResults().getBackpressure() > 400 && (warnings.getFlowRate() / warnings.getInjectionVolume()) > 10) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					"Number of theoretical plates could be better, backpressure is too heigh "
					+ "and Ratio of flow rate and injection volume is not correct!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}else if(warnings.getResults().getTheoreticalPlates() < 1000 && warnings.getResults().getBackpressure() > 400 
				&& (warnings.getFlowRate() / warnings.getInjectionVolume()) > 10){
			Notification notif = new Notification(
					"Number of theoretical plates could be better and backpressure is too heigh!",
					"Ratio of flow rate and injection volume is not correct",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());	
		}else if (warnings.getTemperatureValue() >= 71 && warnings.getResults().getTheoreticalPlates() < 1000 
				&& warnings.getResults().getBackpressure() > 400) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					"Number of theoretical plates could be better, backpressure is too heigh ",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}else if (warnings.getTemperatureValue() >= 71 && warnings.getResults().getTheoreticalPlates() < 1000 
				&& (warnings.getFlowRate() / warnings.getInjectionVolume()) > 10) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					"Number of theoretical plates could be better "
					+ "and Ratio of flow rate and injection volume is not correct!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}else if (warnings.getTemperatureValue() >= 71 && warnings.getResults().getBackpressure() > 400
				&& (warnings.getFlowRate() / warnings.getInjectionVolume()) > 10) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					"Backpressure is too heigh and Ratio of flow rate and injection volume is not correct!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}else if (warnings.getTemperatureValue() >= 71 && warnings.getResults().getTheoreticalPlates() < 1000) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					"Number of theoretical plates could be better!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}else if (warnings.getTemperatureValue() >= 71 && warnings.getResults().getBackpressure() > 400) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					"Backpressure is too heigh!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}else if (warnings.getTemperatureValue() >= 71 && (warnings.getFlowRate() / warnings.getInjectionVolume()) > 10) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					"and Ratio of flow rate and injection volume is not correct!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}else if(warnings.getResults().getTheoreticalPlates() < 1000 && (warnings.getFlowRate() / warnings.getInjectionVolume()) > 10){
			Notification notif = new Notification(
					"Number of theoretical plates could be better!",
					"Ratio of flow rate and injection volume is not correct",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());
		}else if(warnings.getResults().getBackpressure() > 400 && (warnings.getFlowRate() / warnings.getInjectionVolume()) > 10){
			Notification notif = new Notification(
					"Warning! Backpressure is too heigh!",
					"Ratio of flow rate and injection volume is not correct",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());	
		}else if(warnings.getResults().getTheoreticalPlates() < 1000 && warnings.getResults().getBackpressure() > 400){
			Notification notif = new Notification(
					"Number of theoretical plates could be better and backpressure is too heigh!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());	
		}else if(warnings.getResults().getTheoreticalPlates() < 1000){
			Notification notif = new Notification(
					"Number of theoretical plates could be better!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());
		}else if(warnings.getResults().getBackpressure() > 400){
			Notification notif = new Notification(
					"Warning! Backpressure is too heigh!",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());	
		}else if (warnings.getTemperatureValue() >= 71) {
			Notification notif = new Notification(
					"Warning! Temperature could be too heigh for some columns",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}else if ((warnings.getFlowRate() / warnings.getInjectionVolume()) > 10) {
			Notification notif = new Notification(
					"Ratio of flow rate and injection volume is not correct",
					Notification.Type.WARNING_MESSAGE);
			notif.show(Page.getCurrent());		
		}
	}

	public double getTemperatureValue() {
		return temperatureValue;
	}

	public void setTemperatureValue(double temperatureValue) {
		this.temperatureValue = temperatureValue;
	}

	public double getFlowRate() {
		return flowRate;
	}

	public void setFlowRate(double flowRate) {
		this.flowRate = flowRate;
	}

	public double getInjectionVolume() {
		return injectionVolume;
	}

	public void setInjectionVolume(double injectionVolume) {
		this.injectionVolume = injectionVolume;
	}

	public double getSolventBValue() {
		return solventBValue;
	}

	public void setSolventBValue(double solventBValue) {
		this.solventBValue = solventBValue;
	}

	public Results getResults() {
		return results;
	}

	public void setResults(Results results) {
		this.results = results;
	}
	
	
}
