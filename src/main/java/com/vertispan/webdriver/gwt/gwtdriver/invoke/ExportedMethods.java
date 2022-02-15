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
package com.vertispan.webdriver.gwt.gwtdriver.invoke;

import org.openqa.selenium.WebElement;

import java.util.List;

public interface ExportedMethods extends ClientMethods {
  boolean isWidget(WebElement elt);

  boolean instanceofwidget(WebElement elt, String type);

  String getContainingWidgetClass(WebElement elt);

  WebElement getContainingWidgetElt(WebElement elt);

  WebElement getContainingWidgetEltOfType(WebElement elt, String type);

//	String getClass(Object obj);

//	String instanceOf(String type, Object instance);

  /**
   * Finds all descendant Widget elements below the context element matching className type.
   * <p>
   * This method will first find the nearest Widget from the context (going up the parent chain
   * until it finds a Widget). From there, it will recursively traverse (breadth-first traversal) of
   * all HasWidget types.
   * <p>
   * Will return empty list if context is null or unable to find a Widget from context element.
   * <p>
   * If nearest Widget to context element is not instanceof {@link com.google.gwt.user.client.ui.HasWidgets},
   * this will return a single item; the Widget element.
   */
  List<WebElement> findDescendantWidgetElementsOfType(WebElement context, String className);

  /**
   * Finds all descendant Widget elements below the context element.
   * <p>
   * This method will first find the nearest Widget from the context (going up the parent chain
   * until it finds a Widget). From there, it will recursively traverse (breadth-first traversal) of
   * all HasWidget types.
   * <p>
   * Will return empty list if context is null or unable to find a Widget from context element.
   * <p>
   * If nearest Widget to context element is not instanceof {@link com.google.gwt.user.client.ui.HasWidgets},
   * this will return a single item; the Widget element.
   */
  List<WebElement> findDescendantWidgetElements(WebElement context);

  /**
   * Finds the first descendant Widget element below the context element matching className type;
   * breadth-first traversal.
   */
  WebElement findFirstDescendantWidgetElementsOfType(WebElement context, String className);
}
