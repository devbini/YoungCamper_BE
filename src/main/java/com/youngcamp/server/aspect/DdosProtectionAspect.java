package com.youngcamp.server.aspect;

import com.youngcamp.server.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DdosProtectionAspect {

  private static final long THRESHOLD_TIME = 10 * 1000;
  private static final int MAX_REQUESTS = 1;

  private ConcurrentHashMap<String, Long[]> requestLog = new ConcurrentHashMap<>();

  private final HttpServletRequest request;

  public DdosProtectionAspect(HttpServletRequest request) {
    this.request = request;
  }

  @Before("@annotation(com.youngcamp.server.annotation.DdosProtected)")
  public void checkDdos() throws Exception {
    String ipAddress = request.getRemoteAddr();
    long currentTime = System.currentTimeMillis();

    requestLog.putIfAbsent(ipAddress, new Long[] {currentTime, 0L});
    Long[] logData = requestLog.get(ipAddress);

    long lastRequestTime = logData[0];
    long requestCount = logData[1];

    if (currentTime - lastRequestTime < THRESHOLD_TIME) {
      requestCount++;
      if (requestCount > MAX_REQUESTS) {
        throw new TooManyRequestsException("너무 많은 요청으로 제한되었습니다.");
      }
    } else {
      logData[0] = currentTime;
      requestCount = 1;
    }

    logData[1] = requestCount;
    requestLog.put(ipAddress, logData);
  }
}
