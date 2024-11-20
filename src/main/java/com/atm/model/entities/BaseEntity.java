package com.atm.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
@MappedSuperclass
@Access(AccessType.FIELD)
// Auditing
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"created_date,update_date"},allowGetters = true)
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_by", nullable = false, length = 60)
    @CreatedBy
    private String createdBy;
    @Column(name = "created_date", nullable = false, length = 60)
    @CreationTimestamp
    private LocalDateTime createdDate;
    @Column(name = "updated_by", nullable = false, length = 60)
    @LastModifiedBy
    private String updatedBy;
    @Column(name = "updated_date", nullable = false, length = 60)
    @LastModifiedDate
    private LocalDateTime updatedDate;
    @Column(name="system_date", nullable = false, length = 60)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime date;
}
