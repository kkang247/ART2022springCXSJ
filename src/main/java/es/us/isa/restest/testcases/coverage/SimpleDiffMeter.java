package es.us.isa.restest.testcases.coverage;

import es.us.isa.restest.testcases.ReadAndEvaluate;
import es.us.isa.restest.testcases.TestCoverage;

import java.util.ArrayList;
import java.util.List;

public class SimpleDiffMeter implements CoverageMeter{

    @Override
    public List<TestCoverage> evaluate(List<TestCoverage> testCaseList) {
        List<TestCoverage> result = new ArrayList<>();
        List<List<String>> totalCoverage = new ArrayList<>();
        for (int i = 0; i < testCaseList.get(0).getCoverage().size(); i++) {
            totalCoverage.add(new ArrayList<>());
            for (int j = 0; j < testCaseList.get(0).getCoverage().get(i).size(); j++) {
                totalCoverage.get(i).add("");
            }
        }
        while (hasNullMethod(totalCoverage) || testCaseList.size() == 0) {
            int maxDiff = 0;
            TestCoverage best = null;
            for (TestCoverage newTc : testCaseList) {
                int diff = newTc.diff(totalCoverage);
                if (diff > maxDiff) {
                    best = newTc;
                    maxDiff = diff;
                }
            }
            if (best != null) {
                result.add(best);
                ReadAndEvaluate.putTogether(totalCoverage, best.getCoverage());
                testCaseList.remove(best);
            } else break;
        }
        return result;
    }

    private static boolean hasNullMethod(List<List<String>> cov) {
        for (List<String> strings : cov) {
            for (String string : strings) {
                if (string.equals("")) return true;
            }
        }
        return false;
    }


}
