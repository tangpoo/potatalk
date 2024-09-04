package com.potatalk.memberservice.domain;

public class FriendSteps {

    public static Friend create(Long memberId, Long friendId) {
        return Friend.create(memberId, friendId);
    }
}
