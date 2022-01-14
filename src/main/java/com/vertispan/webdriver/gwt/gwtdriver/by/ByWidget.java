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
package com.vertispan.webdriver.gwt.gwtdriver.by;

import com.google.gwt.user.client.ui.Widget;

import com.vertispan.webdriver.gwt.gwtdriver.invoke.ClientMethodsFactory;
import com.vertispan.webdriver.gwt.gwtdriver.invoke.ExportedMethods;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * GWT-specific {@code By} implementation that looks for widgets that in the current search context.
 * Use in conjunction with other {@code By} statements to look for widgets that match a certain
 * criteria.
 */
public class ByWidget extends By {
  private final WebDriver driver;
  private final String type;

  public ByWidget(WebDriver driver) {
    this(driver, Widget.class);
  }

  public ByWidget(WebDriver driver, Class<? extends Widget> widgetType) {
    this(driver, widgetType.getName());
  }

  public ByWidget(WebDriver driver, String className) {
    this.driver = driver;
    this.type = className;
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    List<WebElement> elts = context.findElements(By.xpath("."));

    List<WebElement> ret = new ArrayList<WebElement>();
    ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
    for (WebElement elt : elts) {
      if (m.instanceofwidget(elt, type)) {
        ret.add(elt);
      }
    }

    return ret;
  }

  @Override
  public WebElement findElement(SearchContext context) {
    List<WebElement> elts = context.findElements(By.xpath("."));

    ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
    for (WebElement elt : elts) {
      if (m.instanceofwidget(elt, type)) {
        return elt;
      }
    }
    throw new NoSuchElementException("Can't find widget of type " + type);
  }

  @Override
  public String toString() {
    return "isWidget(" + type + ")";
  }
}