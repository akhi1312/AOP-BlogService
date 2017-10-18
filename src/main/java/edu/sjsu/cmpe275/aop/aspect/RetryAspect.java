package edu.sjsu.cmpe275.aop.aspect;
import edu.sjsu.cmpe275.aop.exceptions.AccessDeniedExeption;
import edu.sjsu.cmpe275.aop.exceptions.NetworkException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;  // if needed
import org.aspectj.lang.annotation.Around;  // if needed
import org.aspectj.lang.ProceedingJoinPoint; // if needed
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
@Order(1)
@Aspect
public class RetryAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */
    /**
     * Method revolves around every Blogservice to check the network connectivity
     * @param joinPoint
     * @throws Throwable
     */
    @Around("execution(public * edu.sjsu.cmpe275.aop.BlogService.*(..))")
    public void retryAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        int retry = 0;

        while(retry<=2)
        {
            try{
                joinPoint.proceed();
                break;
            }
            catch(AccessDeniedExeption e)
            {
                throw new AccessDeniedExeption(e.getMessage());
            }

            catch (IllegalArgumentException ex)
            {
                throw new IllegalArgumentException(ex.getMessage());
            }
            catch(Exception exc)
            {
                if(retry==2)
                    throw new NetworkException(exc.getMessage());
            }
            retry++;
        }
    }
}