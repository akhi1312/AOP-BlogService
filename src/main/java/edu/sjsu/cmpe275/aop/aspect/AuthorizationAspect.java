package edu.sjsu.cmpe275.aop.aspect;

import edu.sjsu.cmpe275.aop.exceptions.AccessDeniedExeption;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.beans.factory.annotation.Autowired;  // if needed

import edu.sjsu.cmpe275.aop.BlogService;

import org.aspectj.lang.annotation.Aspect;  // if needed
import org.aspectj.lang.JoinPoint;  // if needed
import org.aspectj.lang.annotation.After;  // if needed
import org.aspectj.lang.annotation.Before;  // if needed
import org.springframework.core.annotation.Order;

import java.util.*;

@Order(3)
@Aspect
public class AuthorizationAspect {

	Map<String,ArrayList<String>> m1 = new HashMap<String, ArrayList<String>>();
	ArrayList<String> sharedUser = new ArrayList<String>();

	@Autowired BlogService blogService;

	/**
	 * Method validates the share authorization of blog
	 * @param joinPoint
	 * @throws AccessDeniedExeption
	 */

	@Before("execution(public void edu.sjsu.cmpe275.aop.BlogService.shareBlog(..))")
	public void beforeShareAdvice(JoinPoint joinPoint) throws AccessDeniedExeption {
		String userId = (String)joinPoint.getArgs()[0];
		String bloguserId = (String)joinPoint.getArgs()[1];


		boolean found = false ;

		if(userId == bloguserId)
		{
			found = true;

		}
		else if (m1.containsKey(bloguserId))
		{
			ArrayList<String> users = m1.get(bloguserId);
			for(String user:users)
			{
				if (user == userId )
					found = true;

			}

		}
		if(!found)
		{
			throw new AccessDeniedExeption(bloguserId + " does not share blog to " + userId );
		}
	}

	/**
	 * Method adds the list of Authorized users to Hash Map
	 * @param joinPoint
	 * @throws AccessDeniedExeption
	 */
	@AfterReturning("execution(public void edu.sjsu.cmpe275.aop.BlogService.shareBlog(..))")
	public void afterShareAdvice(JoinPoint joinPoint) throws AccessDeniedExeption {
		String bloguserId = (String) joinPoint.getArgs()[1];
		String targetuserId = (String) joinPoint.getArgs()[2];

		sharedUser.add(targetuserId);
		m1.put(bloguserId, sharedUser);
	}


	/**
	 * Method Validates the Authorize users who can read blog of others
	 * @param joinPoint
	 * @throws AccessDeniedExeption
	 */
	@Before("execution(public edu.sjsu.cmpe275.aop.Blog edu.sjsu.cmpe275.aop.BlogService.readBlog(..))")
	public void readAdvice(JoinPoint joinPoint) throws AccessDeniedExeption {

		String userId = (String)joinPoint.getArgs()[0];
		String bloguserId = (String)joinPoint.getArgs()[1];
		boolean found = false ;
		if(m1.containsKey(bloguserId))
		{
			ArrayList<String> users = m1.get(bloguserId);

			for(String user:users)
			{

				if (user == userId )
				{
					found = true ;
				}

			}
		}
		if(!found)
		{
			throw new AccessDeniedExeption(userId + " does not able to read blog of " + bloguserId);
		}

	}

	/**
	 * Method Validates the authorize users who can comment on others blog
	 * @param joinPoint
	 * @throws AccessDeniedExeption
	 */

	@Before("execution(public void edu.sjsu.cmpe275.aop.BlogService.commentOnBlog(..))")
	public void commentAdvice(JoinPoint joinPoint) throws AccessDeniedExeption {

		String userId = (String)joinPoint.getArgs()[0];
		String bloguserId = (String)joinPoint.getArgs()[1];
		boolean found = false ;
		if(m1.containsKey(bloguserId))
		{
			ArrayList<String> users = m1.get(bloguserId);

			for(String user:users)
			{

				if (user == userId )
				{
					found = true ;
				}

			}

		}
		if(!found)
		{
			throw new AccessDeniedExeption(userId + " does not have access to comment on  " + bloguserId + " blog");
		}



	}

	/**
	 *
	 * Method Validates the User whether the particular blog can be unshared or not
	 * @param joinPoint
	 * @throws AccessDeniedExeption
	 */

	@Before("execution(public void edu.sjsu.cmpe275.aop.BlogService.unshareBlog(..))")
	public void beforeUnshareAdvice(JoinPoint joinPoint) throws AccessDeniedExeption {
		String userId = (String)joinPoint.getArgs()[0];
		String targetuserId = (String)joinPoint.getArgs()[1];

		boolean found = false ;
		if(m1.containsKey(userId))
		{
			ArrayList<String> users = m1.get(userId);
			for(String user:users)
			{
				if (user == targetuserId )
					found = true;
			}

		}
		if(!found)
		{
			throw new AccessDeniedExeption(userId + " did not share any Blog to " + targetuserId);
		}
	}

	/**
	 * Method removes the user from Hasp Map once the blog is unshared
	 * @param joinPoint
	 * @throws AccessDeniedExeption
	 */
	@AfterReturning("execution(public void edu.sjsu.cmpe275.aop.BlogService.unshareBlog(..))")
	public void afterUnshareAdvice(JoinPoint joinPoint) throws AccessDeniedExeption {
		String userId = (String)joinPoint.getArgs()[0];
		String targetuserId = (String)joinPoint.getArgs()[1];
		ArrayList<String> users = m1.get(userId);
		Iterator<String> iter = users.iterator();
		while (iter.hasNext()) {
			String user = iter.next();

			if (user == targetuserId )
				iter.remove();
		}

	}


}
