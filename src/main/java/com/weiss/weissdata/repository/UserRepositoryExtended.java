package com.weiss.weissdata.repository;

import com.weiss.weissdata.model.forum.MyMark;

public interface UserRepositoryExtended {
    boolean addToMyList(MyMark like);
}
