package com.example.blog.blogapp.specification;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.entity.User;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Join;
import java.util.List;

public class PostSpecification {
    public static Specification<Post> getSpecs(List<Long> authorIds, List<Long> tagIds, String searchField) {
        Specification<Post> specs=null;
        if(tagIds!=null &&authorIds!=null&&searchField!=null){
            specs=getSearchSpecs(searchField).and(getPostsByTagIds(tagIds).and(getPostsByAuthorIds(authorIds)));
        }
       else if(tagIds!=null&&authorIds!=null){
            specs=getPostsByTagIds(tagIds).and(getPostsByAuthorIds(authorIds));
        }
        else if(authorIds!=null&&searchField!=null){
            specs=getSearchSpecs(searchField).and(
                    getPostsByAuthorIds(authorIds));

        }
        else if(tagIds!=null&&searchField!=null){
            specs=getSearchSpecs(searchField).and(getPostsByTagIds(tagIds));

        }
        else if(authorIds!=null){
            specs=getPostsByAuthorIds(authorIds);

        }
        else if(tagIds!=null){
            specs=getPostsByTagIds(tagIds);

        }
        return specs!=null?specs.and(getIsPublished()):specs;    }

    public static Specification<Post> getSearchSpecs(String searchField){
         Specification<Post> specs=getSearchedPosts("author",searchField)
                .or(getSearchedPosts("title",searchField))
                .or(getSearchedPosts("content",searchField))
                .or(getSearchedPosts("excerpt",searchField))
        .or(getSearchedPostsByTag(searchField)).and((root, query, criteriaBuilder) -> {
            query.distinct(true);
            return null;
                 });
         return specs.and(getIsPublished());
    }

    private static Specification<Post> getPostsByAuthorIds(List<Long> authorIds) {
        return ((root, query, criteriaBuilder) -> {
            Join<Post, User> authorPosts=root.join("user");
            return criteriaBuilder.in(authorPosts.get("id")).value(
                    authorIds);
        });
    }
    private static Specification<Post> getPostsByTagIds(List<Long> tagIds) {
        return ((root, query, criteriaBuilder) -> {
            Join<Post, Tag> authorPosts=root.join("tags");
            return criteriaBuilder.in(authorPosts.get("id")).value(tagIds);
        });
    }
    private static Specification<Post> getIsPublished(){
        return((root, query, criteriaBuilder) -> {
            return criteriaBuilder.isTrue(root.get("isPublished"));
        });
    }
    public static Specification<Post> getPostsBetweenDates(String startDate, String endDate) {
        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("publishedAt"),startDate,endDate);
        });
    }
    private static Specification<Post> getSearchedPosts(String column,String searchField){
        return (((root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(column)),"%"+searchField.toLowerCase()+"%");
        }));
    }
    private static Specification<Post> getSearchedPostsByTag(String searchField){
        return (((root, query, criteriaBuilder) -> {
            Join<Post,Tag> postTags=root.join("tags");
            return criteriaBuilder.like(criteriaBuilder.lower(postTags.get("name")),"%"+searchField.toLowerCase()+"%");
        }));
    }
}
