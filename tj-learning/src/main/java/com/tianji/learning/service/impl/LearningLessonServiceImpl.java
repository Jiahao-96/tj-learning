package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.service.ILearningLessonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生课程表 服务实现类
 * </p>
 *
 * @author jiahao
 * @since 2024-08-06
 */
@Service
@RequiredArgsConstructor
public class LearningLessonServiceImpl extends ServiceImpl<LearningLessonMapper, LearningLesson> implements ILearningLessonService {
    private final CourseClient courseClient;
    @Override
    public PageDTO<LearningLessonVO> queryMyLessonsByPage(PageQuery query) {
        Long userId = UserContext.getUser();
        //分页查询当前用户的课程列表,page这个是查询出来的页码数据
        Page<LearningLesson> page = lambdaQuery()
                .eq(LearningLesson::getUserId,userId)
                .page(query.toMpPage("latest_learn_time", false));
        //这里拿到的是List<LearningLesson>,要变成List<LearningLessonVO>
        List<LearningLesson> lessonList = page.getRecords();
        if(CollUtils.isEmpty(lessonList)){
            return PageDTO.empty(page);
        }
        //准备课程信息，通过feign远程调用tj-course服务
        Set<Long> courseIdSet = lessonList.stream().map(LearningLesson::getCourseId).collect(Collectors.toSet());
        List<CourseSimpleInfoDTO> courseList = courseClient.getSimpleInfoList(courseIdSet);
        if(CollUtils.isEmpty(courseList)){
            throw new BadRequestException("课程信息不存在");
        }
        Map<Long, CourseSimpleInfoDTO> courseMap = courseList.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, Function.identity()));
        //封装结果
        List<LearningLessonVO> voList = new ArrayList<>();
        for(LearningLesson lesson: lessonList){
            //拷贝基础属性
            LearningLessonVO vo = BeanUtils.toBean(lesson, LearningLessonVO.class);
            //封装课程信息
            CourseSimpleInfoDTO course = courseMap.get(lesson.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getName());
                vo.setCourseCoverUrl(course.getCoverUrl());
                vo.setSections(course.getSectionNum());
            }
            voList.add(vo);
        }
        return PageDTO.of(page,voList);
    }
}
