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
import java.util.LinkedHashSet;

public class Question implements Serializable {

	private static final long serialVersionUID = -355306598404133153L;

	private String text;

	private LinkedHashSet<Answer> answers;

	public Question(String text) {
		this.text = text;
	}

	public Question() {
	}

	public Question(String text, LinkedHashSet<Answer> answers) {
		this.text = text;
		this.answers = answers;
	}

	public void addAnswer(Answer answer) {
		answers.add(answer);
	}

	public void removeAnswer(Answer answer) {
		answers.remove(answer);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LinkedHashSet<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(LinkedHashSet<Answer> answers) {
		this.answers = answers;
	}

}
