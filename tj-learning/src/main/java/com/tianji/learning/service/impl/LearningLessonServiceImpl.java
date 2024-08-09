package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.tianji.common.utils.DateUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.domain.vo.LearningPlanVO;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.enums.PlanStatus;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final LearningRecordMapper recordMapper;
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

    /**
     * 创建学习计划
     * @param learningPlanDTO
     */
    @Override
    public void createLearningPlans(LearningPlanDTO learningPlanDTO) {
        Long userId = UserContext.getUser();
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId,userId)
                .eq(LearningLesson::getCourseId,learningPlanDTO.getCourseId())
                .one();
        if(lesson == null){
            throw new BadRequestException("课程信息不存在");
        }
        if(lesson.getStatus() == LessonStatus.EXPIRED.getValue()){
            throw new BadRequestException("课程已失效");
        }
        //更新学习计划
        LearningLesson newLesson = new LearningLesson();
        newLesson.setId(lesson.getId());
        newLesson.setWeekFreq(learningPlanDTO.getFreq());
        if(lesson.getPlanStatus() == PlanStatus.NO_PLAN.getValue()){
            newLesson.setPlanStatus(PlanStatus.PLAN_RUNNING.getValue());
        }
        updateById(newLesson);
    }


    @Override
    public LearningPlanPageVO queryMyLearningPlans(PageQuery query) {
        LearningPlanPageVO result = new LearningPlanPageVO();

        Long userId = UserContext.getUser();
        LocalDate now = LocalDate.now();
        LocalDateTime weekBegin = DateUtils.getWeekBeginTime(now);
        LocalDateTime weekEnd = DateUtils.getWeekEndTime(now);

        //1. 本周计划数据
        //1.1 总的计划学习数量
        Integer weekTotalPlan = baseMapper.countWeekTotalPlan(userId);
        result.setWeekTotalPlan(weekTotalPlan);
        //1.2 本周完成的小节数量
        LambdaQueryWrapper<LearningRecord> wrapper = Wrappers.<LearningRecord>lambdaQuery()
                .eq(LearningRecord::getUserId, userId)
                .eq(LearningRecord::getFinished, true)
                .between(LearningRecord::getFinishTime, weekBegin, weekEnd);
        List<LearningRecord> finishRecords = recordMapper.selectList(wrapper);
        result.setWeekFinished(finishRecords.size());

        //2. 分页查询课表信息：有学习计划的，未开始或学习中的课程
        Page<LearningLesson> page = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .in(LearningLesson::getStatus, LessonStatus.LEARNING, LessonStatus.NOT_BEGIN)
                .eq(LearningLesson::getPlanStatus, PlanStatus.PLAN_RUNNING)
                .page(query.toMpPage("latest_learn_time", false));
        List<LearningLesson> lessonList = page.getRecords();
        if (CollUtils.isEmpty(lessonList)) {
            return result;
        }

        //3. 转换vo列表
        //3.1 准备课程信息列表
        Set<Long> courseIdSet = lessonList.stream().map(LearningLesson::getCourseId).collect(Collectors.toSet());
        List<CourseSimpleInfoDTO> courseList = courseClient.getSimpleInfoList(courseIdSet);
        if (CollUtils.isEmpty(courseList)) {
            throw new BadRequestException("课程信息不存在");
        }
        Map<Long, CourseSimpleInfoDTO> courseMap = courseList.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        //3.2 准备每门课程 本周已学习章节数
        Map<Long, Long> weekLearnedMap = finishRecords.stream()
                .collect(Collectors.groupingBy(LearningRecord::getLessonId, Collectors.counting()));

        List<LearningPlanVO> voList = new ArrayList<>();
        for (LearningLesson lesson : lessonList) {
            LearningPlanVO vo = BeanUtils.toBean(lesson, LearningPlanVO.class);

            //课程名称，课程章节数量
            CourseSimpleInfoDTO course = courseMap.get(lesson.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getName());
                vo.setSections(course.getSectionNum());
            }
            //本周已学习章节数
            Long weekLearned = weekLearnedMap.getOrDefault(lesson.getId(), 0L);
            vo.setWeekLearnedSections(weekLearned.intValue());

            voList.add(vo);
        }

        return result.pageInfo(page.getTotal(), page.getPages(), voList);
    }
}
