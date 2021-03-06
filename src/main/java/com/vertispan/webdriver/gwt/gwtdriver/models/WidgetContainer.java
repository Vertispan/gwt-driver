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

import com.google.gwt.user.client.ui.HasWidgets;

import com.vertispan.webdriver.gwt.gwtdriver.by.ByWidget;
import com.vertispan.webdriver.gwt.gwtdriver.by.CheatingByChained;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A Widget that can contain more widgets. A GWT widget type need not implement or extend anything
 * to be mapped to this, but likely implements {@link HasWidgets}.
 * <p>
 * The main way to use this type is to invoke {@link #findWidget(By)} and {@link #findWidgets(By)}.
 * These methods are like WebElement's findElement and findElements - they accept a By, but instead
 * of returning one or more WebElement instances, they return GwtWidget. These objects can be
 * transformed into specific subclasses via the {@link #as(Class)} method.
 */
public class WidgetContainer extends GwtWidget<GwtWidgetFinder<WidgetContainer>> {

  public WidgetContainer(WebDriver driver, WebElement element) {
    super(driver, element);
  }

  /**
   * Convenience method to find child/descendant widgets and wrap them. Same semantics as {@link
   * WebElement#findElements(By)} - can be used to find if nothing matches this By.
   *
   * @param by how to find the widgets
   * @return the widgets found that match the by query
   */
  public List<GwtWidget<?>> findWidgets(By by) {
    LinkedHashSet<WebElement> eltsSet = new LinkedHashSet<WebElement>(
        getElement().findElements(new ByChained(by, new ByWidget(getDriver()))));
    List<GwtWidget<?>> children = new ArrayList<GwtWidget<?>>();
    for (WebElement elt : eltsSet) {
      children.add(new GwtWidget<GwtWidgetFinder<?>>(getDriver(), elt));
    }
    return children;
  }

  /**
   * Convenience method to find a specific child/descendant widgets and wrap it. Same semantics as
   * {@link WebElement#findElement(By)} - will throw an exception if nothing matches.
   *
   * @param by how to find the widget
   * @return a matching widget found that matches
   */
  public GwtWidget<?> findWidget(By by) throws NoSuchElementException {
    WebElement elt = getElement().findElement(new CheatingByChained(by, new ByWidget(getDriver())));
    return new GwtWidget<GwtWidgetFinder<?>>(getDriver(), elt);
  }

}
