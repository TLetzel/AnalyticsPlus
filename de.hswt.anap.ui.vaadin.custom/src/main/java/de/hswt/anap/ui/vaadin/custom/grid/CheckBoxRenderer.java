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

package de.hswt.anap.ui.vaadin.custom.grid;

import com.vaadin.ui.Grid.AbstractRenderer;

import de.hswt.anap.ui.vaadin.custom.client.grid.CheckBoxRendererServerRpc;

public abstract class CheckBoxRenderer extends AbstractRenderer<Boolean> {

	private static final long serialVersionUID = 5386395904074191985L;

	protected CheckBoxRenderer() {
		super(Boolean.class);
		registerRpc(new CheckBoxRendererServerRpc() {
			private static final long serialVersionUID = 5644588976640418681L;

			@Override
			public void change(String rowKey, boolean checked) {
				onChange(getItemId(rowKey), checked);
			}
		});
	}

	protected abstract void onChange(Object itemId, boolean checked);
}
