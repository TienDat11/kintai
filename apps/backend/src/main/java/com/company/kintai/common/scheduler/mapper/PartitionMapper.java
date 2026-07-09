package com.company.kintai.common.scheduler.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PartitionMapper {

    void ensureFuturePartitions(@Param("pMonthsAhead") int pMonthsAhead);

    Integer detachOldPartitions(@Param("pKeepMonths") int pKeepMonths);
}
