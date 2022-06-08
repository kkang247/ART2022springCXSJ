package es.us.isa.restest.testcases.coverage;

import es.us.isa.restest.testcases.ReadAndEvaluate;
import es.us.isa.restest.testcases.TestCoverage;

import java.util.ArrayList;
import java.util.List;

public class SubDomainMeter implements CoverageMeter{
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
        while (hasNullMethod(totalCoverage) || testCaseList.size() == 0){
            int fillNum = 0; // how many empty methods this testcase can fill in
            TestCoverage best = null;
            for (TestCoverage newTc : testCaseList) {
                int fill = newTc.fill(totalCoverage);
                if (fill > fillNum){
                    best = newTc;
                    fillNum = fill;
                }
            }
            if (best != null){
                result.add(best);
                ReadAndEvaluate.putTogether(totalCoverage, best.getCoverage());
                testCaseList.remove(best);
            }
        }

        for (TestCoverage tc: result) {
            System.out.println(tc.tempName);
        }

        return result;
    }

    private static boolean hasNullMethod(List<List<String>> cov){
        for (List<String> strings : cov) {
            for (String string : strings) {
                if (string.equals("")) return true;
            }
        }
        return false;
    }
}
