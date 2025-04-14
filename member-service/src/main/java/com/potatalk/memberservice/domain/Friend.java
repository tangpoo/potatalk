package com.potatalk.memberservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("friends")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    private Long id;

    private Long memberId;

    private Long friendId;

    private boolean isAccepted = false;

    private Friend(final Long memberId, final Long friendId) {
        this.memberId = memberId;
        this.friendId = friendId;
    }

    public static Friend create(Long memberId, Long friendId) {
        return new Friend(memberId, friendId);
    }

    public Friend accept() {
        this.isAccepted = true;
        return this;
    }
}
