package com.ecommerce.common.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

  
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters & setters
    
    public Long getId() {
  		return id;
  	}

  	public void setId(Long id) {
  		this.id = id;
  	}

  	public LocalDateTime getCreatedAt() {
  		return createdAt;
  	}

  	public void setCreatedAt(LocalDateTime createdAt) {
  		this.createdAt = createdAt;
  	}

  	public LocalDateTime getUpdatedAt() {
  		return updatedAt;
  	}

  	public void setUpdatedAt(LocalDateTime updatedAt) {
  		this.updatedAt = updatedAt;
  	}

}
