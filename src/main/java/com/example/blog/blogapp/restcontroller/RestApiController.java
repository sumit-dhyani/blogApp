package com.example.blog.blogapp.restcontroller;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.exceptions.ResourceNotFoundException;
import com.example.blog.blogapp.exceptions.UnauthorizedException;
import com.example.blog.blogapp.repository.PostRepository;
import com.example.blog.blogapp.serviceimpl.PostDataServiceImpl;
import com.example.blog.blogapp.serviceimpl.PostServiceImpl;
import com.example.blog.blogapp.serviceimpl.RestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RestApiController {
    public static final String LIMIT = "20";
    public static final String START_INDEX = "0";
    public static final String START = "start";
    public static final String LIMIT_PARAM = "limit";
    public static final String SEARCH = "search";
    public static final String AUTHOR_ID = "authorId";
    public static final String TAG_ID = "tagId";
    public static final String ORDER = "order";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";

    private final PostServiceImpl postService;
    PostRepository postRepo;

    private PostDataServiceImpl postDataService;
    RestServiceImpl restService;
    @Autowired
    public RestApiController(PostRepository postRepo, PostServiceImpl postService, RestServiceImpl restService,PostDataServiceImpl postDataService){
        this.postRepo=postRepo;
        this.postService=postService;
        this.restService=restService;
        this.postDataService=postDataService;
    }
    @GetMapping()
    public ResponseEntity<Page<Post>> getAllPosts(@RequestParam(value = START, required = false, defaultValue = START_INDEX) Integer start,
                                                  @RequestParam(value = LIMIT_PARAM, required = false, defaultValue = LIMIT) Integer limit,
                                                  @RequestParam(value = SEARCH, required = false) String searchField,
                                                  @RequestParam(value = AUTHOR_ID, required = false) String[] authorId,
                                                  @RequestParam(value = TAG_ID, required = false) String[] tagId,
                                                  @RequestParam(value = ORDER, required = false) String order,
                                                  @RequestParam(value = START_DATE, required = false) String startDate,
                                                  @RequestParam(value = END_DATE, required = false) String endDate){
        Page<Post> postList;
        Pageable pagination= order!=null
                ?order.equals("asc")
                    ?PageRequest.of(start/limit,limit, Sort.by("publishedAt").ascending())
                    :PageRequest.of(start/limit,limit, Sort.by("publishedAt").descending())
                :PageRequest.of(start/limit,limit);
        if(startDate!=null && endDate!=null && !startDate.isEmpty() && !startDate.isEmpty() && searchField!=null | authorId!=null | tagId!=null | order!=null){
            return new ResponseEntity<>(postDataService.
                    getfilteredPosts(authorId,tagId,searchField,pagination,startDate,endDate),HttpStatus.OK);
        }
        else if(startDate!=null && endDate!=null && !startDate.isEmpty() && !startDate.isEmpty()){
            postList=postDataService.postsBetweenDates(startDate,endDate,pagination);
            return new ResponseEntity<>(postList,HttpStatus.OK);
        }
        else if(searchField!=null | authorId!=null | tagId!=null | order!=null){
            postList=postDataService.filteredPosts(authorId,tagId,searchField,pagination);
            return new ResponseEntity<>(postList,HttpStatus.OK);
        }
        return new ResponseEntity<>(postDataService.findPublishedPosts(pagination),HttpStatus.OK);
    }
    @GetMapping("{id}")
    public Post getPostById(@PathVariable("id") long id){
        Optional<Post> postFound=postRepo.findById(id);
        if(!postFound.isPresent()){
            throw new ResourceNotFoundException("User not found");
        }
        return postFound.get();
    }

    @PostMapping("/add")
    public ResponseEntity<Post> addPost(@RequestBody Post post, Authentication authentication){
        return new ResponseEntity<>(restService.createPost(post,authentication),HttpStatus.CREATED);

    }
    @PutMapping("update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable("id") long id,@RequestBody Post post,Authentication authentication){
        Post existingPost=postService.returnBlog(id);
        if(authentication.getName().equals(existingPost.getUser().getEmail())|
                authentication.getAuthorities().equals(new SimpleGrantedAuthority("ADMIN"))){
            post.setId(id);
            postService.updatePost(post);
            return new ResponseEntity<>(postService.returnBlog(id),HttpStatus.OK);
        }
        else{
            throw new UnauthorizedException("Unauthorized");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deletePost(@PathVariable("id") long id,Authentication authentication){
        restService.deleteById(id,authentication);
        return new ResponseEntity<>("Post Deleted",HttpStatus.OK);
    }

    @GetMapping("/draft")
    public ResponseEntity<Page<Post>> getAllPosts(@RequestParam(value = START, required = false,defaultValue = START_INDEX) Integer start,
                                                  @RequestParam(value = LIMIT_PARAM, required = false,defaultValue = LIMIT) Integer limit,
                                                  @RequestParam(value = ORDER,required = false) String order){
        Pageable pagination= order!=null
                ?order.equals("asc")
                ?PageRequest.of(start/limit,limit, Sort.by("publishedAt").ascending())
                :PageRequest.of(start/limit,limit, Sort.by("publishedAt").descending())
                :PageRequest.of(start/limit,limit);
        Page<Post> postList=postDataService.findUnPublishedPosts(pagination);
        return new ResponseEntity<>(postList,HttpStatus.OK);
    }
}
