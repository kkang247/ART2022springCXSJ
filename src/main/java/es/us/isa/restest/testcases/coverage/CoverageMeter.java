package es.us.isa.restest.testcases.coverage;

import es.us.isa.restest.testcases.TestCoverage;

import java.util.List;

public interface CoverageMeter {
    List<TestCoverage> evaluate(List<TestCoverage> testCaseList);
}
