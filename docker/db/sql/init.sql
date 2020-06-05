# POST テーブルの作成
CREATE TABLE if not exists `post` (
  `id`         BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
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
INSERT INTO post (id, title, body, public) VALUES (1, "IxiaS を用いたユーザー認証機能の実装", "ユーザー認証の実装頑張ります", FALSE);
INSERT INTO post (id, title, body, public) VALUES (2, "エンティティーモデルについて", "エンティティーモデルとは云々", FALSE);
INSERT INTO post (id, title, body, public) VALUES (3, "Slick を用いたデータベースアクセスについて", "Slick は難しい。", FALSE);
