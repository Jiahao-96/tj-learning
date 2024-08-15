package com.tianji.learning.mq;

import com.tianji.common.constants.MqConstants;
import com.tianji.learning.domain.dto.SignInMessageDTO;
import com.tianji.learning.enums.PointsRecordType;
import com.tianji.learning.service.IPointsRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author liuyp
 * @since 2023/07/16
 */
@Component
@RequiredArgsConstructor
public class LearningPointsChangeListener {
    private final IPointsRecordService pointsRecordService;

    /**
     * 处理签到积分（每日签到得积分，需要签到时计算好连续签到的积分值，加上当天签到积分值，把最终结果发送过来）
     * @param message 积分消息(用户id、当天签到的积分值)
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sign.points.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.SIGN_IN
    ))
    public void listenSignPoint(SignInMessageDTO message) {
        pointsRecordService.addPointRecord( message.getUserId(),message.getPoints(),PointsRecordType.SIGN);
    }

    /**
     * 处理学习新小节的积分（学习一个新小节，积10分，每日上限50）todo
     * @param userId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "learning.points.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.LEARN_SECTION
    ))
    public void listenLearnSection(Long userId){
		//todo
    }

    /**
     * 处理回答问题的积分（回答一次积5分，每日上限20分；对别人的回答做评价是没有积分的）todo
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "reply.points.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.WRITE_REPLY
    ))
    public void listenWriteReply(Long userId){
		//todo
    }

    /**
     * 处理写笔记的积分（写笔记一次积3分，和“笔记被采集”一起每日上限20）todo
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "note.new.points.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.WRITE_NOTE
    ))
    public void listenWriteNote(Long userId){
		//todo
    }

    /**
     * 处理笔记被采集的积分（笔记被采集一次积2分，和“写笔记”一起每日上限20）todo
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "note.gathered.points.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.NOTE_GATHERED
    ))
    public void listenNoteGathered(Long userId){
		//todo
    }
}