package com.miktattooink.model;

public class TattooWork {
    private final Long id;
    private final String title;
    private final String style;
    private final String imageUrl;
    private final String description;

    public TattooWork(Long id, String title, String style, String imageUrl, String description) {
        this.id = id;
        this.title = title;
        this.style = style;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStyle() {
        return style;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
