package com.tianji.learning.controller;


import com.tianji.learning.domain.vo.PointsBoardSeasonVO;
import com.tianji.learning.service.IPointsBoardSeasonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuyp
 * @since 2023-07-16
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "积分赛季相关接口")
@RequestMapping("/boards/seasons")
public class PointsBoardSeasonController {

    private final IPointsBoardSeasonService pointsBoardSeasonService;

    @GetMapping("/list")
    @ApiOperation("查询赛季列表")
    public List<PointsBoardSeasonVO> queryPointBoardSeasons(){
        return pointsBoardSeasonService.queryPointBoardSeasons();
    }
}