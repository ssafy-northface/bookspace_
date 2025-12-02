package com.bookspace.domain.common.validation;

/**
 * 유저 존재 여부를 검증하는 AOP Validator
 *
 * [역할]
 * - Service 메서드 실행 잉전 (before advice)에 userId 검사
 * - User 관련 기능(Comment, Review, Wish, Post 등)에서 반복되는 사용자 존재 체크 로직을 공통화
 *
 * [목적]
 * - 잘못된 userId로 인해 발생할 수 있는 Null 데이터 접근, FK 무결성 오류 등을 사전에 방지
 * - Commnet, Wish, Review 등 도메인에서 재사용 가능
 *
 * [적용 위치]
 * - @ValidateUserExists 어노테이션이 붙은 Serivce 메서드
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateUserExists {
    String userId();
}
