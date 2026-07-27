CREATE TABLE pixels (
    x           SMALLINT        NOT NULL,
    y           SMALLINT        NOT NULL,
    color       VARCHAR(7)      NOT NULL DEFAULT '#FFFFFF',
    painted_by  UUID            REFERENCES users(id) ON DELETE SET NULL,
    painted_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (x, y)
);

CREATE TABLE user_cooldowns (
    user_id         UUID        PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    last_painted_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE pixel_history (
    id          BIGSERIAL       PRIMARY KEY,
    x           SMALLINT        NOT NULL,
    y           SMALLINT        NOT NULL,
    color       VARCHAR(7)      NOT NULL,
    painted_by  UUID            REFERENCES users(id) ON DELETE SET NULL,
    painted_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pixel_history_xy         ON pixel_history(x, y);
CREATE INDEX idx_pixel_history_painted_by ON pixel_history(painted_by);
CREATE INDEX idx_pixel_history_painted_at ON pixel_history(painted_at DESC);