package com.atm.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

/*
    By extending the base entity or intermediate one,
    fields can be diver from one to another. If a field
    is needed in multiple entities just not all, u can
    use this approach to vary them.
 */
@Data
@MappedSuperclass
public class IntermidateBaseEntity extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String slug;
}
