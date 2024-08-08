package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.api.client.course.CatalogueClient;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CataSimpleInfoDTO;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.enums.LessonStatus;
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
    private final CatalogueClient catalogueClient;
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

    @Override
    public LearningLessonVO queryMyCurrentLesson() {
        Long userId = UserContext.getUser();
        //已经知道用户了，查出用户最近的课
        LearningLesson latestLearningLesson = lambdaQuery().eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getStatus, LessonStatus.LEARNING)
                .orderByDesc(LearningLesson::getLatestLearnTime)
                .last("limit 1")
                .one();
        if(latestLearningLesson == null){
            return null;
        }
        //查出来了课、用户关系以后，找到课并且封装返回
        LearningLessonVO vo = BeanUtils.toBean(latestLearningLesson, LearningLessonVO.class);
        CourseFullInfoDTO course = courseClient.getCourseInfoById(latestLearningLesson.getCourseId(),false,false);
        if(course == null){
            throw new BadRequestException("课程不存在");
        }
        vo.setCourseName(course.getName());
        vo.setCourseCoverUrl(course.getCoverUrl());
        vo.setSections(course.getSectionNum());
        //开始封装小节信息
        List<CataSimpleInfoDTO> cataList = catalogueClient.batchQueryCatalogue(CollUtils.singletonList(latestLearningLesson.getLatestSectionId()));
        if(CollUtils.isNotEmpty(cataList)){
            CataSimpleInfoDTO cata = cataList.get(0);
            vo.setLatestSectionName(cata.getName());
            vo.setLatestSectionIndex(cata.getCIndex());
        }
        return vo;
    }

    @Override
    public void deleteExpireCourse(Long courseId) {
        Long userId = UserContext.getUser();
        remove(Wrappers.<LearningLesson>lambdaQuery().eq(LearningLesson::getUserId,userId)
                .eq(LearningLesson::getCourseId,courseId));
    }

    @Override
    public LearningLessonVO queryLearningLessonStatus(Long courseId) {
        Long userId = UserContext.getUser();
        //查询出用户的选课信息
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getCourseId, courseId)
                .eq(LearningLesson::getUserId, userId)
                .one();
        if (lesson == null) {
            return null;
        }
        //封装vo
        LearningLessonVO vo = BeanUtils.toBean(lesson, LearningLessonVO.class);

        CourseFullInfoDTO course = courseClient.getCourseInfoById(lesson.getCourseId(), false, false);
        if (course == null) {
            throw new BadRequestException("课程不存在");
        }
        vo.setCourseName(course.getName());
        vo.setCourseCoverUrl(course.getCoverUrl());
        vo.setSections(course.getSectionNum());

        return vo;
    }

    @Override
    public Long isLessonValid(Long courseId) {
        Long userId = UserContext.getUser();
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId,userId)
                .eq(LearningLesson::getCourseId, courseId)
                .last("limit 1")
                .one();
        if(lesson == null || lesson.getStatus() == LessonStatus.EXPIRED.getValue()){
            log.error("课程已过期");
        }
        return lesson.getId();
    }


}
