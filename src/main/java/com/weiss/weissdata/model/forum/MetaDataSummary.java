package com.weiss.weissdata.model.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
public class MetaDataSummary {
    int like;
    int dislike;
    int comment;
    int views;
    int loads;

    public MetaDataSummary() {
        this.like = 0;
        this.dislike = 0;
        this.comment = 0;
        this.views = 0;
        this.loads = 0;
    }
}
