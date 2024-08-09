package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.service.ILearningLessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 学生课程表 前端控制器
 * </p>
 *
 * @author jiahao
 * @since 2024-08-06
 */
@RestController
@RequestMapping("/lessons")
@Api(tags = "我的课程相关接口")
@RequiredArgsConstructor
public class LearningLessonController {
    private final ILearningLessonService learningLessonService;

    @GetMapping("/page")
    @ApiOperation("分页查询我的课表")
    public PageDTO<LearningLessonVO> queryMyLessonsByPage(PageQuery query){
        return learningLessonService.queryMyLessonsByPage(query);
    }

    @GetMapping("/now")
    @ApiOperation("查询最近学习的课程信息")
    public LearningLessonVO queryMyCurrentLesson(){
        return learningLessonService.queryMyCurrentLesson();
    }

    @DeleteMapping("/{courseId}")
    @ApiOperation("删除失效课程")
    public void DeleteExpireCourse(@ApiParam("课程id") @PathVariable Long courseId){
        learningLessonService.deleteExpireCourse(courseId);
    }

    @GetMapping("/{courseId}")
    @ApiOperation("查询指定课程的信息")
    public LearningLessonVO queryLearningLessonStatus(@ApiParam("课程id") @PathVariable Long courseId){
        return learningLessonService.queryLearningLessonStatus(courseId);
    }

    @GetMapping("/{courseId}/valid")
    @ApiOperation("检验用户能否学习当前课程")
    public Long isLessonValid(@ApiParam("课程id") @PathVariable("courseId") Long courseId){
        return learningLessonService.isLessonValid(courseId);
    }

    @PostMapping("/plans")
    @ApiOperation("/创建学习计划")
    public void createLearningPlans(@Validated @RequestBody LearningPlanDTO learningPlanDTO){
        learningLessonService.createLearningPlans(learningPlanDTO);
    }

    @GetMapping("/plans")
    @ApiOperation("查询我的学习计划(与进度)")
    public LearningPlanPageVO queryMyLearningPlans(PageQuery query){
        return learningLessonService.queryMyLearningPlans(query);
    }

}
















