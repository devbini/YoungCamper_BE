package com.youngcamp.server.repository;

import com.youngcamp.server.domain.Announcement;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

  @Modifying
  @Transactional
  @Query("DELETE FROM Announcement a WHERE a.id IN :ids")
  void deleteAllByIds(@Param("ids") List<Long> ids);

  @Query("SELECT a.id FROM Announcement a WHERE a.id IN :ids")
  List<Long> findExistingIds(@Param("ids") List<Long> ids);

  List<Announcement> findAllByOrderByCreatedAtDesc();

  @Query("SELECT a FROM Announcement a LEFT JOIN FETCH a.contents WHERE a.id = :id")
  Optional<Announcement> findByIdWithContents(@Param("id") Long id);

  @Query(
      "SELECT a FROM Announcement a JOIN a.contents c WHERE c.title LIKE %:title% ORDER BY a.createdAt DESC")
  List<Announcement> findByTitleLikeOrderByCreatedAtDesc(@Param("title") String title);

  @Query(
      "SELECT DISTINCT a FROM Announcement a JOIN FETCH a.contents c WHERE (LOWER(c.title) LIKE %:keyword% OR LOWER(c.content) LIKE %:keyword%) AND c.languageCode = :languageCode")
  List<Announcement> findByKeywordAndLanguageCode(
      @Param("keyword") String keyword, @Param("languageCode") String languageCode);
}
