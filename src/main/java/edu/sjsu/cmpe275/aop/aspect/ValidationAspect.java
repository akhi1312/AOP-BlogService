package edu.sjsu.cmpe275.aop.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;  // if needed
import org.aspectj.lang.annotation.Around;  // if needed
import org.aspectj.lang.ProceedingJoinPoint; // if needed
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
@Order(2)
@Aspect
public class ValidationAspect {
	/**
	 *
	 * @param joinPoint
	 * Method Validates the User Id Length Must be between 3 to 16 character
	 */

	@Before("execution(public * edu.sjsu.cmpe275.aop.BlogService.*(..))")
	public void userValidation(JoinPoint joinPoint)  {
		Object[] userId = joinPoint.getArgs();



		if(joinPoint.getSignature().getName() == "commentOnBlog")
		{
              userId = Arrays.copyOfRange(userId ,0,(userId.length -2));
		}
		for(Object user :userId) {
			if (user == null) {
				throw new IllegalArgumentException("Userd Id Length is less than 3");
			} else {
				String us = (String) user;
				//us = us.trim();


				if (us.length() < 3 || us.length() > 16 || us.isEmpty()) {
					throw new IllegalArgumentException("Userd Id Length is less than 3");
				}


			}
		}
	}


	/**
	 *
	 * @param joinPoint
	 * Method Validates the message length on blog shoud not be more than 100 character
	 */
	@Before("execution(public void edu.sjsu.cmpe275.aop.BlogService.commentOnBlog(..))")
	public void commentValidation(JoinPoint joinPoint)  {

		if(joinPoint.getArgs()[2] == null)
		{
			throw new IllegalArgumentException("Invalid Comment length");
		}
		else {
			String userMessage = (String) joinPoint.getArgs()[2];

			//userMessage = userMessage.trim();
			if (userMessage.isEmpty() || userMessage.length() > 100) {
				throw new IllegalArgumentException("Invalid Comment length");
			}
		}
	}
}