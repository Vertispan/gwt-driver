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

  public static CheatingByChained cheatingChained(By... bys) {
    return new CheatingByChained(bys);
  }

  public static FasterByChained fasterChained(By... bys) {
    return new FasterByChained(bys);
  }

  // is widget

  public static ByWidget isWidget() {
    return new ByWidget();
  }

  public static ByWidget isWidget(Class<? extends Widget> widgetType) {
    return new ByWidget(widgetType);
  }

  public static ByWidget isWidget(String widgetClassName) {
    return new ByWidget(widgetClassName);
  }

  // nearest widget

  public static ByNearestWidget nearestWidget() {
    return new ByNearestWidget();
  }

  public static ByNearestWidget nearestWidget(Class<? extends Widget> widgetType) {
    return new ByNearestWidget(widgetType);
  }

  public static ByNearestWidget nearestWidget(String widgetClassName) {
    return new ByNearestWidget(widgetClassName);
  }

  // descendant widgets
  public static ByDescendantWidget descendantWidget() {
    return new ByDescendantWidget();
  }

  public static ByDescendantWidget descendantWidget(Class<? extends Widget> widgetType) {
    return new ByDescendantWidget(widgetType);
  }

  public static ByDescendantWidget descendantWidget(String widgetClassName) {
    return new ByDescendantWidget(widgetClassName);
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
