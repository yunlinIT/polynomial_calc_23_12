package com.ll; // 패키지

import java.util.Arrays;
import java.util.stream.Collectors;

public class Calc {

  public static boolean recursionDebug = true; // 내가 디버그 모드를 켜겠다 할때는 true로 변경

  public static int runCallCount = 0;

  public static int run(String exp) {
    return _run(exp, 0);
  }

  public static int _run(String exp, int depth) {
    runCallCount++;

    exp = exp.trim();
    exp = stripOuterBracket(exp);

    // 음수괄호 패턴이면, 기존에 갖고있던 해석 패턴과는 맞지 않으니 패턴을 변경
    int[] pos = null;
    while ((pos = findNegativeCaseBracket(exp)) != null) {
      exp = changeNegativeBracket(exp, pos[0], pos[1]);
    }
    exp = stripOuterBracket(exp);

    if (recursionDebug) {
      System.out.print(" ".repeat(depth * 4));
      System.out.printf("exp(%d in %d) : %s\n", runCallCount, depth, exp);
    }

    // 연산기호가 없으면 바로 리턴
    if (!exp.contains(" ")) return Integer.parseInt(exp);

    boolean needToMultiply = exp.contains(" * ");
    boolean needToPlus = exp.contains(" + ") || exp.contains(" - ");

    boolean needToCompound = needToMultiply && needToPlus;
    boolean needToSplit = exp.contains("(") || exp.contains(")");

    if (needToSplit) {
      exp = exp.replaceAll("- ", "\\+ -");
      int splitPointIndex = findSplitPointIndex(exp);

      String firstExp = exp.substring(0, splitPointIndex);
      String secondExp = exp.substring(splitPointIndex + 1);

      char operator = exp.charAt(splitPointIndex);

      exp = Calc._run(firstExp, depth + 1) + " " + operator + " " + Calc._run(secondExp, depth + 1);

      return Calc._run(exp, depth + 1);

    } else if (needToCompound) {
      String[] bits = exp.split(" \\+ ");

      String newExp = Arrays.stream(bits).mapToInt(e -> Calc.run(e)).mapToObj(e -> e + "").collect(Collectors.joining(" + "));

      return Calc._run(newExp, depth + 1);
    }
    if (needToPlus) {
      exp = exp.replaceAll("\\- ", "\\+ \\-");

      String[] bits = exp.split(" \\+ ");

      int sum = 0;

      for (int i = 0; i < bits.length; i++) {
        sum += Integer.parseInt(bits[i]);
      }

      return sum;
    } else if (needToMultiply) {
      String[] bits = exp.split(" \\* ");

      int rs = 1;

      for (int i = 0; i < bits.length; i++) {
        rs *= Integer.parseInt(bits[i]);
      }
      return rs;
    }

    throw new RuntimeException("처리할 수 있는 계산식이 아닙니다");
  }

  private static String changeNegativeBracket(String exp, int startPos, int endPos) {
    String head = exp.substring(0, startPos);
    String body = "(" + exp.substring(startPos + 1, endPos + 1) + " * -1)";
    String tail = exp.substring(endPos + 1);

    exp = head + body + tail;

    return exp;
  }

  private static int[] findNegativeCaseBracket(String exp) {
    for (int i = 0; i < exp.length() - 1; i++) {
      if (exp.charAt(i) == '-' && exp.charAt(i + 1) == '(') {
        // 어? 마이너스 괄호 찾았다
        int bracketCount = 1;

        for (int j = i + 2; j < exp.length(); j++) {
          char c = exp.charAt(j);

          if (c == '(') {
            bracketCount++;
          } else if (c == ')') {
            bracketCount--;
          }

          if (bracketCount == 0) {
            return new int[]{i, j};
          }
        }
      }
    }
    return null;
  }

  private static int findSplitPointIndexBy(String exp, char findChar) {
    int bracketCount = 0;

    for (int i = 0; i < exp.length(); i++) {
      char c = exp.charAt(i);

      if (c == '(') {
        bracketCount++;
      } else if (c == ')') {
        bracketCount--;
      } else if (c == findChar) {
        if (bracketCount == 0) return i;
      }
    }
    return -1;
  }

  private static int findSplitPointIndex(String exp) {
    int index = findSplitPointIndexBy(exp, '+');

    if (index >= 0) return index;

    return findSplitPointIndexBy(exp, '*');
  }

  private static String stripOuterBracket(String exp) {
    if (exp.charAt(0) == '(' && exp.charAt(exp.length() - 1) == ')') {
      int bracketCount = 0;

      for (int i = 0; i < exp.length(); i++) {
        if (exp.charAt(i) == '(') {
          bracketCount++;
        } else if (exp.charAt(i) == ')') {
          bracketCount--;
        }

        if (bracketCount == 0) {
          if (exp.length() == i + 1) {
            return stripOuterBracket(exp.substring(1, exp.length() - 1));
          }

          return exp;
        }
      }
    }
    return exp;
  }
}


//String exp = "-(8 + 2) * -(7 + 3) + 5";
//
//int startPos = 0;
//int endPos = 7;
//
//String head = exp.substring(0, startPos);
//String body = "(" + exp.substring(startPos + 1, endPos + 1) + " * -1)";
//String tail = exp.substring(endPos + 1);
//
//    System.out.println("head : " + head);
//    System.out.println("body : " + body);
//    System.out.println("tail : " + tail);
//
//    System.out.println("전체 : " + head + body + tail);