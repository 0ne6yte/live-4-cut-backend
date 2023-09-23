package com.onebyte.life4cut.picture.domain;


import com.onebyte.life4cut.album.domain.Album;
import com.onebyte.life4cut.album.domain.Slot;
import com.onebyte.life4cut.common.entity.BaseEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_picture_1", columnList = "userId"),
                @Index(name = "idx_picture_2", columnList = "albumId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Picture extends BaseEntity {

    @Nonnull
    @Column(nullable = false)
    private Long userId;

    @Nonnull
    @Column(nullable = false)
    private Long albumId;

    @Nonnull
    @Column(nullable = false)
    private String path;

    @Nonnull
    @Column(nullable = false)
    private String content;

    @Nonnull
    @Column(nullable = false)
    private LocalDateTime picturedAt;

    @Nullable
    @Column
    private LocalDateTime deletedAt;

    @Nonnull
    public static Picture create(@Nonnull Long userId, @Nonnull Long albumId, @Nonnull String path, @Nonnull String content, @Nonnull LocalDateTime picturedAt) {
        Picture picture = new Picture();
        picture.userId = userId;
        picture.albumId = albumId;
        picture.path = path;
        picture.content = content;
        picture.picturedAt = picturedAt;
        return picture;
    }
}
