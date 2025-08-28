
package com.shinhanDS5gi.memento.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MyMentosByMentiSliceResponse {
    private final List<MyMentosByMentiResponse> content;
    private final Long nextCursor;
    private final boolean hasNext;
}
