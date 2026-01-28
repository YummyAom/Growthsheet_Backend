package com.growthsheet.product_service.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "sheets")
public class Sheet {

    @Id
    @GeneratedValue
    private UUID id;

    // ===== FK (logical) =====
    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // ===== sheet info =====
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    private String faculty;
    
    @Column(nullable = false)
    private Integer studyYear;
    
    @Column(nullable = false)
    private String academicYear;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private String fileUrl;

    private String previewUrl;
    private Integer pageCount;

    // ===== admin =====
    @Enumerated(EnumType.STRING)
    private SheetStatus status = SheetStatus.PENDING;

    private String adminNote;
    private Boolean isPublished = true;

    // ===== hashtags =====
    @ManyToMany
    @JoinTable(name = "sheet_hashtags", joinColumns = @JoinColumn(name = "sheet_id"), inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
    private Set<Hashtag> hashtags = new HashSet<>();

    // ===== getters / setters =====
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public SheetStatus getStatus() {
        return status;
    }

    public void setStatus(SheetStatus status) {
        this.status = status;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public Category getCategory() {
        return category;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDescription() {
        return description;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public Integer getStudyYear() {
        return studyYear;
    }

    public University getUniversity() {
        return university;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public void setUniversity(University university) {
        this.university = university;
    }
}
