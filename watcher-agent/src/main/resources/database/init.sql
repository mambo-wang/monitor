CREATE TABLE IF NOT EXISTS awesome_metrics (
  `platform` String,
  `traceId` String,
  `metric` String,
  `batchNum` String,
  `tags` String,
  `value` DECIMAL,
  `createTime` DateTime DEFAULT now(),
)ENGINE = MergeTree
order by (createTime);

