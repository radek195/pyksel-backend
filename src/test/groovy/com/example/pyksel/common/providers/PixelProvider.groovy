package com.example.pyksel.common.providers

import com.example.pyksel.infrastructure.persistence.pixel.Pixel
import com.example.pyksel.infrastructure.persistence.pixel.PixelId

import java.time.Instant

trait PixelProvider {
    def getPixel(
        def paintedBy = null,
        def id = new PixelId((short) 10, (short) 20),
        def color = "#FF0000",
        def paintedAt = Instant.now()
    ) {
        Pixel.builder()
            .id(id)
            .color(color)
            .paintedAt(paintedAt)
            .paintedBy(paintedBy)
            .build()
    }
}