package com.spartanullnull.otil.domain.post.repository;

import com.spartanullnull.otil.domain.post.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();
}
