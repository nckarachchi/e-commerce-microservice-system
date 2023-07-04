package com.ecommerce.productservice.dto.comment;

import com.ecommerce.productservice.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDto commentToCommentDto(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .createdDate(comment.getCreatedDate())
                .createdBy(comment.getCreatedBy())
                .text(comment.getText())
                .creator(comment.getCreator())
                .build();
    }
}
