package com.colinalworth.gwtdriver.by;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

/**
 * Speeds up the last By in the chain by only running it until it finds something.
 * 
 * @author colin
 *
 */
public class FasterByChained extends By {
	private By[] bys;
	public FasterByChained(By... bys) {
		this.bys = bys;
	}
	@Override
	public List<WebElement> findElements(SearchContext context) {
		return new ByChained(bys).findElements(context);
	}
	@Override
	public WebElement findElement(SearchContext context) {
		By[] firstBys = new By[bys.length - 1];
		System.arraycopy(bys, 0, firstBys, 0, firstBys.length);
		List<WebElement> elts = new ByChained(firstBys).findElements(context);
		if (elts == null) { 
			throw new NoSuchElementException("Cannot locate element using " + this);
		}
		for (WebElement elt : elts) {
			try {
				return elt.findElement(bys[bys.length - 1]);
			} catch (NoSuchElementException ex) {
				continue;
			}
		}
		throw new NoSuchElementException("Cannot locate element using " + this);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("FasterByChained(");
		stringBuilder.append("{");

		boolean first = true;
		for (By by : bys) {
			stringBuilder.append((first ? "" : ",")).append(by);
			first = false;
		}
		stringBuilder.append("})");
		return stringBuilder.toString();
	}
}