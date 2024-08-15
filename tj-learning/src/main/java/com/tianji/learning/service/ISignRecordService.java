package com.tianji.learning.service;

import com.tianji.learning.domain.vo.SignResultVO;

/**
 * @author liuyp
 * @since 2023/07/16
 */
public interface ISignRecordService {
    /**
     * 用户签到
     * @return 签到结果
     */
    SignResultVO addSignRecord();

    /**
     * 查询当前用户的本月签到记录
     * @return 本月签到记录 [1,1,1,0,1,1,0,...]
     */
    Byte[] querySignRecords();
}