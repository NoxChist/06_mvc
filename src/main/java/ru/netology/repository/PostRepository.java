package ru.netology.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository implements ru.netology.repository.Repository {
    private ConcurrentHashMap<Long, Post> postMap;
    private ConcurrentHashMap<Long, Post> deletedPosts;
    private AtomicLong cnt;

    @Autowired
    public PostRepository() {
        this(new ConcurrentHashMap<>());
    }

    public PostRepository(ConcurrentHashMap<Long, Post> postMap) {
        this.postMap = postMap;
        var maxId = all().stream().max(Comparator.comparingLong(Post::getId));
        cnt = maxId.isPresent() ? new AtomicLong(maxId.get().getId()) : new AtomicLong(0);
        deletedPosts = new ConcurrentHashMap<>();
    }

    @Override
    public List<Post> all() {
        return List.copyOf(postMap.values());
    }

    @Override
    public Optional<Post> getById(long id) {
        System.out.println("");
        return Optional.ofNullable(postMap.get(id));
    }

    @Override
    public Post save(Post post) {
        long id = post.getId();
        if (id == 0) {
            post.setId(cnt.incrementAndGet());
            postMap.put(post.getId(), post);
            return post;
        } else if (postMap.containsKey(id)) {
            postMap.put(post.getId(), post);
            return post;
        } else throw new NotFoundException(String.format("Post with id:%d was not found", post.getId()));
    }

    @Override
    public void removeById(long id) {
        if (getById(id).isPresent()) {
            var post = getById(id).get();
            deletedPosts.put(id, post);
            postMap.remove(id);
        } else throw new NotFoundException(String.format("Post with id:%d was not found", id));
    }
}
