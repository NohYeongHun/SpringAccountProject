package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다"),
    USER_NOT_FOUND("사용자가 없습니다."),
    ACCOUNT_NOT_FOUND("계좌를 찾을 수 없습니다."),
    ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용 중입니다."),
    TRANSACTION_NOT_FOUND("해당 거래가 없습니다."),
    EXIST_SAME_PRIVATE_NUMBER("동일한 사용자가 존재합니다."),
    ALREADY_UNREGISTERED("이미 해지된 계정입니다."),
    USER_ACCOUNT_UN_MATCH("유저가 일치하지 않습니다."),
    TRANSACTION_ACCOUNT_UN_MATCH("이 거래는 해당 계좌에서 발생한 거래가 아닙니다."),
    CANCEL_MUST_FULLY("부분 취소는 허용되지 않습니다."),
    TOO_OLD_ORDER_TO_CANCEL("1년이 지난 거래는 취소가 불가능합니다."),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개 입니다."),
    EXISTS_BALANCE("잔액이 남아있습니다."),
    AMOUNT_EXCEED_BALANCE("거래금액이 잔액보다 큽니다.");

    private final String description;

}
