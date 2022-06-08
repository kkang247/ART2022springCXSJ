package es.us.isa.restest.testcases;

import es.us.isa.restest.testcases.coverage.SimpleDiffMeter;
import io.swagger.v3.oas.models.PathItem;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestCaseTest {

    @Test
    public void getFlatRepresentationQueryHeaderBodyTest() {
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

        assertEquals("The flat representation of the test case is wrong", "POST/booksapplication/jsonq1val1q2val2h1valF1h2valF2example body", tc1.getFlatRepresentation());
        assertEquals("The flat representation of the test case is wrong", "POST/booksapplication/jsonq1val1q2val2h1valF1h2valF2example body", tc2.getFlatRepresentation());
        assertEquals("The two test cases should have the same flat representation", tc1.getFlatRepresentation(), tc2.getFlatRepresentation());
    }

    @Test
    public void getFlatRepresentationPathFormTest() {
        TestCase tc1 = new TestCase("abc", true, "updatePage", "/books/{bookId}/{page}", PathItem.HttpMethod.PUT);
        tc1.setFaultyReason("inter_parameter_dependency");
        tc1.addFormParameter("q2", "val2");
        tc1.addPathParameter("bookId", "valF1");
        tc1.addFormParameter("q1", "val1");
        tc1.addPathParameter("page", "valF2");

        TestCase tc2 = new TestCase("def", false, "updatePage", "/books/{bookId}/{page}", PathItem.HttpMethod.PUT);
        tc2.setFaultyReason("none");
        tc2.addFormParameter("q1", "val1");
        tc2.addPathParameter("page", "valF2");
        tc2.addFormParameter("q2", "val2");
        tc2.addPathParameter("bookId", "valF1");

        assertEquals("The flat representation of the test case is wrong", "PUT/books/valF1/valF2application/x-www-form-urlencodedq1val1q2val2", tc1.getFlatRepresentation());
        assertEquals("The flat representation of the test case is wrong", "PUT/books/valF1/valF2application/x-www-form-urlencodedq1val1q2val2", tc2.getFlatRepresentation());
        assertEquals("The two test cases should have the same flat representation", tc1.getFlatRepresentation(), tc2.getFlatRepresentation());
    }

    @Test
    public void testGetDiff(){
        List<List<String>> coverage = new ArrayList<>();
        coverage.add(new ArrayList<>());
        coverage.add(new ArrayList<>());
        coverage.get(0).add("1010");
        coverage.get(0).add("");
        coverage.get(0).add("111");
        coverage.get(1).add("");
        coverage.get(1).add("1100");
        coverage.get(1).add("111");
        coverage.get(1).add("");
        TestCoverage tc1 = new TestCoverage("tc1", coverage);
        List<List<String>> coverage1 = new ArrayList<>();
        for (int i = 0; i < coverage.size(); i++) {
            coverage1.add(new ArrayList<>());
            for (int j = 0; j < coverage.get(i).size(); j++) {
                coverage1.get(i).add(coverage.get(i).get(j));
            }
        }
        coverage1.get(0).set(0,"1111");
        coverage1.get(1).set(1,"0011");
        coverage1.get(0).set(1,"10101");
        coverage1.get(1).set(2, "");
        assertEquals(12, tc1.diff(coverage1));
    }

    @Test
    public void testPutTogether(){
        List<List<String>> coverage = new ArrayList<>();
        coverage.add(new ArrayList<>());
        coverage.add(new ArrayList<>());
        coverage.get(0).add("1010");
        coverage.get(0).add("");
        coverage.get(0).add("111");
        coverage.get(1).add("");
        coverage.get(1).add("1100");
        coverage.get(1).add("111");
        coverage.get(1).add("");
        TestCoverage tc1 = new TestCoverage("tc1", coverage);
        List<List<String>> coverage1 = new ArrayList<>();
        for (int i = 0; i < coverage.size(); i++) {
            coverage1.add(new ArrayList<>());
            for (int j = 0; j < coverage.get(i).size(); j++) {
                coverage1.get(i).add(coverage.get(i).get(j));
            }
        }
        coverage1.get(0).set(0,"1111");
        coverage1.get(1).set(1,"0011");
        coverage1.get(0).set(1,"10101");
        coverage1.get(1).set(2, "");
        System.out.println(tc1.diff(coverage1));
        System.out.println("before put together coverage is");
        for (List<String> strings : coverage) {
            for (String string : strings) {
                System.out.print("\"" + string + "\" , ");
            }
            System.out.println();
        }
        System.out.println("before put together coverage1 is");
        for (List<String> strings : coverage1) {
            for (String string : strings) {
                System.out.print("\"" + string + "\" , ");
            }
            System.out.println();
        }
        ReadAndEvaluate.putTogether(coverage1, coverage);
        System.out.println("after put together coverage is");
        for (List<String> strings : coverage1) {
            for (String string : strings) {
                System.out.print("\"" + string + "\" , ");
            }
            System.out.println();
        }
        System.out.println(tc1.diff(coverage1));

    }

    @Test
    public void testGenerate() throws IOException {
        List<List<String>> cov1 = new ArrayList<>();
        cov1.add(new ArrayList<>());
        cov1.get(0).add("1010");
        cov1.get(0).add("");
        cov1.get(0).add("111");
        cov1.get(0).add("10001");
        cov1.add(new ArrayList<>());
        cov1.get(1).add("");
        cov1.get(1).add("");
        TestCoverage tc1 = new TestCoverage("tc1", cov1);

        List<List<String>> cov2 = new ArrayList<>();
        cov2.add(new ArrayList<>());
        cov2.get(0).add("");
        cov2.get(0).add("11111");
        cov2.get(0).add("");
        cov2.get(0).add("10111");
        cov2.add(new ArrayList<>());
        cov2.get(1).add("");
        cov2.get(1).add("");
        TestCoverage tc2 = new TestCoverage("tc2", cov2);

        List<List<String>> cov3 = new ArrayList<>();
        cov3.add(new ArrayList<>());
        cov3.get(0).add("0001");
        cov3.get(0).add("10001");
        cov3.get(0).add("");
        cov3.get(0).add("10000");
        cov3.add(new ArrayList<>());
        cov3.get(1).add("101");
        cov3.get(1).add("");
        TestCoverage tc3 = new TestCoverage("tc3", cov3);

        List<List<String>> cov4 = new ArrayList<>();
        cov4.add(new ArrayList<>());
        cov4.get(0).add("");
        cov4.get(0).add("");
        cov4.get(0).add("");
        cov4.get(0).add("");
        cov4.add(new ArrayList<>());
        cov4.get(1).add("");
        cov4.get(1).add("1");
        TestCoverage tc4 = new TestCoverage("tc4", cov4);

        List<TestCoverage> testCoverages = new ArrayList<>();
        testCoverages.add(tc1);
        testCoverages.add(tc2);
        testCoverages.add(tc3);
        testCoverages.add(tc4);

        ReadAndEvaluate.setTestCaseList(testCoverages);

        List<TestCase> result = (List<TestCase>) ReadAndEvaluate.generateCoverageTestCaseCollection();

    }

}
