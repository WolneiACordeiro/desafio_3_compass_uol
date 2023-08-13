package com.compassuol.desafio3;

import com.compassuol.desafio3.entity.PostState;
import com.compassuol.desafio3.payload.ProcessingHistoryDto;
import com.compassuol.desafio3.service.CommentService;
import com.compassuol.desafio3.service.PostService;
import com.compassuol.desafio3.service.ProcessingHistoryService;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableJms
public class QueueConsumer {
    private final JmsTemplate jmsTemplate;
    private PostService postService;
    private CommentService commentService;
    private final ProcessingHistoryService processingHistoryService;

    public QueueConsumer(JmsTemplate jmsTemplate,
                         PostService postService,
                         CommentService commentService,
                         ProcessingHistoryService processingHistoryService) {
        this.jmsTemplate = jmsTemplate;
        this.postService = postService;
        this.commentService = commentService;
        this.processingHistoryService = processingHistoryService;
    }

    @JmsListener(destination = "CREATED")
    public void consumeFromQueueCreated(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.CREATED, processingHistoryDto);
        postService.createEmptyPost(postId).subscribe();
    }

    @JmsListener(destination = "POST_FIND")
    public void consumeFromQueuePostFind(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.POST_FIND, processingHistoryDto);
        postService.processBasedOnDataExistence(postId).subscribe();
    }

    @JmsListener(destination = "POST_OK")
    public void consumeFromQueuePostOk(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.POST_OK, processingHistoryDto);
        postService.createPost(postId).subscribe();
    }

    @JmsListener(destination = "COMMENTS_FIND")
    public void consumeFromQueueCommentsFind(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.COMMENTS_FIND, processingHistoryDto);
        commentService.processBasedOnDataExistence(postId).subscribe();
    }

    @JmsListener(destination = "COMMENTS_OK")
    public void consumeFromQueueCommentsOk(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.COMMENTS_OK, processingHistoryDto);
        commentService.createComment(postId).subscribe();
    }

    @JmsListener(destination = "ENABLED")
    public void consumeFromQueueEnabled(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.ENABLED, processingHistoryDto);
    }

    @JmsListener(destination = "DISABLED")
    public void consumeFromQueueDisabled(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.DISABLED, processingHistoryDto);
    }

    @JmsListener(destination = "FAILED")
    public void consumeFromQueueFailed(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.FAILED, processingHistoryDto);
        jmsTemplate.convertAndSend("DISABLED", postId);
    }

    @JmsListener(destination = "UPDATING")
    public void consumeFromQueueUpdating(Long postId) {
        ProcessingHistoryDto processingHistoryDto = new ProcessingHistoryDto();
        processingHistoryService.createProcessQueue(postId, PostState.UPDATING, processingHistoryDto);
        jmsTemplate.convertAndSend("POST_FIND", postId);
    }

}
