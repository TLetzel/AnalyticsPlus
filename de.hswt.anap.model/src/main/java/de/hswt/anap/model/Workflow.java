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

import java.util.LinkedHashSet;

public class Workflow {

	private String name;

	private LinkedHashSet<Question> questions;

	private LinkedHashSet<String> infoLabels;

	private LinkedHashSet<String> infoContents;

	public Workflow(String name, LinkedHashSet<Question> questions, LinkedHashSet<String> infoLabels,
			LinkedHashSet<String> infoContents) {
		this.name = name;
		this.questions = questions;
		this.setInfoLabels(infoLabels);
		this.setInfoContents(infoContents);
	}

	public Workflow() {
	}

	public void addQuestion(Question question) {
		questions.add(question);
	}

	public void removeQuestion(Question question) {
		questions.remove(question);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedHashSet<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(LinkedHashSet<Question> questions) {
		this.questions = questions;
	}

	public LinkedHashSet<String> getInfoLabels() {
		return infoLabels;
	}

	public void setInfoLabels(LinkedHashSet<String> infoLabels) {
		this.infoLabels = infoLabels;
	}

	public LinkedHashSet<String> getInfoContents() {
		return infoContents;
	}

	public void setInfoContents(LinkedHashSet<String> infoContents) {
		this.infoContents = infoContents;
	}

}
