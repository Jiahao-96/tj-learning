package com.tianji.learning.service.impl;

import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.api.dto.leanring.LearningRecordDTO;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.dto.LearningRecordFormDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.enums.SectionType;
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
    private final CourseClient courseClient;
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

    /**
     * 存储学习记录
     *
     * @param learningRecordFormDTO
     */
    @Override
    public void addLearningRecord(LearningRecordFormDTO learningRecordFormDTO) {
        Long userId = UserContext.getUser();
        //要判断添加的是看视频的记录还是考试的记录
        boolean firstFinished;
        if(learningRecordFormDTO.getSectionType() == SectionType.VIDEO){
            firstFinished = handleVideoRecord(userId, learningRecordFormDTO);
        }else{
            firstFinished = handleExamRecord(userId, learningRecordFormDTO);
        }
        //统一更改lesson
        handleLearningLesson(firstFinished, learningRecordFormDTO);
    }

    private void handleLearningLesson(Boolean firstFinished, LearningRecordFormDTO learningRecordFormDTO){
        LearningLesson learningLesson = learningLessonService.getById(learningRecordFormDTO.getLessonId());
        if(learningLesson == null){
            throw new BizIllegalException("课表信息找不到，无法更新");
        }
        boolean allLearned = false;
        if(firstFinished){
            CourseFullInfoDTO course = courseClient.getCourseInfoById(learningLesson.getCourseId(),false,false);
            if(course == null){
                throw new BizIllegalException("找不到课程信息");
            }
            allLearned = learningLesson.getLearnedSections()+1 >= course.getSectionNum();
        }
    }


    //处理考试记录
    private boolean handleExamRecord(Long userId, LearningRecordFormDTO learningRecordFormDTO){
        LearningRecord record = BeanUtils.toBean(learningRecordFormDTO, LearningRecord.class);
        record.setUserId(userId);
        record.setFinished(true);
        record.setCreateTime(learningRecordFormDTO.getCommitTime());
        boolean success = save(record);
        if(!success){
            throw new BizIllegalException("新增考试类型的学习记录失败");
        }
        return true;
    }
    //处理视频记录
    private boolean handleVideoRecord(Long userId, LearningRecordFormDTO learningRecordFormDTO){
        //查询旧记录
        LearningRecord oldRecord = lambdaQuery().eq(LearningRecord::getLessonId, learningRecordFormDTO.getLessonId())
                .eq(LearningRecord::getSectionId, learningRecordFormDTO.getSectionId()).one();
        //之前没看过，那就需要新增
        if(oldRecord == null){
            oldRecord = BeanUtils.toBean(learningRecordFormDTO, LearningRecord.class);
            oldRecord.setUserId(userId);
            oldRecord.setFinished(false);
            boolean success = save(oldRecord);
            if(!success){
                throw new BizIllegalException("新增视频类型的学习记录失败");
            }
            return false;
        }
        boolean finishedNewSection = !oldRecord.getFinished() && learningRecordFormDTO.getMoment()*2>= learningRecordFormDTO.getDuration();
        //之前看过，这次看完了
        if(finishedNewSection){
            oldRecord.setFinished(true);
            oldRecord.setMoment(learningRecordFormDTO.getMoment());
            oldRecord.setFinishTime(learningRecordFormDTO.getCommitTime());
            boolean updated = updateById(oldRecord);
            if(!updated){
                throw new BizIllegalException("更新视频播放进度异常");
            }
            return true;
        }
        //之前看过，但是这次没看完
        oldRecord.setMoment(learningRecordFormDTO.getMoment());
        boolean success = updateById(oldRecord);
        if(!success){
            throw new BizIllegalException("更新视频播放进度异常");
        }
        return false;
    }
}
