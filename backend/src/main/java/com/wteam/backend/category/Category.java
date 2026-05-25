package com.wteam.backend.category;

import com.wteam.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.wteam.backend.common.constants.ValidationConstants.Category.*;

@Entity
@Table(name = "categories")
@SuperBuilder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Category extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Category parent;

    @Column(name = "name", length = NAME_MAX_LENGTH, nullable = false)
    private String name;

    @Column(name = "slug", length = SLUG_MAX_LENGTH, unique = true, nullable = false)
    private String slug;

    @Column(name = "icon_url", columnDefinition = "TEXT")
    private String iconUrl;
}
