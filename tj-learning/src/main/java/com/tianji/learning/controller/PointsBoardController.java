package com.tianji.learning.controller;


import com.tianji.learning.domain.query.PointsBoardQuery;
import com.tianji.learning.domain.vo.PointsBoardVO;
import com.tianji.learning.service.IPointsBoardSeasonService;
import com.tianji.learning.service.IPointsBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 学霸天梯榜 前端控制器
 * </p>
 *
 * @author liuyp
 * @since 2023-07-18
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "积分排行榜相关接口")
@RequestMapping("/boards")
public class PointsBoardController {
    private final IPointsBoardService pointsBoardService;

    @GetMapping
    @ApiOperation("查询学霸天梯榜")
    public PointsBoardVO queryPointsBoard(PointsBoardQuery query){
        return pointsBoardService.queryPointsBoard(query);
    }
}