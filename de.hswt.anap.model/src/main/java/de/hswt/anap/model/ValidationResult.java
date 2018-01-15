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

public class ValidationResult {

	public enum Priority {
		STANDARD, RESULT, WARNING, ERROR
	}
	
	private Object validatedObject;
	
	private boolean valid;
	
	private String message;

	private String helpMessage;

	private Priority priority;
	
	public ValidationResult(boolean valid) {
		this(valid, Priority.STANDARD);
	}

	public ValidationResult(boolean valid, Priority priority) {
		this(null, valid, priority);
	}
	
	public ValidationResult(boolean valid, String validMessage, Priority priority){
		this.valid = valid;
		this.message = validMessage;
		this.priority = priority;
	}

	public ValidationResult(Object validatedObject, boolean valid) {
		this(validatedObject, valid, Priority.STANDARD);
	}
	
	public ValidationResult(Object validatedObject, boolean valid, Priority priority) {
		this(validatedObject, valid, null, null, priority);
	}

	public ValidationResult(boolean valid, String errorMessage, 
			String helpMessage, Priority priority) {
		this(null, valid, errorMessage, helpMessage, priority);
	}
	
	public ValidationResult(Object validateObject, boolean valid, String validMessage, Priority priority){
		this.validatedObject = validateObject;
		this.valid = valid;
		this.message = validMessage;
		this.priority = priority;
	}

	public ValidationResult(Object validatedObject, boolean valid, 
			String errorMessage, String helpMessage, Priority priority) {
		this.validatedObject = validatedObject;
		this.valid = valid;
		this.message = errorMessage;
		this.helpMessage = helpMessage;
		this.priority = priority;
	}

	public boolean isValid() {
		return valid;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String error){
		this.message = error;
	}
	
	public boolean hasMessage() {
		return message != null;
	}
	
	public String getHelpMessage() {
		return helpMessage;
	}

	public boolean hasHelpMessage() {
		return helpMessage != null;
	}
	
	public Object getValidatedObject() {
		return validatedObject;
	}
	
	public Priority getPriority() {
		return priority;
	}
}
