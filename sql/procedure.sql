-- 定义储存过程（先插记录表，再减库存）
-- in输入参数, out输出参数
-- row_count() 返回上一条修改类型sql(delete,insert,update)的影响行数, 0-未修改数据、>0-影响行数、<0-sql错误
-- r_result返回值：-2:sql错误, -1:重复秒杀, 0:秒杀结束, 1:秒杀成功
CREATE PROCEDURE seckill.execute_seckill(IN v_id BIGINT, IN v_phone BIGINT, IN v_kill_time  TIMESTAMP, OUT r_result INT)
BEGIN
  DECLARE insert_count INT DEFAULT 0;
  START TRANSACTION;
  INSERT IGNORE INTO sk_record(skproduct_id, user_phone, state, create_time) VALUES (v_id, v_phone, 0, v_kill_time);
  SELECT row_count() INTO insert_count;
  IF (insert_count = 0)
    THEN ROLLBACK;
    SET r_result = -1;
  ELSEIF (insert_count < 0)
    THEN ROLLBACK;
    SET r_result = -2;
  ELSE
    UPDATE sk_product SET number = number - 1 WHERE id = v_id AND end_time > v_kill_time AND start_time < v_kill_time AND number > 0;
    SELECT row_count() INTO insert_count;
    IF (insert_count = 0)
      THEN ROLLBACK;
      SET r_result = 0;
    ELSEIF (insert_count < 0)
      THEN ROLLBACK;
      SET r_result = -2;
    ELSE
      COMMIT;
      SET r_result = 1;
    END IF;
  END IF;
END;