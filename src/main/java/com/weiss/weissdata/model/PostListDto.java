package com.weiss.weissdata.model;

import com.weiss.weissdata.model.forum.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostListDto {
    List<Post> postList;

}
