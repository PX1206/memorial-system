-- 墓碑留言配图（纪念页带图评论，最多3张，存 JSON 数组）
ALTER TABLE `tomb_message`
  ADD COLUMN `image_urls` text NULL COMMENT '配图URL JSON数组，最多3张' AFTER `content`;
