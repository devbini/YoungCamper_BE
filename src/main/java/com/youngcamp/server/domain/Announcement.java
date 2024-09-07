package com.youngcamp.server.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Announcement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Boolean isPinned;

  @Column(nullable = true)
  private String imageUrl;

  @Column(nullable = true)
  private String fileUrl;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Getter
  @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<AnnouncementContents> contents = new ArrayList<>();

  @Setter @Transient private AnnouncementContents filteredContent;

  public void addContents(List<AnnouncementContents> contents) {
    if (this.contents == null) {
      this.contents = new ArrayList<>();
    }
    for (AnnouncementContents content : contents) {
      content.setAnnouncement(this); // 양방향 관계 설정
    }
    this.contents.addAll(contents);
  }

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // 업데이트될 때 updatedAt을 현재 시각으로 변경
  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public void clearContents() {
    this.contents.clear();
  }
}
