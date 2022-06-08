package es.us.isa.restest.util;

import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.testcases.TestCoverage;
import io.swagger.v3.oas.models.PathItem;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReadWriteJSONTest {
    @Test
    public void testWriteAndRead() throws FileNotFoundException {
        TestCase tc1 = new TestCase("abc", true, "createBook", "/books", PathItem.HttpMethod.POST);
        tc1.setFaultyReason("inter_parameter_dependency");
        tc1.setBodyParameter("example body");
        tc1.addQueryParameter("q2", "val2");
        tc1.addHeaderParameter("h1", "valF1");
        tc1.addQueryParameter("q1", "val1");
        tc1.addHeaderParameter("h2", "valF2");

        TestCase tc2 = new TestCase("def", false, "createBook", "/books", PathItem.HttpMethod.POST);
        tc2.setFaultyReason("none");
        tc2.setBodyParameter("example body");
        tc2.addQueryParameter("q1", "val1");
        tc2.addHeaderParameter("h2", "valF2");
        tc2.addQueryParameter("q2", "val2");
        tc2.addHeaderParameter("h1", "valF1");

        List<TestCase> testCaseList = new ArrayList<>();
        testCaseList.add(tc1);
        testCaseList.add(tc2);
        JSONManager.saveAsJson(testCaseList);
        JSONManager.readTestCase();
    }
}
