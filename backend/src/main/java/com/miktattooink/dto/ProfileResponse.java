package com.miktattooink.dto;

import java.util.List;

public record ProfileResponse(
        String name,
        String imageUrl,
        List<String> bio,
        String email,
        String phone,
        String location,
        List<SocialLinkResponse> socials
) {
}
