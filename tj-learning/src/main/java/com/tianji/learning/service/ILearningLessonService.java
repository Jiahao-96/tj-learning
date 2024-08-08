package com.tianji.learning.service;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.vo.LearningLessonVO;

/**
 * <p>
 * 学生课程表 服务类
 * </p>
 *
 * @author jiahao
 * @since 2024-08-06
 */
public interface ILearningLessonService extends IService<LearningLesson> {

    PageDTO<LearningLessonVO> queryMyLessonsByPage(PageQuery query);

    LearningLessonVO queryMyCurrentLesson();

    void deleteExpireCourse(Long courseId);

    LearningLessonVO queryLearningLessonStatus(Long courseId);

    Long isLessonValid(Long courseId);

    /**
     * 创建学习计划
     * @param learningPlanDTO
     */
    void createLearningPlans(LearningPlanDTO learningPlanDTO);
}
