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

package de.hswt.anap.ui.vaadin.custom.client.grid;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.client.renderers.ComplexRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(de.hswt.anap.ui.vaadin.custom.grid.CheckBoxRenderer.class)
public class CheckBoxRendererConnector extends AbstractRendererConnector<Boolean> {

	private static final long serialVersionUID = 3149122218001958038L;

	@Override
	protected Renderer<Boolean> createRenderer() {
		return new ComplexRenderer<Boolean>() {

			@Override
			public void init(RendererCellReference cell) {
				System.out.println("inside init renderer");
				InputElement element = Document.get().createCheckInputElement();
				cell.getElement().appendChild(element);
			}

			@Override
			public void render(RendererCellReference cell, Boolean data) {
				System.out.println("inside: " + data);
				InputElement element = cell.getElement().getChild(0).cast();
				element.setChecked(Boolean.TRUE == data);
			}

			@Override
			public Collection<String> getConsumedEvents() {
				return Arrays.asList(BrowserEvents.CHANGE);
			}

			@Override
			public boolean onBrowserEvent(CellReference<?> cell, NativeEvent event) {
				boolean checked = event.getEventTarget().<InputElement> cast().isChecked();
				String rowKey = getRowKey((JsonObject) cell.getRow());
				getRpcProxy(CheckBoxRendererServerRpc.class).change(rowKey, checked);
				return true;
			}

		};
	}
}
