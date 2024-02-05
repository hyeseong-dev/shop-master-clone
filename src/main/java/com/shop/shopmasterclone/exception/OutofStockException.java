package com.shop.shopmasterclone.exception;

/**
 * 재고 부족 상황에서 발생하는 예외를 나타내는 클래스입니다.
 * 이 예외는 상품 주문 과정에서 요청된 상품의 재고가 충분하지 않을 때 발생합니다.
 * {@link RuntimeException}을 상속받아, 체크되지 않는(unchecked) 예외로 처리됩니다.
 *
 * 이 클래스는 주로 상품 관리 시스템 내에서 재고 관리 로직에 의해 사용됩니다.
 * 예외 메시지는 상세한 재고 부족 상황을 설명하는 데 사용될 수 있습니다.
 */
public class OutofStockException extends RuntimeException {
    /**
     * 재고 부족 예외를 생성합니다. 예외 메시지를 인자로 받아 초기화합니다.
     *
     * @param message 재고 부족 상황을 설명하는 문자열입니다.
     *                이 메시지는 예외가 발생했을 때 로그나 사용자에게 제공되어 문제의 원인을 설명하는 데 사용됩니다.
     */
    public OutofStockException(String message) {
        super(message);
    }
}

