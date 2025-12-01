package com.bookspace.domain.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 게시글 존재 여부를 검증하는 AOP Validator
 *
 * [역할]
 * - Service 메서드 실행 잉전 (before advice)에 postId를 검사
 * - 요청된 postId에 해당하는 게시글이 DB에 존재하는지 확인
 * - 존재하지 않을 경우 예외를 발생시켜 비즈니스 로직 실행을 차단
 *
 * [목적]
 * - 반복되는 게시글 존재 여부 검증 로직은 AOP로 분리하여 공통화
 * - Commnet, Wish, Review 등 도메인에서 재사용 가능
 *
 * [적용 위치]
 * - @ValidationPostExists 어노테이션이 붙은 Serivce 메서드
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatePostExists {
    String postId(); // SpEl을 통해 postId 추출
}
