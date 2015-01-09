package ru.ydn.orienteer.junit.tests;

import org.junit.Rule;

import ru.ydn.orienteer.junit.OrienteerRule;

public class TestOrienteerRule extends AbstractTestInjection
{
	@Rule
	public OrienteerRule rule = new OrienteerRule();
}
