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
package com.vertispan.webdriver.gwt.gwtdriver.models;

import com.google.gwt.user.client.ui.Label;

import com.vertispan.webdriver.gwt.gwtdriver.by.ByNearestWidget;
import com.vertispan.webdriver.gwt.gwtdriver.by.FasterByChained;
import com.vertispan.webdriver.gwt.gwtdriver.models.GwtLabel.GwtLabelFinder;
import com.vertispan.webdriver.gwt.gwtdriver.models.GwtWidget.ForWidget;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@ForWidget(Label.class)
public class GwtLabel extends GwtWidget<GwtLabelFinder> {

  public GwtLabel(WebDriver driver, WebElement element) {
    super(driver, element);
  }

  public String getText() {
    return getElement().getText();
  }

  public static class GwtLabelFinder extends GwtWidgetFinder<GwtLabel> {
    String text;

    public GwtLabelFinder withText(String text) {
      this.text = text;
      return this;
    }

    @Override
    public GwtLabel done() {
      WebElement elt = this.elt;
      if (text != null) {
        elt = elt.findElement(
            new FasterByChained(By.xpath(".//*[contains(text(), " + escapeToString(text) + ")]"),
                new ByNearestWidget(driver, Label.class)));
      }
      return new GwtLabel(driver, elt);
    }
  }

}
