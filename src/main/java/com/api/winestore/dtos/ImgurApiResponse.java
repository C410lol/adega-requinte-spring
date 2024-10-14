package com.api.winestore.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ImgurApiResponse(
        Data data,
        boolean success,
        int status
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            String id,
            String deletehash,
            Integer account_id,
            String account_url,
            String ad_type,
            String ad_url,
            String title,
            String description,
            String name,
            String type,
            int width,
            int height,
            long size,
            int views,
            String section,
            String vote,
            long bandwidth,
            boolean animated,
            boolean favorite,
            boolean in_gallery,
            boolean in_most_viral,
            boolean has_sound,
            boolean is_ad,
            Boolean nsfw,
            String link,
            String[] tags,
            long datetime,
            String mp4,
            String hls
    ) {}
}
