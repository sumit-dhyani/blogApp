package com.example.blog.blogapp.serviceimpl;

import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.service.PostDataService;
import com.example.blog.blogapp.specification.PostSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.example.blog.blogapp.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PostDataServiceImpl implements PostDataService {
    @Autowired
    PostRepository postRepo;

    @Override
    public Page<Post> filteredPosts(String[] authorIds, String[] tagIds, String searchField,Pageable pagination) {
        List<Long> auId =  convertStringToList(authorIds);
        List<Long> tId=convertStringToList(tagIds);

        Specification<Post> spec=PostSpecification.getSpecs(auId, tId, searchField);
        return postRepo.findAll(spec,pagination);
    }
    public Page<Post> searchedPosts(String searchField, Pageable pagination){
        Specification<Post> spec=PostSpecification.getSearchSpecs(searchField);
        return postRepo.findAll(spec,pagination);
    }

    public Page<Post> postsBetweenDates(String startDate,String endDate,Pageable pagination){
        Specification<Post> spec=PostSpecification.getPostsBetweenDates(startDate,endDate);
        return postRepo.findAll(spec,pagination);
    }


    public Page<Post> getfilteredPosts(String[] authorIds, String[] tagIds,
                                       String searchField,Pageable pagination,String startDate,String endDate){
        List<Long> auId = new ArrayList<>();
        if(authorIds!=null) {
            for (String s : authorIds) {
                auId.add(Long.parseLong(s));
            }
        }
        else{
            auId=null;
        }
        List<Long> tId=new ArrayList<>();
        if(tagIds!=null) {
            for (String s : tagIds) {
                tId.add(Long.parseLong(s));
            }
        }
        else{
            tId=null;
        }

        Specification<Post> spec=PostSpecification.getFilteredPostsWithDate(auId,tId,searchField,startDate,endDate);
        return postRepo.findAll(spec,pagination);
    }
    private List<Long> convertStringToList(String[] authorIds){
        List<Long> auId=new ArrayList<>();
        if(authorIds!=null) {
            for (String s : authorIds) {
                auId.add(Long.parseLong(s));
            }
        }
        else{
            auId=null;
        }
       return auId;
    }

}
