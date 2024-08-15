package com.tianji.learning.domain.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author liuyp
 * @since 2023/12/12
 */
@Data
@Document(collection = "interaction_reply")
@CompoundIndex(def = "{questionId:-1, answerId:1, hidden:1, likedTimes:-1, createTime:1}", name = "answer_idx")
@CompoundIndex(def = "{answerId:-1, hidden:1, createTime:1}", name = "comment_idx")
public class Reply {
    /**主键id*/
    @Id
    private String id;

    /**问题id*/
    private String questionId;
    /**回答id*/
    private String answerId;

    /**回复人的id*/
    private Long userId;
    /**回复的内容*/
    private String content;
    /**回复的目标对象id*/
    private String targetReplyId;
    /**回复的目标人id*/
    private Long targetUserId;

    /**本条回复的评论次数*/
    private Integer replyTimes = 0;
    /**本条回复的点赞次数*/
    private Integer likedTimes = 0;

    /**是否隐藏*/
    private Boolean hidden = false;
    /**是否匿名*/
    private Boolean anonymity = false;

    /**创建时间*/
    private LocalDateTime createTime;
    /**修改时间*/
    private LocalDateTime updateTime;
}
