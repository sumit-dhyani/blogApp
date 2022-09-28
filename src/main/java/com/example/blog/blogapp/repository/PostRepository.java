package com.example.blog.blogapp.repository;

import com.example.blog.blogapp.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

//	List<Post> findByTitleOrContentOrTagsOrAuthor(String keyword);

    Page<Post> findAllByIsPublishedTrue(Pageable paging);

    Page<Post> findAllByIsPublishedFalse(Pageable paging);

    @Query(value = "select * from post where is_published=true order by published_at asc",nativeQuery = true)
    Page<Post> findSortedPublishedPostAsc(Pageable paging);

    @Query(value = "select * from post where is_published=true order by published_at desc",nativeQuery = true)
    Page<Post> findSortedPublishedPostDesc(Pageable paging);


    @Query(value = "select distinct p.* from post p join post_tags q on "
            + "p.id=q.post_id where q.tag_id=(select t.id from tag t where "
            + "lower(t.name)=lower(:r)) or lower(p.title) LIKE lower(concat('%',:r,'%')) "
            + "or lower(p.content) Like lower(concat('%',:r,'%')) or lower(p.author) LIKE "
            + "lower(concat('%',:r,'%')) or lower(p.excerpt) LIKE lower(concat('%',:r,'%'))", nativeQuery = true)
    Page<Post> findByMultipleFieldsIgnoreCaseIn(@Param("r") String name, Pageable paging);

    @Query(value = "select distinct p.* from post p join post_tags q on "
            + "p.id=q.post_id where q.tag_id=(select t.id from tag t where "
            + "lower(t.name)=lower(:r)) or lower(p.title) LIKE lower(concat('%',:r,'%')) "
            + "or lower(p.content) Like lower(concat('%',:r,'%')) or lower(p.author) LIKE "
            + "lower(concat('%',:r,'%')) or lower(p.excerpt) LIKE lower(concat('%',:r,'%'))", nativeQuery = true)
    List<Post> findByMultipleFieldsIgnoreCaseIn(@Param("r") String name);

    @Query(value = "select p.* from post p join post_tags q "
            + "on p.id=q.post_id where q.tag_id=:tagId and p.user_id=:userId", nativeQuery = true)
    List<Post> findPostIfUserHasThatTag(@Param("tagId") Long tagId, @Param("userId") Long userId);

    @Query(value = "select * from post where id in (:ids)", nativeQuery = true)
    Page<Post> getResultsById(@Param("ids") List<Long> ids, Pageable paging);

    @Query(value = "select p.* from post p join post_tags q on p.id=q.post_id where q.tag_id=:tagId "
            + "and p.user_id=:userId and and p.published_at <=:dateStart and p.published_at::date >=:dateEnd", nativeQuery = true)
    List<Post> findPostIfUserHasThatTagAndDate(@Param("tagId") Long tagId, @Param("userId") Long userId,
                                               @Param("dateStart") LocalDate startDate, @Param("dateEnd") LocalDate endDate);

    @Query(value = "select p.* from post p where p.published_at <= :startDate and p.published_at >= :endDate", nativeQuery = true)
    List<Post> findPostBetweenStartAndDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    List<Post> findAllByPublishedAtLessThanEqualAndPublishedAtGreaterThanEqual(OffsetDateTime endDate, OffsetDateTime startDate);

    @Query(value = "select p.* from post p join post_tags q on p.id=q.post_id where q.tag_id in (:ids)",nativeQuery = true)
    Set<Post> finAllPostsByTag(@Param("ids") List<Long> tagIds);

    @Query(value = "select p.* from post p join post_tags q on p.id=q.post_id where q.tag_id in (:tagIds) and p.user_id in (:userIds)",nativeQuery = true)
    Set<Post> findPostsByUserAndTags(@Param("tagIds") List<Long> tagIds,@Param("userIds") List<Long> userIds);

    @Query(value = "select p.* from post p join post_tags q on p.id=q.post_id where q.tag_id in (:tagIds) and p.user_id in (:userIds) order by published_at :ord",nativeQuery = true)
    Page<Post> findPostsByUserAndTagsSorted(@Param("tagIds") List<Long> tagIds,
                                           @Param("userIds") List<Long> userIds,
                                           @Param("ord") String order,Pageable pagination);


    @Query(value = "select * from post p where user_id in (:userIds)", nativeQuery = true)
    Page<Post> findUsersWithIdSorted(@Param("userIds") List<Long> userIds,Pageable paging);


    @Query(value = "select p.* from post p join post_tags q on p.id=q.post_id where q.tag_id in (:tagIds)", nativeQuery = true)
    Page<Post> findPostsByTagSorted(@Param("tagIds") List<Long> tagIds,Pageable paging);































}
