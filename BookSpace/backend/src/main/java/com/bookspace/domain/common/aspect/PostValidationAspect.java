package com.bookspace.domain.common.aspect;

import com.bookspace.domain.common.validation.ValidatePostExists;
import com.bookspace.domain.post.dao.PostDao;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class PostValidationAspect {
    private final PostDao postDao;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Before("@annotation(validatePostsExists)")
    public void validatePostsExists(JoinPoint joinPoint, ValidatePostExists validatePostsExists) {
        // 1) 현재 호출된 메서드 파라미터 가져오기
        Object[] args = joinPoint.getArgs();

        // 2) SpEL을 설정한 값으로 파싱
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = methodSignature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // 3) SpEL로 postId 추출
        Long postId = parser.parseExpression(validatePostsExists.postId()).getValue(context, Long.class);

        if(!postDao.existsById(postId)){
            throw new IllegalArgumentException("Post not found: " + postId);
        }
    }
}
