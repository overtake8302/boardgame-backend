package com.elice.boardgame.category.repository;

import com.elice.boardgame.category.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

}
