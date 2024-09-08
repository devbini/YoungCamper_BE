package com.youngcamp.server.repository;

import com.youngcamp.server.domain.Announcement;
import com.youngcamp.server.dto.AnnouncementDetailProjection;
import com.youngcamp.server.dto.AnnouncementProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

  void deleteAllById(Iterable<? extends Long> ids);

  @Query("SELECT a.id FROM Announcement a WHERE a.id IN :ids")
  List<Long> findExistingIds(@Param("ids") List<Long> ids);

  List<Announcement> findAllByOrderByCreatedAtDesc();

  @Query(
      "SELECT new com.youngcamp.server.dto.AnnouncementProjection(a.id, a.createdAt, a.isPinned, c.languageCode, c.title) "
          + "FROM Announcement a JOIN a.contents c "
          + "WHERE c.languageCode = :languageCode "
          + "ORDER BY a.createdAt DESC")
  List<AnnouncementProjection> findAllByLanguageCodeOrderByCreatedAtDesc(
      @Param("languageCode") String languageCode);

  @Query(
      "SELECT new com.youngcamp.server.dto.AnnouncementDetailProjection(a.id, a.isPinned, a.imageUrl, a.createdAt, a.updatedAt, c.languageCode, c.title, c.content) "
          + "FROM Announcement a LEFT JOIN a.contents c WHERE a.id = :id AND c.languageCode = :languageCode")
  Optional<AnnouncementDetailProjection> findAnnouncementDetailByIdAndLanguageCode(
      @Param("id") Long id, @Param("languageCode") String languageCode);

  @Query(
      "SELECT a FROM Announcement a LEFT JOIN FETCH a.contents c WHERE c.title LIKE %:title% ORDER BY a.createdAt DESC")
  List<Announcement> findByTitleLikeOrderByCreatedAtDesc(@Param("title") String title);

  @Query(
      "SELECT new com.youngcamp.server.dto.AnnouncementProjection(a.id, a.createdAt, a.isPinned, c.languageCode, c.title) "
          + "FROM Announcement a JOIN a.contents c WHERE LOWER(c.title) LIKE %:keyword% AND c.languageCode = :languageCode")
  List<AnnouncementProjection> findAllByKeywordAndLanguageCode(
      @Param("keyword") String keyword, @Param("languageCode") String languageCode);
}
