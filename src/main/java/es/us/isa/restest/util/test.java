package es.us.isa.restest.util;

import java.io.File;

public class test {
    public static void main(String[] args) {
        File file = new File("F:\\zxc-booking-backend-final\\coverage.txt");
        boolean value = file.delete();
    }
}
