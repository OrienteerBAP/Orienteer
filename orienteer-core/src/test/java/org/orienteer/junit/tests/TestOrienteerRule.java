package org.orienteer.junit.tests;

import org.junit.Rule;
import org.orienteer.junit.OrienteerRule;

public class TestOrienteerRule extends AbstractTestInjection
{
	@Rule
	public OrienteerRule rule = new OrienteerRule();
}
