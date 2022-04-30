package es.us.isa.restest.testcases.diversity;

import es.us.isa.restest.testcases.TestCase;

import java.util.List;

/**
 * This function maximizes the globalDiversity of a specific property of several
 * elements. Only one mode is possible:<br>
 *     1.- Diversity among inputs (HTTP requests).<br>
 *
 * globalDiversity is computed as follows: 1) the number of unique pairs
 * operationId-statusCode is counted, and this number is added to globalDiversity;
 * 2) for those pairs whose operationId is the same, diversity (1 - similarity) is
 * computed and added to globalDiversity; 3) globalDiversity is divided by the total
 * number of elements.
 *
 * @author Alberto Martin-Lopez
 */
public class Diversity {

    private SimilarityMeter similarityMeter;
    private boolean normalize; // Diversity measured in [0,1], to avoid bias due to test suite size

    public Diversity(SimilarityMeter.METRIC similarityMetric, boolean normalize) {
        similarityMeter = new SimilarityMeter(similarityMetric);
        this.normalize = normalize;
    }

    /**
     *<p>this one is used in the test file of the project</p>
     */

    public Double evaluate(List<TestCase> testCases) {
        double globalDiversity = 0;
        for (int i=0; i < testCases.size(); i++) {
            TestCase testCase_i = testCases.get(i);
            for (int j=i+1; j < testCases.size(); j++) {
                TestCase testCase_j = testCases.get(j);
                if (testCase_i.getOperationId().equals(testCase_j.getOperationId()))
                    //get the flat representation in String format and add the "distance" it has
                    //if the two case is completely not same or has not same operation id, the globalDiversity will add 1
                    globalDiversity += 1 - similarityMeter.apply(testCase_i, testCase_j);
                else
                    globalDiversity += 1; // Distinct operations count +1 each
            }
        }
        if (normalize)
            globalDiversity /= ((double) (testCases.size() * (testCases.size() - 1)) / 2);

        return globalDiversity;
    }

    /**
     * <p>this one is used in ARTestCaseGenerator.java in the project</p>
     * <p>it finds the max similarity of all the testCases in the list with the new testCase</p>
     * 取最大的similarity，apply返回的值越大的越好，最后返回1-maxSimilarity
     */
    public Double evaluate(List<TestCase> testCases, TestCase testCase) {
        double maxSimilarity = 0;
        for (TestCase testCase_i: testCases) {
            if (testCase_i.getOperationId().equals(testCase.getOperationId()))
                maxSimilarity = Math.max(maxSimilarity, similarityMeter.apply(testCase, testCase_i));
        }
        return 1 - maxSimilarity;
    }

}
