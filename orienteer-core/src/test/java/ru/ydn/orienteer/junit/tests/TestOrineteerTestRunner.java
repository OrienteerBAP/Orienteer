package ru.ydn.orienteer.junit.tests;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ydn.orienteer.junit.OrienteerTestRunner;
import ru.ydn.orienteer.junit.OrienteerTester;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.junit.Assert.*;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestOrineteerTestRunner extends AbstractTestInjection
{
	
}
