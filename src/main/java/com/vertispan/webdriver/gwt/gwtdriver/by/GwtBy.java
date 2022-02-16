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

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;

/**
 *
 */
public abstract class GwtBy extends By {

  /**
   * When looking for only one item, cheats by only looking for one item at each level.
   * <p>
   * Useful only if you want the first result of each {@link By} operation, otherwise use {@link
   * #fasterChained(By...)} or {@link org.openqa.selenium.support.pagefactory.ByChained}.
   */
  public static CheatingByChained cheatingChained(By... bys) {
    return new CheatingByChained(bys);
  }

  /**
   * When looking for only one item, speeds up the last {@link By} in the chain by only running it
   * until it finds something.
   * <p>
   * When using   {@link SearchContext#findElements(By)}, uses {@link
   * org.openqa.selenium.support.pagefactory.ByChained} to find all possible elements as normal.
   * <p>
   * Using this should be functionally equivalent to using {@link org.openqa.selenium.support.pagefactory.ByChained},
   * except faster in some cases.
   */
  public static FasterByChained fasterChained(By... bys) {
    return new FasterByChained(bys);
  }

  // ----------
  // is widget
  // ----------

  /**
   * Checks if context is a widget of any type - anything that extends Widget.
   */
  public static ByWidget isWidget() {
    return new ByWidget();
  }

  /**
   * Checks if context is a widget of the specified widgetType.
   */
  public static ByWidget isWidget(Class<? extends Widget> widgetType) {
    return new ByWidget(widgetType);
  }

  /**
   * Checks if context is a widget of the specified widgetType.
   */
  public static ByWidget isWidget(String widgetClassName) {
    return new ByWidget(widgetClassName);
  }

  // ---------------
  // nearest widget
  // ---------------

  /**
   * Looks at the current search context and above for the nearest Widget object.
   */
  public static ByNearestWidget nearestWidget() {
    return new ByNearestWidget();
  }

  /**
   * Looks at the current search context and above for the nearest Widget object of provided
   * widgetType.
   */
  public static ByNearestWidget nearestWidget(Class<? extends Widget> widgetType) {
    return new ByNearestWidget(widgetType);
  }

  /**
   * Looks at the current search context and above for the nearest Widget object of provided
   * widgetType.
   */
  public static ByNearestWidget nearestWidget(String widgetClassName) {
    return new ByNearestWidget(widgetClassName);
  }

  // ------------------
  // descendant widgets
  // ------------------


  /**
   * Finds the descendant Widgets of any type - anything that extends Widget will be found.
   */
  public static ByDescendantWidget descendantWidget() {
    return new ByDescendantWidget();
  }

  /**
   * Finds the descendant Widgets of the given type.
   */
  public static ByDescendantWidget descendantWidget(Class<? extends Widget> widgetType) {
    return new ByDescendantWidget(widgetType);
  }

  /**
   * Finds the descendant Widgets of the given type.
   */
  public static ByDescendantWidget descendantWidget(String widgetClassName) {
    return new ByDescendantWidget(widgetClassName);
  }

  // ------------------
  // get children
  // ------------------

  /**
   * Gets the children Widgets of any type - any child that extends Widget will be found.
   */
  public static ByWidgetChildren childrenWidgets() {
    return new ByWidgetChildren();
  }

  /**
   * Gets the children Widgets of the given type.
   */
  public static ByWidgetChildren childrenWidgets(Class<? extends Widget> widgetType) {
    return new ByWidgetChildren(widgetType);
  }

  /**
   * Gets the children Widgets of the given type.
   */
  public static ByWidgetChildren childrenWidgets(String widgetClassName) {
    return new ByWidgetChildren(widgetClassName);
  }


  private final WebDriver driver;

  protected GwtBy() {
    this(null);
  }

  protected GwtBy(WebDriver driver) {
    this.driver = driver;
  }


  protected WebElement toWebElement(SearchContext context) {
    final WebElement contextElem;
    if (context instanceof WebElement) {
      contextElem = (WebElement) context;
    } else {
      // most likely the driver
      contextElem = context.findElement(By.xpath("//body"));
    }
    return contextElem;
  }

  protected WebDriver getDriver(SearchContext context) {
    if (this.driver != null) {
      return this.driver;
    }

    if (context instanceof WebDriver) {
      return (WebDriver) context;
    }

    if (context instanceof WrapsDriver) {
      return ((WrapsDriver) context).getWrappedDriver();
    }

    throw new IllegalArgumentException("Unable to get WebDriver from provided context.  "
        + "Maybe use the constructor that takes a driver.");
  }
}
