package com.tianji.learning.controller;

import com.tianji.learning.domain.vo.SignResultVO;
import com.tianji.learning.service.ISignRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuyp
 * @since 2023/07/16
 */
@Api(tags = "签到相关功能接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sign-records")
public class SignRecordController {
    private final ISignRecordService signRecordService;

    @PostMapping
    @ApiOperation("用户签到")
    public SignResultVO addSignRecord(){
        return signRecordService.addSignRecord();
    }

    @GetMapping
    @ApiOperation("查询本月签到记录")
    public Byte[] querySignRecords(){
        return signRecordService.querySignRecords();
    }

}