package es.us.isa.restest.util;

import java.util.List;

public class SootJson {
    String test_id;
    List<String> soot_result;

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public List<String> getSoot_result() {
        return soot_result;
    }

    public void setSoot_result(List<String> soot_result) {
        this.soot_result = soot_result;
    }
}
