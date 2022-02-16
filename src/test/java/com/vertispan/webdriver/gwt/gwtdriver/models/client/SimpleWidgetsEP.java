/*
 * Copyright 2013 Colin Alworth
 * Copyright 2012-2013 Sencha Labs
 * Copyright 2022 Vertispan LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertispan.webdriver.gwt.gwtdriver.models.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SimpleWidgetsEP implements EntryPoint {

  interface MyUiBinder extends UiBinder<Widget, SimpleWidgetsEP> {
  }

  private MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField
  TextBox textBox;

  @Override
  public void onModuleLoad() {
    // outside of UiBinder
    RootPanel.get().add(new Label("testing"));

    // add UiBinder UI
    RootPanel.get().add(uiBinder.createAndBindUi(this));
  }

  @UiHandler("openDialog")
  void onOpenDialog(ClickEvent event) {
    DialogBox box = new DialogBox();
    box.setText("Heading Text For Dialog");
    box.add(new HTML(textBox.getValue()));
    box.show();
  }
}
