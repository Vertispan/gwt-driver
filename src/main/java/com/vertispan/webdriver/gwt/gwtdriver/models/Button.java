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

import com.google.gwt.user.client.ui.ButtonBase;

import com.vertispan.webdriver.gwt.gwtdriver.by.ByNearestWidget;
import com.vertispan.webdriver.gwt.gwtdriver.models.Button.ButtonFinder;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

public class Button extends GwtWidget<ButtonFinder> {

  public Button(WebDriver driver, WebElement element) {
    super(driver, element);
  }

  public void click() {
    getElement().click();
  }

  public static class ButtonFinder extends GwtWidgetFinder<Button> {
    private String text;

    public ButtonFinder withText(String text) {
      this.text = text;
      return this;
    }

    @Override
    public Button done() {
      WebElement element = elt;
      if (text != null) {
        String escaped = escapeToString(text);
        element = elt.findElement(new ByChained(
            By.xpath(".//*[contains(text(), " + escaped + ")]"),
            new ByNearestWidget(driver, ButtonBase.class)));
      }

      return new Button(driver, element);
    }
  }
}
