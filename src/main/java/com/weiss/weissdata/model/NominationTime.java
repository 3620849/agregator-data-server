package com.weiss.weissdata.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Builder
@Data
public class NominationTime {
    @Id
    private String id;
    private int time;
    private int timeout;
}
