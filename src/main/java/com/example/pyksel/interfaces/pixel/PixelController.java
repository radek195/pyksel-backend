package com.example.pyksel.interfaces.pixel;

import com.example.pyksel.domian.pixel.PaintPixelService;
import com.example.pyksel.infrastructure.persistence.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pixels")
@RequiredArgsConstructor
public class PixelController {

    private final PaintPixelService paintPixelService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void paint(@AuthenticationPrincipal User user, @RequestBody @Valid PaintPixelRequest request) {
        paintPixelService.paint(user.getId(), request.x(), request.y(), request.color());
    }
}
