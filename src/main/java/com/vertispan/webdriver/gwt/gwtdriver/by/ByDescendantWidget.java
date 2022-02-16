/*
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

import java.util.List;


/**
 * GWT specific {@code By} implementation that finds descendant widgets.
 * <p>
 * As this {@code By} calls the GWT module itself, it will only locate descendants if the {@link
 * com.google.gwt.user.client.ui.HasWidgets} hierarchy is setup correctly.  For example, if a Widget
 * takes the Element from another Widget, but does not implement HasWidgets, this {@code By} will
 * not locate the other Widget.
 */
public class ByDescendantWidget extends GwtBy {
  private final String type;

  /**
   * Finds the descendant Widgets of any type - anything that extends Widget will be found.
   */
  public ByDescendantWidget() {
    this((WebDriver) null);
  }

  /**
   * Finds the descendant Widgets of any type - anything that extends Widget will be found.
   *
   * @param driver The driver to use to communicate with the browser.
   */
  public ByDescendantWidget(WebDriver driver) {
    this(driver, Widget.class);
  }

  /**
   * Finds the descendant Widgets of the given type.
   * <p>
   * This will find any subtype of that widget, allowing you to pass in {@link
   * com.google.gwt.user.client.ui.ValueBoxBase} and find any {@link com.google.gwt.user.client.ui.TextBox},
   * {@link com.google.gwt.user.client.ui.TextArea}, {@link com.google.gwt.user.client.ui.IntegerBox},
   * etc, as these are all subclasses of {@code ValueBoxBase}.  Note that interfaces cannot be used,
   * only base classes, and those classes *must* extend Widget.
   *
   * @param widgetType The type of widget to find
   */
  public ByDescendantWidget(Class<? extends Widget> widgetType) {
    this(widgetType.getName());
  }

  /**
   * Finds the descendant Widgets of the given type.
   * <p>
   * This will find any subtype of that widget, allowing you to pass in {@link
   * com.google.gwt.user.client.ui.ValueBoxBase} and find any {@link com.google.gwt.user.client.ui.TextBox},
   * {@link com.google.gwt.user.client.ui.TextArea}, {@link com.google.gwt.user.client.ui.IntegerBox},
   * etc, as these are all subclasses of {@code ValueBoxBase}.  Note that interfaces cannot be used,
   * only base classes, and those classes *must* extend Widget.
   *
   * @param driver The driver to use to communicate with the browser.
   * @param widgetType The type of widget to find
   */
  public ByDescendantWidget(WebDriver driver, Class<? extends Widget> widgetType) {
    this(driver, widgetType.getName());
  }

  /**
   * Finds the descendant Widgets of the given type.
   * <p>
   * This will find any subtype of that widget, allowing you to pass in {@link
   * com.google.gwt.user.client.ui.ValueBoxBase} and find any {@link com.google.gwt.user.client.ui.TextBox},
   * {@link com.google.gwt.user.client.ui.TextArea}, {@link com.google.gwt.user.client.ui.IntegerBox},
   * etc, as these are all subclasses of {@code ValueBoxBase}.  Note that interfaces cannot be used,
   * only base classes, and those classes *must* extend Widget.
   *
   * @param widgetClassName The type of widget to find
   */
  public ByDescendantWidget(String widgetClassName) {
    this(null, widgetClassName);
  }

  /**
   * Finds the descendant Widgets of the given type.
   * <p>
   * This will find any subtype of that widget, allowing you to pass in {@link
   * com.google.gwt.user.client.ui.ValueBoxBase} and find any {@link com.google.gwt.user.client.ui.TextBox},
   * {@link com.google.gwt.user.client.ui.TextArea}, {@link com.google.gwt.user.client.ui.IntegerBox},
   * etc, as these are all subclasses of {@code ValueBoxBase}.  Note that interfaces cannot be used,
   * only base classes, and those classes *must* extend Widget.
   *
   * @param driver The driver to use to communicate with the browser.
   * @param widgetClassName The type of widget to find
   */
  public ByDescendantWidget(WebDriver driver, String widgetClassName) {
    super(driver);
    this.type = widgetClassName;
  }


  @Override
  public List<WebElement> findElements(SearchContext context) {
    Require.nonNull("Search Context", context);
    final WebElement contextElem = toWebElement(context);

    ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, getDriver(context));
    // could return empty list
    return m.findDescendantWidgetElementsOfType(contextElem, type);
  }

  @Override
  public WebElement findElement(SearchContext context) {
    Require.nonNull("Search Context", context);
    final WebElement contextElem = toWebElement(context);

    ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, getDriver(context));
    WebElement first = m.findFirstDescendantWidgetElementsOfType(contextElem, type);
    if (first == null) {
      throw new NoSuchElementException("Cannot find widget of type " + type);
    }
    return first;
  }

  @Override
  public String toString() {
    return "ByDescendantWidget{" +
        "type='" + type + '\'' +
        '}';
  }
}
