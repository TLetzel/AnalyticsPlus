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

package de.hswt.anap.service.quiz.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hswt.anap.model.Quiz;
import de.hswt.anap.service.quiz.api.QuizFileService;

@Component
public class QuizFileServiceImpl implements QuizFileService {

	private static final String QUIZ_FILE = "/data/quiz.json";
	private int countHelper = 0;

	@Autowired
	private ObjectMapper mapper;

	public Quiz getQuizFromFile() {
		Quiz quiz = new Quiz();
		try {
			quiz = mapper.readValue(QuizFileServiceImpl.class.getResourceAsStream(QUIZ_FILE),
					new TypeReference<Quiz>() {
					});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return quiz;

	}

	// // Method returns next WorkflowData
	// public Workflow getNextWorkflow() {
	//
	// Workflow quiz = new Workflow();
	// ArrayList<String> workflowList = createWorkflowList();
	// try {
	// quiz =
	// mapper.readValue(QuizFileServiceImpl.class.getResourceAsStream(workflowList.get(getCountHelper())),
	// new TypeReference<Workflow>() {
	// });
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// // if last workflow got skipped, go to the 1st one again
	// setCountHelper(getCountHelper() + 1);
	// if (getCountHelper() == workflowList.size()) {
	// setCountHelper(0);
	// }
	// return quiz;
	// }

	// public void createQuizJson() {
	// LinkedHashSet<Answer> answer1 = new LinkedHashSet<Answer>();
	// answer1.add(new Answer("2"));
	// answer1.add(new Answer("4", "true"));
	// Question question1 = new Question("2+2");
	// question1.setAnswers(answer1);
	//
	// LinkedHashSet<Answer> answer2 = new LinkedHashSet<Answer>();
	// answer2.add(new Answer("8"));
	// answer2.add(new Answer("2", "true"));
	// Question question2 = new Question("5-3");
	// question2.setAnswers(answer2);
	//
	// LinkedHashSet<Answer> answer3 = new LinkedHashSet<Answer>();
	// answer3.add(new Answer("3"));
	// answer3.add(new Answer("0", "true"));
	// Question question3 = new Question("3*0");
	// question3.setAnswers(answer3);
	//
	// LinkedHashSet<Question> questionsSet = new LinkedHashSet<Question>();
	// questionsSet.add(question1);
	// questionsSet.add(question2);
	// questionsSet.add(question3);
	//
	// Workflow workflow1 = new Workflow("SPE Workflow", questionsSet);
	//
	// LinkedHashSet<Workflow> workflowSet = new LinkedHashSet<Workflow>();
	// workflowSet.add(workflow1);
	//
	// Quiz quiz = new Quiz(workflowSet);
	//
	// try {
	// File file = new File("./quiz.json");
	// mapper.writeValue(file, quiz);
	// System.out.println(file.getAbsolutePath());
	// System.out.println(mapper.writeValueAsString(quiz));
	// } catch (JsonProcessingException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	private int getCountHelper() {
		return countHelper;
	}

	private void setCountHelper(int countHelper) {
		this.countHelper = countHelper;
	}

}
