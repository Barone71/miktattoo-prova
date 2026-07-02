package com.miktattooink.dto;

public record TattooWorkResponse(
        Long id,
        String title,
        String style,
        String imageUrl,
        String description
) {
}
