package com.example.blog.blogapp.serviceimpl;import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.repository.TagRepository;
import com.example.blog.blogapp.service.TagService;

@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepo;
	@Autowired
	public TagServiceImpl(TagRepository tagRepo){
		this.tagRepo=tagRepo;
	}
	@Override
	public Tag getTagById(Long id) {
		return tagRepo.findById(id).orElseThrow(() -> new RuntimeException("Tag id is invalid."));
	}

	@Override
	public List<Tag> getLinkedTags() {
		return tagRepo.findAllLinkedTags();
	}

}
