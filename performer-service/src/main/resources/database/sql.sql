CREATE TABLE IF NOT EXISTS user_info (
  `id` UInt64,
  `user_name` String,
  `pass_word` String,
  `phone` String,
  `create_day` Date DEFAULT CAST(now(),'Date')
)ENGINE = MergeTree
primary key (id)
order by (id);


INSERT INTO user_info
  (id,user_name,pass_word,phone)
VALUES
  (1,'xiaowang','123456','13325511231'),
  (2,'xiaoma','123456','13825511231'),
  (3,'xiaozhao','123456','18925511231');