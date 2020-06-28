package com.example.technology_forum.service;

import com.example.technology_forum.dao.commentDao;
import com.example.technology_forum.model.Blog;
import com.example.technology_forum.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class commentJDBCService implements commentService {

    @Resource
    commentDao commentDao;

    @Resource
    blogService blogService;

    @Override
    public void writeComment(Comment comment) {
        Blog blog = new Blog();
        blog.setBlog_id(comment.getBlog_id());
        Blog blog1 = blogService.getBlogDetail(blog);//为了获取写博客人的id
        if(comment.getAuthor_id()==blog1.getU_id()){//说明该用户是层主
            comment.setStory_master(comment.getU_id());
            try {
                comment.setDepth(commentDao.getDepth(comment)+1);//当前层数+1
            }catch(EmptyResultDataAccessException e){
                comment.setDepth(1);
            }
            comment.setIs_master(true);
            comment.setRoom(1);//层主的房间号为1
        }
        else{//不为层主，需要额外参数层主评论的id
            Comment comment1 = new Comment();
            comment1.setComment_id(comment.getMaster_comment_id());//填上层主评论的id
            Comment master_comment = commentDao.getCommentById(comment1);//获取层主的评论
            comment.setStory_master(master_comment.getStory_master());//设置层主
            comment.setDepth(master_comment.getDepth());//层主才知道层数
            comment.setIs_master(false);
            try{
                comment.setRoom(commentDao.getRoom(comment)+1);//房间号为当前最大房间号+1
            }catch (EmptyResultDataAccessException e){
                comment.setRoom(2);
            }
        }
        log.info("{commentService-writeComment-comment}:"+comment);
        commentDao.writeComment(comment);
    }

    @Override
    public boolean deleteComment(Comment comment) {
        try{
            Comment comment1 = commentDao.getCommentById(comment);
            boolean is_master = comment1.getIs_master();
            if(is_master){//是层主，删除时连同子评论一起删除
                List<Comment> child_comment = commentDao.getCommonCommentInfo(comment1);
                for(Comment comment2:child_comment){
                    commentDao.deleteComment(comment2);//删除子评论
                }
                commentDao.deleteComment(comment1);//删除层主评论
            }
            else{//只删除子评论
                commentDao.deleteComment(comment1);
            }
        }catch(EmptyResultDataAccessException e){
            return false;//没有此评论
        }
        return true;
    }

    @Override
    public boolean changeLike(Comment comment) {
        Comment comment1 = commentDao.getCommentById(comment);//获取评论的详细信息
        String like_people = comment1.getLike_people();
        if("add".equals(comment.getTypeOfLike())){//点赞
            if(like_people==null || like_people.equals("")){//目前还没有人点赞
                commentDao.updateCommentLikeNum(comment,1);
                commentDao.addFirstLikePeople(comment);
            }
            else{
                String[] peoples = like_people.split(",");
                for (String people:peoples) {
                    if(String.valueOf(comment.getU_id()).equals(people)){
                        return false;//已经点赞
                    }
                }
                commentDao.updateCommentLikeNum(comment,1);
                commentDao.addLikePeople(comment);
            }
            return true;
        }
        else{//取消点赞
            if((like_people == null) || ("").equals(like_people)){//没有人点赞，取消不了
                return false;
            }
            else{
                String[] peoples = like_people.split(",");
                for (String people:peoples) {
                    if(String.valueOf(comment.getU_id()).equals(people)){
                        commentDao.reduceLikePeople(comment.getComment_id(),String.valueOf(comment.getU_id())+',');
                        commentDao.updateCommentLikeNum(comment,-1);
                        return true;
                    }
                }
            }
            return false;
        }

    }

    @Override
    public List<Comment> getMasterCommentInfo(Comment comment) {
        return commentDao.getMasterCommentInfo(comment);
    }

    @Override
    public List<Comment> getCommonCommentINfo(Comment comment) {
        return commentDao.getCommonCommentInfo(comment);
    }


    @Override
    public Comment getCommentByOtherId(Comment comment) {
        try{
            return commentDao.getCommentByOtherId(comment);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

}
