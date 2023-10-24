package com.practic.back.testtz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_audit")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "operation_type")
    private String operationType;
    @Column(name = "new_account_json")
    private String newAccountJson;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    @Column(name = "changed_account_id")
    private UUID changedAccountId;
}
