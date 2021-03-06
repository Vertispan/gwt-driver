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

import com.vertispan.webdriver.gwt.gwtdriver.ModuleUtilities;

import net.sourceforge.htmlunit.corejs.javascript.ConsString;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * Allows simple invocation of exported methods from GWT. Must follow the same rules as {@link
 * JavascriptExecutor#executeAsyncScript(String, Object...)} in both the Java/Test and
 * Java/Gwt/Client code in terms of arguments passed, expect for primitives, which must be a string
 * (unless returning them from jsni, in which case it should be treated as a long/Long as per
 * executeAsyncScript) from the client, and will be returned as a int/double/ boolean into code.
 */
public class ClientMethodsFactory {
  private static final class InvocationHandlerImplementation implements
      InvocationHandler {
    private final WebDriver driver;
    private final String module;

    private InvocationHandlerImplementation(WebDriver driver, String module) {
      this.driver = driver;
      this.module = module;
    }

    @Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {
      Object ret;
      String key = method.getDeclaringClass().getName() + "::" + method.getName();
      if (module != null) {
        ret = ModuleUtilities.executeExportedFunction(module, key, driver, args);
      } else {
        ret = ModuleUtilities.executeExportedFunction(key, driver, args);
      }
      if (method.getReturnType().isPrimitive() && method.getReturnType() != long.class) {
        // any primitive coming back from gwt will be a string, parse it
        if (method.getReturnType() == int.class) {
          return Integer.parseInt(ret.toString());
        } else if (method.getReturnType() == double.class) {
          return Double.parseDouble(ret.toString());
        } else if (method.getReturnType() == boolean.class) {
          return Boolean.parseBoolean(ret.toString());
        }
      }
      //normalize string, apparently htmlunit gives us junk values from time to time
      if (ret instanceof ConsString) {
        ret = ret.toString();
      }
      return ret;
    }
  }

  /**
   * Creates an instance of the ClientMethods type on the first available module.
   *
   * @param type the ClientMethods type to build
   * @param driver a webdriver that should be used to communicate with the browser
   * @param <T> the type
   * @return an instance of the ClientMethods interface given, wired to the first module in the
   * active browser
   */
  public static <T extends ClientMethods> T create(Class<T> type, WebDriver driver) {
    return create(type, driver, null);
  }

  /**
   * Creates an instance of the ClientMethods type on the given module name.
   *
   * @param type the ClientMethods type to build
   * @param driver a webdriver that should be used to communicate with the browser
   * @param moduleName the name of the GWT module to connect with
   * @param <T> the type
   * @return an instance of the ClientMethods interface given, wired to the named module in the
   * active browser
   */
  public static <T extends ClientMethods> T create(Class<T> type, WebDriver driver,
      String moduleName) {
    assert driver instanceof JavascriptExecutor;
    @SuppressWarnings("unchecked")
    T proxy = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
        new InvocationHandlerImplementation(driver, moduleName));
    return proxy;
  }
}
