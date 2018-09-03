package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
public class PostController {

    @Autowired
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public class PostWithComments {
        private Post post;
        private Set<Comment> commentSet;

        public Post getPost() {
            return post;
        }

        void setPost(Post post) {
            this.post = post;
        }

        public Set<Comment> getCommentSet() {
            return commentSet;
        }

        void setCommentSet(Set<Comment> commentSet) {
            this.commentSet = commentSet;
        }
    }

    private final
    PostRepository postRepository;

    @GetMapping("/posts")
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @GetMapping("/posts/{postId}")
    public PostWithComments getPostById(
            @PathVariable("postId") Long postId
    ) {
        return postRepository.findById(postId).map(post -> {
            Set<Comment> commentSet = post.getComments();
            PostWithComments pwc = new PostWithComments();
            pwc.setPost(post);
            pwc.setCommentSet(commentSet);
            return pwc;
        }).orElseThrow(() -> new ResourceNotFoundException("PostId" + postId + "not found"));
    }

    @PostMapping("/posts")
    public Post createPost(@Valid @RequestBody Post post) {
        return postRepository.save(post);
    }

    @PutMapping("/posts/{postId}")
    public Post updatePost(@PathVariable("postId") Long postId, @Valid @RequestBody Post postRequest) {
        return postRepository.findById(postId).map(post -> {
            post.setTitle(postRequest.getTitle());
            post.setDescription(postRequest.getTitle());
            post.setContent(postRequest.getContent());
            return postRepository.save(post);
        }).orElseThrow(() -> new ResourceNotFoundException("PostId" + postId + "not found"));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        return postRepository.findById(postId).map(post -> {
            postRepository.delete(post);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + postId + " not found"));
    }
}
