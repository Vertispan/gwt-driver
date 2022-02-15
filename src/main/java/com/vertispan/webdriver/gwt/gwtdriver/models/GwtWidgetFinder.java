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

import com.google.common.base.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.function.Supplier;

public class GwtWidgetFinder<W extends GwtWidget<?>> {
  protected WebDriver driver;
  protected WebElement elt;

  public GwtWidgetFinder<W> withDriver(WebDriver driver) {
    this.driver = driver;
    if (elt == null) {
      elt = driver.findElement(By.tagName("body"));
    }
    return this;
  }

  public GwtWidgetFinder<W> withElement(WebElement element) {
    if (driver == null && elt instanceof WrapsDriver) {
      driver = ((WrapsDriver) elt).getWrappedDriver();
    }
    this.elt = element;
    return this;
  }

  @SuppressWarnings("unchecked")
  public W done() {
    assert getClass()
        == GwtWidgetFinder.class : "GwtWidgetFinder.done() must be overridden in all subclasses";
    return (W) new GwtWidget<GwtWidgetFinder<?>>(driver, elt);
  }

  public W waitFor() {
    return waitFor((Supplier<String>) null);
  }

  public W waitFor(String message) {
    return waitFor(message == null ? null : () -> message);
  }

  public W waitFor(Supplier<String> messageSupplier) {
    return waitFor(Duration.ofSeconds(10), messageSupplier);
  }

  public W waitFor(long duration, TemporalUnit unit) {
    return waitFor(Duration.of(duration, unit));
  }

  public W waitFor(Duration duration) {
    return waitFor(duration, (Supplier<String>) null);
  }

  public W waitFor(Duration duration, String message) {
    return waitFor(duration, message == null ? null : () -> message);
  }

  public W waitFor(Duration duration, Supplier<String> messageSupplier) {
    return new FluentWait<>(driver)
        .withTimeout(duration)
        .withMessage(messageSupplier)
        .ignoring(NotFoundException.class)
        .until((Function<WebDriver, W>) webDriver -> done());
  }
}
