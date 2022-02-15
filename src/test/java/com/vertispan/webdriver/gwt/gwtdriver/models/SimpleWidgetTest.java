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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
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

    WidgetContainer widget = new GwtRootPanel(driver);
    assert widget.as(GwtRootPanel.class) != null;

    List<GwtWidget<?>> children = widget.findWidgets(By.xpath(".//*"));
    //RootPanel
    //*Label
    //*FlowPanel
    //**TextBox
    //**Button
    assert children.size() == 5 : children.size();

    //find Label by iterating through sub-widgets and as'ing
    GwtLabel label = children.get(0).as(GwtLabel.class);
    assert label != null;
    assert label.getText().equals("testing") : label.getText();

    //find label by finder
    GwtLabel label2 = widget.find(GwtLabel.class).withText("testing").done();
    assert label2 != null;
    assert label.getElement().equals(label2.getElement());
    assert label.getText().equals("testing");

    //find, as TextBox as input, verify text and enter new
    Input textBox = children.get(2).as(Input.class);
    assert "asdf".equals(textBox.getValue());
    textBox.sendKeys("fdsa");

    //find, click button
    GwtWidget.find(Button.class, driver).withText("Open dialog").done().click();

    //find dialog by heading
    Dialog headingDialog = new DialogFinder().withHeading("Heading").withDriver(driver).done();
    assert headingDialog != null;
    assert headingDialog.getHeadingText().equals("Heading Text For Dialog");
    //find dialog by top
    Dialog topDialog = new DialogFinder().atTop().withDriver(driver).done();
    assert topDialog != null;
    assert topDialog.getHeadingText().equals("Heading Text For Dialog");

    assert headingDialog.getElement().equals(topDialog.getElement());

    Point initialHeaderLoc = topDialog.getElement().getLocation();

    Actions actions = new Actions(driver);
    actions.dragAndDrop(topDialog.getHeaderElement(), children.get(3).getElement());
    actions.build().perform();
    Point movedHeaderLoc = topDialog.getElement().getLocation();

    assert !movedHeaderLoc.equals(initialHeaderLoc);
    //this line is a little screwy in htmlunit
//			assert movedHeaderLoc.equals(children.get(3).getElement().getLocation());

    assert topDialog.getElement().getText().contains("fdsa");
  }


  @Test
  void testFindDescedantWidgets() {
    driver.get(url);

    WidgetContainer rootPanel = new GwtRootPanel(driver);
    assert rootPanel.as(GwtRootPanel.class) != null;

    ExportedMethods exportedMethods = ClientMethodsFactory.create(ExportedMethods.class, driver);

    // find all widgets under a context
    List<WebElement> allWidgetElements = exportedMethods
        .findDescendantWidgetElements(rootPanel.getElement());

    // should be 6 (including root panel)
    assertEquals(6, allWidgetElements.size());
    exportedMethods.instanceofwidget(allWidgetElements.get(1), Label.class.getName());
    exportedMethods.instanceofwidget(allWidgetElements.get(2), Panel.class.getName());
    exportedMethods.instanceofwidget(allWidgetElements.get(3), TextBox.class.getName());
    exportedMethods.instanceofwidget(allWidgetElements.get(4),
        com.google.gwt.user.client.ui.Button.class.getName());
    exportedMethods.instanceofwidget(allWidgetElements.get(5), Label.class.getName());

    // find descendant of type
    List<WebElement> buttons = exportedMethods.findDescendantWidgetElementsOfType(
        rootPanel.getElement(),
        com.google.gwt.user.client.ui.Button.class.getName());
    assertEquals(1, buttons.size());

    // find first widget type
    WebElement firstLabel = exportedMethods.findFirstDescendantWidgetElementsOfType(
        rootPanel.getElement(), Label.class.getName());
    assertEquals("testing", new GwtLabel(driver, firstLabel).getText());
  }
}
