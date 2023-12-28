package com.ll;

public class Calc {
  public static int run(String exp) {


    String[] bits = exp.split(" ");

    int a = Integer.parseInt(bits[0]);
    int c = Integer.parseInt(bits[2]);


    if (bits[1].equals("+")) {
      return a + c;
    } else if (bits[1].equals("-")) {
      return a - c;
    } else {
      return 0;
    }
  }
}
