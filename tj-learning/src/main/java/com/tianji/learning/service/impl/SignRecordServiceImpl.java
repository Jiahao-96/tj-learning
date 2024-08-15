package com.tianji.learning.service.impl;

import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.DateUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.vo.SignResultVO;
import com.tianji.learning.service.ISignRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author liuyp
 * @since 2023/07/16
 */
@Service
@RequiredArgsConstructor
public class SignRecordServiceImpl implements ISignRecordService {
    private final StringRedisTemplate redisTemplate;

    @Override
    public SignResultVO addSignRecord() {
        SignResultVO vo = new SignResultVO();
        Long userId = UserContext.getUser();
        LocalDate now = LocalDate.now();

        //保存签到信息
        String key = "sign:uid:" + userId + now.format(DateUtils.SIGN_DATE_SUFFIX_FORMATTER);
        long offset = now.getDayOfMonth() - 1;
        Boolean exist = redisTemplate.opsForValue().setBit(key, offset, true);
        if (Boolean.TRUE.equals(exist)) {
            throw new BizIllegalException("请不要重复签到");
        }

        //统计连续签到天数：获取本月开始到今天的签到数据
        int days = 0;
        List<Long> result = redisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create().get(
                        BitFieldSubCommands.BitFieldType.unsigned((int) (offset + 1))
                ).valueAt(0)
        );
        if (CollUtils.isNotEmpty(result)) {
            //  获取签到结果。例如：981，那么签到结果是 1111010101
            int signData = result.get(0).intValue();
            while ((signData & 1) == 1) {
                days++;
                signData>>>=1;
            }
        }

        //计算连续签到的积分 todo
        int rewardPoints = 0;
        if (days == 7) {
            rewardPoints = 10;
        } else if (days == 14) {
            rewardPoints = 20;
        } else if (days == 28) {
            rewardPoints = 40;
        }
        //保存积分明细记录 todo

        //封装结果
        vo.setSignDays(days);
        vo.setRewardPoints(rewardPoints);
        return vo;
    }

    @Override
    public Byte[] querySignRecords() {
        LocalDate today = LocalDate.now();
        int dayOfMonth = today.getDayOfMonth();

        //查询当前用户的本月打卡记录
        String key = "sign:uid:" + UserContext.getUser() + today.format(DateUtils.SIGN_DATE_SUFFIX_FORMATTER);
        List<Long> results = redisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create().get(
                        BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)
                ).valueAt(0)
        );
        if (CollUtils.isEmpty(results)) {
            return new Byte[0];
        }

        //转换成[1,1,1,0,1,1,0,..]格式的数组
        int score = results.get(0).intValue();
        Byte[] array = new Byte[dayOfMonth];
        for (int i = dayOfMonth-1; i >= 0; i--) {
            array[i] =  Byte.valueOf((byte) (score & 1));
            score>>>=1;
        }

        return array;
    }



}