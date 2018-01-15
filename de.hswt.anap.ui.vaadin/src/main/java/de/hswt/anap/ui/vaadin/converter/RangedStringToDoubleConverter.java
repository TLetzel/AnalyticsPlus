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

package de.hswt.anap.ui.vaadin.converter;

import java.util.Locale;

import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.ui.AbstractField;

public class RangedStringToDoubleConverter extends StringToDoubleConverter {

	private static final long serialVersionUID = 8951336052709951046L;

	private final double minValue;

	private final double maxValue;

	private final AbstractField<String> field;
	
	public RangedStringToDoubleConverter(double minValue, double maxValue) {
		this(null, minValue, maxValue);
	}

	public RangedStringToDoubleConverter(AbstractField<String> field, double minValue, double maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.field = field;
	}

	@Override
	public Double convertToModel(String givenValue, Class<? extends Double> targetType, Locale locale)
			throws ConversionException {
		
		String value = givenValue.replace(",", ".");
		
		Double result = minValue;
		Double converted = null;
		try {
			result = super.convertToModel(value, targetType, Locale.ENGLISH);
			converted = result;
		} catch (ConversionException ex) {
		}

		if (result == null || result < minValue) {
        	result = minValue;
        } else if (result > maxValue) {
        	result = maxValue;
        }

        // result changed, so update field
        if ( !result.equals(converted)) {
        	field.setValue("" + result);
        }

		return result;
	}
}
