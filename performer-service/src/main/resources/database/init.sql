CREATE TABLE IF NOT EXISTS user_info (
  `id` UInt64,
  `user_name` String,
  `pass_word` String,
  `phone` String,
  `create_day` Date DEFAULT CAST(now(),'Date')
)ENGINE = MergeTree
primary key (id)
order by (id);
