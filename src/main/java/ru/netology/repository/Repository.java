package ru.netology.repository;

import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;

public interface Repository {
    public List<Post> all();

    public Post save(Post post);

    public Optional<Post> getById(long id);

    public void removeById(long id);
}
