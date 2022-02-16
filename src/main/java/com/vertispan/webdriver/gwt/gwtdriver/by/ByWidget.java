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

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Require;

import java.util.Collections;
import java.util.List;

/**
 * GWT-specific {@code By} implementation that identifies if the current search context is a Widget.
 * Use in conjunction with other {@code By} statements to look for widgets that match a certain
 * criteria.
 * <p>
 * This implementation will only look at the current context and will not search surrounding
 * elements.
 */
public class ByWidget extends GwtBy {
  private final String type;

  /**
   * Checks if context is a widget of any type - anything that extends Widget will be found.
   */
  public ByWidget() {
    this((WebDriver) null);
  }

  /**
   * Checks if context is a widget of any type - anything that extends Widget will be found.
   *
   * @param driver The driver to use to communicate with the browser.
   */
  public ByWidget(WebDriver driver) {
    this(driver, Widget.class);
  }

  /**
   * Checks if context is a widget of the given type.
   *
   * @param widgetType The type of widget to validate against
   */
  public ByWidget(Class<? extends Widget> widgetType) {
    this(widgetType.getName());
  }

  /**
   * Checks if context is a widget of the given type.
   *
   * @param driver The driver to use to communicate with the browser.
   * @param widgetType The type of widget to validate against
   */
  public ByWidget(WebDriver driver, Class<? extends Widget> widgetType) {
    this(driver, widgetType.getName());
  }

  /**
   * Checks if context is a widget of the given type.
   *
   * @param widgetClassName The type of widget to validate against
   */
  public ByWidget(String widgetClassName) {
    this(null, widgetClassName);
  }

  /**
   * Checks if context is a widget of the given type.
   *
   * @param driver The driver to use to communicate with the browser.
   * @param widgetClassName The type of widget to validate against
   */
  public ByWidget(WebDriver driver, String widgetClassName) {
    super(driver);
    this.type = widgetClassName;
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    Require.nonNull("Search Context", context);
    WebElement contextElem = toWebElement(context);

    ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, getDriver(context));
    if (m.instanceofwidget(contextElem, type)) {
      return Collections.singletonList(contextElem);
    }

    return Collections.emptyList();
  }

  @Override
  public WebElement findElement(SearchContext context) {
    Require.nonNull("Search Context", context);
    final WebElement contextElement = toWebElement(context);

    ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, getDriver(context));
    if (m.instanceofwidget(contextElement, type)) {
      return contextElement;
    }

    throw new NoSuchElementException("Can't find widget of type " + type);
  }

  @Override
  public String toString() {
    return "isWidget(" + type + ")";
  }
}