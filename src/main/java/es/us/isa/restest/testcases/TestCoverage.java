package es.us.isa.restest.testcases;

import java.util.List;

public class TestCoverage {
    private TestCase testCase;
    private List<List<String>> coverage;
    public String tempName;

    // count the diff rate
    public int diff(List<List<String>> covList) {
        int diff = 0;

        for (int i = 0; i < Math.min(coverage.size(), covList.size()); i++) {
            for (int j = 0; j < Math.min(coverage.get(i).size(), covList.get(i).size()); j++) {
                String s1 = coverage.get(i).get(j);
                String s2 = covList.get(i).get(j);
                if (!s1.equals(s2)) {
                    if (s2.equals("")) { // s1 is not "", s2 is "" --> <i,j> is new covered
                        diff += count1s(s1); // count 1 of s1
                    } else { // s1 and s2 both not "" --> <i,j> covered different --> scan and count difference
                        for (int k = 0; k < Math.min(s1.length(), s2.length()); k++) { // length of s1 s2 should be same
                            if (s1.charAt(k) == '1' && s2.charAt(k) == '0') diff++;
                        }
                    }
                }

            }
        }

        return diff;
    }

    public int fill(List<List<String>> covList){
        int fill = 0;

        for (int i = 0; i < Math.min(coverage.size(), covList.size()); i++) {
            for (int j = 0; j < Math.min(coverage.get(i).size(), covList.get(i).size()); j++) {
                String s1 = coverage.get(i).get(j);
                String s2 = covList.get(i).get(j);
                if (s2.equals("") && (!s1.equals(""))) fill++;
            }
        }

        return fill;
    }

    public TestCoverage(String string, List<List<String>> coverage) {
        this.tempName = string;
        this.coverage = coverage;
    }

    public TestCoverage(TestCase testCase, List<List<String>> coverage) {
        this.testCase = testCase;
        this.coverage = coverage;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public List<List<String>> getCoverage() {
        return coverage;
    }

    public void setCoverage(List<List<String>> coverage) {
        this.coverage = coverage;
    }

    // count the number of 1 in coverage
    private int count1s(String s) {
        int cnt1 = 0;
        for (int i = 0; i < s.length(); i++) {
            cnt1 += s.charAt(i) == '1' ? 1 : 0;
        }
        return cnt1;
    }

    public String getCoverageInfo(){
        StringBuilder result = new StringBuilder();
        for (List<String> strings : coverage) {
            for (String string : strings) {
                result.append("\"").append(string).append("\"").append(",");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
