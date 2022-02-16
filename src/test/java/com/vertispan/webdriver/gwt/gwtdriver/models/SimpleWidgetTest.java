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

import static org.junit.jupiter.api.Assertions.*;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.vertispan.webdriver.gwt.gwtdriver.invoke.ClientMethodsFactory;
import com.vertispan.webdriver.gwt.gwtdriver.invoke.ExportedMethods;
import com.vertispan.webdriver.gwt.gwtdriver.models.Dialog.DialogFinder;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Simple initial tests to make sure the basics pass a smoke test, so lots of manual setup
 */
public class SimpleWidgetTest {

  // a bit dumb as this will have to be updated every time the test UI changes
  private static final int TOTAL_WIDGET_COUNT = 13;

  public static class SmokeTestWidget {
    @Child(type = RootPanel.class)
    private GwtWidget widget;
  }

  private static Server server;
  private static String url;


  @BeforeAll
  static void beforeAll() throws Exception {
    WebDriverManager.chromedriver().setup();

    server = new Server(0);
    ResourceHandler handler = new ResourceHandler();
    handler.setResourceBase("target/www");
    handler.setDirectoriesListed(true);
    server.setHandler(handler);

    server.start();

    //get URL
    NetworkConnector connector = (NetworkConnector) server.getConnectors()[0];
    url = "http://localhost:" + connector.getLocalPort() + "/index.html";
    System.out.println(url);
  }

  @AfterAll
  static void afterAll() throws Exception {
    if (server != null && server.isRunning()) {
      server.stop();
    }
  }

  private ChromeDriver driver;

  @BeforeEach
  void setup() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    driver = new ChromeDriver(options);
  }


  @Test
  public void testSmokeTestWidget() {
    SmokeTestWidget test = new SmokeTestWidget();
  }

  @Test
  public void testWithDriver() throws Exception {
    driver.get(url);

    WidgetContainer rootPanel = new GwtRootPanel(driver);
    assert rootPanel.as(GwtRootPanel.class) != null;

    List<GwtWidget<?>> children = rootPanel.findWidgets(By.xpath(".//*"));
    //RootPanel
    //*Label
    //*UIBINDER Internals

    // growing tests will be difficult to narrow down an exact count without having to continually
    // update this value.
    assertEquals(TOTAL_WIDGET_COUNT, children.size());

    //find Label by iterating through sub-widgets and as'ing
    GwtLabel label = children.get(0).as(GwtLabel.class);
    assertNotNull(label);
    assertEquals("testing", label.getText());

    //find label by finder
    GwtLabel label2 = rootPanel.find(GwtLabel.class).withText("testing").done();
    assertNotNull(label2);
    assertEquals(label.getElement(), label2.getElement());
    assertEquals("testing", label2.getText());

    // find panel1 that contains form, button for dialog
    WidgetContainer panel1 = rootPanel.findWidget(By.cssSelector(".panel1"))
        .as(WidgetContainer.class);
    List<GwtWidget<?>> panel1Children = panel1.findWidgets(By.xpath(".//*"));

    //find, as TextBox as input, verify text and enter new
    Input textBox = panel1Children.get(0).as(Input.class);
    assertEquals("asdf", textBox.getValue());
    textBox.sendKeys("fdsa");

    //find, click button
    GwtWidget.find(Button.class, driver).withText("Open dialog").done().click();

    //find dialog by heading
    Dialog headingDialog = new DialogFinder().withHeading("Heading").withDriver(driver).done();
    assertNotNull(headingDialog);
    assertEquals("Heading Text For Dialog", headingDialog.getHeadingText());

    //find dialog by top
    Dialog topDialog = new DialogFinder().atTop().withDriver(driver).done();
    assertNotNull(topDialog);
    assertEquals("Heading Text For Dialog", topDialog.getHeadingText());

    assertEquals(topDialog.getElement(), headingDialog.getElement());

    Point initialHeaderLoc = topDialog.getElement().getLocation();

    Actions actions = new Actions(driver);
    actions.dragAndDrop(topDialog.getHeaderElement(), children.get(3).getElement());
    actions.build().perform();
    Point movedHeaderLoc = topDialog.getElement().getLocation();

    assertNotEquals(initialHeaderLoc, movedHeaderLoc);
    assertTrue(topDialog.getElement().getText().contains("fdsa"));
  }


  @Test
  void testFindDescedantWidgets() {
    driver.get(url);

    WidgetContainer rootPanel = new GwtRootPanel(driver);
    assertNotNull(rootPanel.as(GwtRootPanel.class));

    ExportedMethods exportedMethods = ClientMethodsFactory.create(ExportedMethods.class, driver);

    // let's get the panel1 and only look under it
    WidgetContainer panel1 = rootPanel.findWidget(By.cssSelector(".panel1"))
        .as(WidgetContainer.class);

    // find all widgets under a context
    List<WebElement> allWidgetElements = exportedMethods
        .findDescendantWidgetElements(panel1.getElement());

    // should be TOTAL_WIDGET_COUNT (excludes the parent panel)
    assertEquals(3, allWidgetElements.size());
    exportedMethods.instanceofwidget(allWidgetElements.get(0), TextBox.class.getName());
    exportedMethods.instanceofwidget(allWidgetElements.get(1),
        com.google.gwt.user.client.ui.Button.class.getName());
    exportedMethods.instanceofwidget(allWidgetElements.get(2), Label.class.getName());

    // find descendant of type
    List<WebElement> buttons = exportedMethods.findDescendantWidgetElementsOfType(
        panel1.getElement(),
        com.google.gwt.user.client.ui.Button.class.getName());
    assertEquals(1, buttons.size());

    // find first widget type; using root panel as the context as we know the first
    // widget is the label we're expecting
    WebElement firstLabel = exportedMethods.findFirstDescendantWidgetElementsOfType(
        rootPanel.getElement(), Label.class.getName());
    assertEquals("testing", new GwtLabel(driver, firstLabel).getText());


    // let's validate direct children fetch; only looking in panel2
    WidgetContainer panel2 = rootPanel.findWidget(By.cssSelector(".panel2"))
        .as(WidgetContainer.class);
    List<WebElement> panel2Children = exportedMethods.getChildren(panel2.getElement());
    assertEquals(3, panel2Children.size());
    exportedMethods.instanceofwidget(panel2Children.get(0), Label.class.getName());
    exportedMethods.instanceofwidget(panel2Children.get(1), Button.class.getName());
    exportedMethods.instanceofwidget(panel2Children.get(2), FlowPanel.class.getName());
  }
}
