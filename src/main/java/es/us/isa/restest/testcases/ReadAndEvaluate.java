package es.us.isa.restest.testcases;

import es.us.isa.restest.testcases.coverage.CoverageMeter;
import es.us.isa.restest.testcases.coverage.SimpleDiffMeter;
import es.us.isa.restest.testcases.coverage.SubDomainMeter;
import es.us.isa.restest.util.JSONManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ReadAndEvaluate {

    private static CoverageARTStrategy coverageARTStrategy = CoverageARTStrategy.SIMPLE;
    private static List<TestCoverage> testCaseList = new ArrayList<>();
    private static CoverageMeter coverageMeter;

    public static void setTestCaseList(List<TestCoverage> testCaseList) {
        ReadAndEvaluate.testCaseList = testCaseList;
    }

    public static Collection<TestCase> generateCoverageTestCaseCollection() throws IOException {

//        readCoverageTestCases();

        switch (coverageARTStrategy){
            case SIMPLE: coverageMeter = new SimpleDiffMeter(); break;
            case SUBDOMAIN: coverageMeter = new SubDomainMeter(); break;
        }

        List<TestCoverage> newTestCaseList;
        if (testCaseList.size() < 2) return translateToTestCase(testCaseList);

        List<List<String>> totalCoverage = new ArrayList<>();
        for (int i = 0; i < testCaseList.get(0).getCoverage().size(); i++) {
            totalCoverage.add(new ArrayList<>());
            for (int j = 0; j < testCaseList.get(0).getCoverage().get(i).size(); j++) {
                totalCoverage.get(i).add("");
            }
        }

        newTestCaseList = coverageMeter.evaluate(testCaseList);

        return translateToTestCase(newTestCaseList);
    }

    public static void readCoverageTestCases() throws IOException {
        List<TestCase> testCases = JSONManager.readTestCase();
        HashMap<String, List<String>> testCaseCoverageMap = JSONManager.readCoverage();
        for (TestCase testCase : testCases) {
            List<String> covList = testCaseCoverageMap.get(testCase.getId());
            List<List<String>> coverageList = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                coverageList.add(new ArrayList<>());
            }
            for (int i = 0; i < 5; i++) {
                coverageList.get(0).add("");
            }
            for (int i = 0; i < 5; i++) {
                coverageList.get(1).add("");
            }
            for (int i = 0; i < 15; i++) {
                coverageList.get(2).add("");
            }
            for (int i = 0; i < 7; i++) {
                coverageList.get(3).add("");
            }
            for (int i = 0; i < 6; i++) {
                coverageList.get(4).add("");
            }
            for (int i = 0; i < 5; i++) {
                coverageList.get(5).add("");
            }
            for (String str : covList) {
                String[] s = str.split("-");
                int sevId = Integer.parseInt(s[0]);
                int mtdId = Integer.parseInt(s[1]);
                String oldCov = coverageList.get(sevId).get(mtdId);
                if (oldCov.equals("")) {
                    coverageList.get(sevId).set(mtdId, s[2]);
                } else {
                    StringBuilder newCov = new StringBuilder();
                    for (int i = 0; i < oldCov.length(); i++) {
                        String additionCov = coverageList.get(sevId).get(mtdId);
                        newCov.append((oldCov.charAt(i) == '1' || additionCov.charAt(i) == '1') ? '1' : '0');
                    }
                    coverageList.get(sevId).set(mtdId, newCov.toString());
                }
            }

            testCaseList.add(new TestCoverage(testCase, coverageList));
        }
    }

    // merge the new list together with the old list
    public static void putTogether(List<List<String>> oldList, List<List<String>> newList) {
        for (int i = 0; i < oldList.size(); i++) {
            for (int j = 0; j < oldList.get(i).size(); j++) {
                String s1 = oldList.get(i).get(j);
                String s2 = newList.get(i).get(j);
                if (!s1.equals(s2)) {
                    if (s1.equals("")) {
                        oldList.get(i).set(j, s2);
                    } else if (s2.equals("")) {
                        oldList.get(i).set(j, s1);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int k = 0; k < Math.min(s1.length(), s2.length()); k++) { // length of s1 and s2 should be same
                            stringBuilder.append((s1.charAt(k) == '1' || s2.charAt(k) == '1') ? '1' : '0');
                        }
                        oldList.get(i).set(j, stringBuilder.toString());
                    }
                }
            }
        }
    }

    private static List<TestCase> translateToTestCase(List<TestCoverage> testCoverages) {
        List<TestCase> result = new ArrayList<>();
        for (TestCoverage ts : testCoverages) {
            result.add(ts.getTestCase());
        }
        return result;
    }

    public enum CoverageARTStrategy{
        SIMPLE,
        SUBDOMAIN
    }

}
