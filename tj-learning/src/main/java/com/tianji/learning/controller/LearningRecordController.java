package com.tianji.learning.controller;


import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.learning.service.ILearningRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 学习记录表 前端控制器
 * </p>
 *
 * @author jiahao
 * @since 2024-08-08
 */
@RestController
@RequestMapping("/learning-records")
@Api(tags = "查询课程历史相关接口")
@RequiredArgsConstructor
public class LearningRecordController {
    private final ILearningRecordService learningRecordService;

    @GetMapping("/course/{courseId}")
    @ApiOperation("查询课程学习进度")
    public LearningLessonDTO queryLearningRecordByCourse(@ApiParam("课程id") @PathVariable("courseId")Long courseId){
        return learningRecordService.queryLearningRecordByCourse(courseId);
    }





}
