package com.tianji.learning.service.impl;

import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.api.dto.leanring.LearningRecordDTO;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import com.tianji.learning.service.ILearningRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 学习记录表 服务实现类
 * </p>
 *
 * @author jiahao
 * @since 2024-08-08
 */
@Service
@RequiredArgsConstructor
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements ILearningRecordService {
    private final ILearningLessonService learningLessonService;
    /**
     * 根据课程查课程学习记录
     *
     * @param courseId
     * @return
     */
    @Override
    public LearningLessonDTO queryLearningRecordByCourse(Long courseId) {
        LearningLessonDTO learningLessonDTO = new LearningLessonDTO();
        //设置查找课表id和最近学习的小节id
        Long userId = UserContext.getUser();
        LearningLesson lesson = learningLessonService.lambdaQuery()
                .eq(LearningLesson::getUserId,userId)
                .eq(LearningLesson::getCourseId,courseId)
                .ne(LearningLesson::getStatus, LessonStatus.EXPIRED.getValue())
                .one();
        if(lesson == null){
            throw new BadRequestException("课程信息不存在");
        }
        learningLessonDTO.setId(lesson.getId());
        learningLessonDTO.setLatestSectionId(lesson.getLatestSectionId());
        //设置学习过的小节的记录
        List<LearningRecord> recordList = lambdaQuery()
                .eq(LearningRecord::getLessonId,lesson.getId())
                .eq(LearningRecord::getUserId,lesson.getUserId())
                .list();
        List<LearningRecordDTO> recordDTOList = BeanUtils.copyList(recordList,LearningRecordDTO.class);
        learningLessonDTO.setRecords(recordDTOList);
        return learningLessonDTO;
    }
}
