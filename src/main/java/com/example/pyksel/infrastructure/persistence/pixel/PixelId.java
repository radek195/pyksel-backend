package com.example.pyksel.infrastructure.persistence.pixel;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixelId implements Serializable {
    private Short x;
    private Short y;
}