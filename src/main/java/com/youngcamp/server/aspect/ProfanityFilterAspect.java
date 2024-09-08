package com.youngcamp.server.aspect;

import com.vane.badwordfiltering.BadWordFiltering;
import com.youngcamp.server.annotation.ProfanityCheck;
import java.lang.reflect.Field;
import java.util.regex.Pattern;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProfanityFilterAspect {

  private final BadWordFiltering badWordFiltering = new BadWordFiltering();

  @Before("execution(* com.youngcamp..*(..))")
  public void checkProfanity(JoinPoint joinPoint) throws IllegalAccessException {
    Object[] args = joinPoint.getArgs();

    for (Object arg : args) {
      if (arg == null) {
        continue;
      }

      Field[] fields = arg.getClass().getDeclaredFields();
      for (Field field : fields) {
        if (field.isAnnotationPresent(ProfanityCheck.class)) {
          field.setAccessible(true);
          Object value = field.get(arg);

          System.out.println(field);
          System.out.println(value);
          if (value == null || !(value instanceof String)) {
            continue;
          }

          String fieldValue = (String) value;

          if (badWordFiltering.contains(fieldValue)) {
            System.out.println("라이브러리에서 걸림");
            throw new IllegalArgumentException("비속어가 포함되어 있습니다.");
          }

          Pattern pattern =
              Pattern.compile(generateProfanityPattern(fieldValue), Pattern.CASE_INSENSITIVE);
          if (pattern.matcher(fieldValue).find()) {
            System.out.println("정규식에 의해 걸림!!");
            throw new IllegalArgumentException("정규식에 의해 비속어가 감지되었습니다.");
          }
        }
      }
    }
  }

  private String generateProfanityPattern(String word) {
    return word.replaceAll("([a-zA-Z가-힣])", "$1[^a-zA-Z가-힣0-9]*\\s*") // 문자 사이에 숫자나 기호 허용
        .replaceAll("([.,!?])", "\\\\$1") // 특수 문자 escape 처리
        .replaceAll("\\s+", "\\\\s*"); // 공백 허용
  }
}
