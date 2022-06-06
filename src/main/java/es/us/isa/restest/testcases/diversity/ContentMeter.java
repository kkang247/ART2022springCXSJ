package es.us.isa.restest.testcases.diversity;

import es.us.isa.restest.testcases.TestCase;

public class ContentMeter {

    private final double HeaderPara = 0.25;
    private final double PathPara = 0.25;
    private final double QueryPara = 0.25;
    private final double FormPara = 0.25;

    /**
     * <p>返回值越大，相似度越大， 返回值为1时表示完全相同</p>
     */
    public Double apply(TestCase left, TestCase right) {
        final double[] sim = {0};
        left.getHeaderParameters().forEach((s1, s2) ->
                sim[0] += s2.equals(right.getHeaderParameters().get(s1)) ? HeaderPara : 0);
        left.getPathParameters().forEach((s1, s2) ->
                sim[0] += s2.equals(right.getPathParameters().get(s1)) ? PathPara : 0);
        left.getQueryParameters().forEach((s1, s2) ->
                sim[0] += s2.equals(right.getQueryParameters().get(s1)) ? QueryPara : 0);
        left.getFormParameters().forEach((s1, s2) ->
                sim[0] += s2.equals(right.getFormParameters().get(s1)) ? FormPara : 0);
        return sim[0];
    }
}
