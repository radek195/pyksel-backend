package com.example.pyksel.interfaces.pixel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaintPixelRequest(
        @NotNull Short x,
        @NotNull Short y,
        @NotBlank @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String color
) {
}
