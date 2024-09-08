package com.youngcamp.server.aspect;

import com.vane.badwordfiltering.BadWordFiltering;
import com.youngcamp.server.annotation.ProfanityCheck;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProfanityFilterAspect {

  private final BadWordFiltering badWordFiltering = new BadWordFiltering();
  private final Map<String, Pattern> cachedPatterns = new HashMap<>();

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

          if (value == null || !(value instanceof String)) {
            continue;
          }

          String fieldValue = (String) value;

          if (badWordFiltering.blankCheck(fieldValue)) { // check() 메서드로 변경
            throw new IllegalArgumentException("비속어가 포함되어 있습니다.");
          }

          for (String badWord : badWordFiltering) {
            Pattern pattern = cachedPatterns.get(badWord);

            // 캐싱된 패턴이 없거나 유효하지 않으면 새로 생성
            if (pattern == null || !pattern.matcher(badWord).matches()) {
              pattern = generateProfanityPattern(badWord);
              cachedPatterns.put(badWord, pattern); // 캐시에 업데이트
            }

            // 필터링 로직
            if (pattern.matcher(fieldValue).find()) {
              throw new IllegalArgumentException("변형된 비속어가 감지되었습니다: " + badWord);
            }
          }
        }
      }
    }
  }

  private Pattern generateProfanityPattern(String word) {
    String patternString =
        word.replaceAll("([a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ\\p{Punct}])", "$1[^a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ\\p{Punct}]*")
            .replaceAll("\\s+", "\\s*")
            .replaceAll("%", "[%]*");

    return Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
  }
}
