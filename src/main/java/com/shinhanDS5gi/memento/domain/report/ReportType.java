package com.shinhanDS5gi.memento.domain.report;

public enum ReportType {
    ABUSING, /* 어뷰징 - 다계정 생성, 허위 리뷰 작성, 시스템 조작 등으로 플랫폼 신뢰를 훼손하는 행위 */
    IDENTITY_THEFT, /* 명의 도용 - 타인의 명의나 신분 도용하여 계정 생성 및 멘토링 활동하는 행위 */
    FRAUD, /* 금전적 사기, 불법 행위 - 멘토링 과정에서 계좌 송금 요청, 외부 결제 강요, 수익률 보장 같은 불법 투자 권유 행위 */
    ABUSIVE_LANGUAGE, /* 부적절한 언행, 욕설 - 멘토링 중 욕설, 협박, 성희롱, 모욕 등 상대방의 존엄성을 침해하는 행위 */
    COMMERCIAL_AD, /* 스팸, 광고성 행동 - 멘토링과 무관한 상업적 광고, 다단계 권유, 외부 링크 유포 행위 */
    PERSONAL_DATA_ABUSE; /* 개인정보 요구, 유출 - 계좌번호, 신분증 사본, 비밀번호 등 과도한 개인정보 요구 또는 무단 유출 행위 */
}