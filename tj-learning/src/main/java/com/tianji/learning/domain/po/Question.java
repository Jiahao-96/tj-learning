package com.tianji.learning.domain.po;

import com.tianji.learning.enums.QuestionStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author liuyp
 * @since 2023/12/11
 */
@Data
@Document(collection = "interaction_question")
@CompoundIndex(def = "{hidden:1, courseId:-1, sectionId:-1, userId:1, createTime:-1}", name = "user_search_idx")
@CompoundIndex(def = "{createTime:-1,status:1,courseId:1}", name = "admin_search_idx")
public class Question {
    /**id*/
    @Id
    private String id;

    /**问题的标题*/
    private String title;
    /**问题的内容*/
    private String description;

    /**课程id*/
    private Long courseId;
    /**章id*/
    private Long chapterId;
    /**小节id*/
    private Long sectionId;

    /**提问者*/
    private Long userId;

    /**最后一条回答*/
    private Reply latestAnswer;
    /**回答次数*/
    private Integer answerTimes;

    /**是否匿名*/
    private Boolean anonymity = false;
    /**是否隐藏*/
    private Boolean hidden = false;
    /**问题状态 0未查看，1已查看*/
    private QuestionStatus status = QuestionStatus.UN_CHECK;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
