# USER テーブルの作成

CREATE TABLE IF NOT EXISTS `user` (
  `id`         BIGINT(20)   UNSIGNED      NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(64)  CHARSET ASCII NOT NULL,
  `email`      VARCHAR(255) CHARSET ASCII NOT NULL UNIQUE,
  `hash`       VARCHAR(255)               NOT NULL,
  `updated_at` TIMESTAMP                  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` TIMESTAMP                  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# サンプルデータの挿入
INSERT INTO user (id, name, email, hash) VALUES (1, 'root', 'root@example.com', '$pbkdf2-sha512$790$zX46CGqdQgw4eQCy..YSy6YVEGWHUQdY41yZQmCejpY$TFnP2AqR1moc02vmiYwLIz0c5NNbhxL1fc5sKbv8ePI');

# POST テーブルの作成
CREATE TABLE if not exists `post` (
  `id`         BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`    BIGINT(20) UNSIGNED NOT NULL,
  `title`      VARCHAR(255)        NOT NULL,
  `body`       TEXT,
  `public`     BOOLEAN             NOT NULL DEFAULT FALSE,
  `updated_at` TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  # 主キーの設定
  PRIMARY KEY (`id`)
  # エンジンとして InnoDB を使う。文字コードとして utf8mb4 (4バイトの UTF-8 Unicode エンコーディング) を使う。
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# サンプルデータの挿入
INSERT INTO post (id, user_id, title, body, public) VALUES (1, 1, "IxiaS を用いたユーザー認証機能の実装", "ユーザー認証の実装頑張ります", FALSE);
INSERT INTO post (id, user_id, title, body, public) VALUES (2, 1, "エンティティーモデルについて", "エンティティーモデルとは云々", FALSE);
INSERT INTO post (id, user_id, title, body, public) VALUES (3, 1, "Slick を用いたデータベースアクセスについて", "Slick は難しい。", FALSE);

