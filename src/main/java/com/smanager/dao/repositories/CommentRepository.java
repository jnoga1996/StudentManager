package com.smanager.dao.repositories;

import com.smanager.dao.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllBySolutionIdOrderByCreationDateDesc(Long solutionId);
}
