package pcf.crksdev.spquiz.services.user.context;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pcf.crksdev.spquiz.services.user.User;

import java.net.URI;

@Configuration
class AuthenticatedUserProvider {

    @Autowired
    private UserMappers mappers;

    @Bean("authenticated-user")
    User authenticatedUser() {
        User dummyTarget = new User("", "", "", "", URI.create(""));
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(dummyTarget);
        factory.addAdvice(new UserAdvice());
        return (User) factory.getProxy();
    }

    private class UserAdvice implements MethodInterceptor {

        @Override
        public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
            final Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
            if(authentication == null) {
                throw new UsernameNotFoundException("User not authenticated");
            }
            final BeanWrapper wrapper = new BeanWrapperImpl(
                mappers.fromAuthentication(authentication)
            );
            //remove get prefix
            final String propertyName = invocation.getMethod().getName().substring(3);
            return wrapper.getPropertyValue(propertyName);
        }
    }

}
