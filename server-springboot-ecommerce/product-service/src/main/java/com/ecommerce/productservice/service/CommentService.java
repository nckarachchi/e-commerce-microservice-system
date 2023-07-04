package com.ecommerce.productservice.service;


import com.ecommerce.common.model.UserCredential;
import com.ecommerce.productservice.dto.comment.CommentDto;
import com.ecommerce.productservice.dto.comment.CommentMapper;
import com.ecommerce.productservice.dto.comment.CreateCommentRequest;
import com.ecommerce.productservice.model.Comment;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProductService productService;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CreateCommentRequest createCommentDto){
        Product product = productService.getProductById(createCommentDto.getProductId());
        UserCredential userCredential = (UserCredential) SecurityContextHolder.getContext()
                .getAuthentication().getCredentials();

        Comment comment = Comment.builder()
                .product(product)
                .text(createCommentDto.getText())
                .creator(userCredential.getUsername())
                .build();

        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

}
