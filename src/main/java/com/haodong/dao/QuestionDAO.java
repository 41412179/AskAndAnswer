package com.haodong.dao;

import com.haodong.model.Question;
import com.haodong.model.User;
import org.apache.ibatis.annotations.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String TABLE_FIELDS = " title, content, created_date, user_id, comment_count ";
    String STAR = TABLE_FIELDS + ",id ";

    /**
     * @param question
     * @return
     */
    //intsert into table (a,b,c) values(d,e,f);
    @Insert({"insert into ", TABLE_NAME, "(", TABLE_FIELDS, " ) values( #{title}, #{content}, #{createdDate}, #{userId}, #{commentCount})"})
    int addQuestion(Question question);


    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select ", STAR, " from ", TABLE_NAME, " where id = #{id}"})
    Question queryQuestionById(@Param("id") int id);

    @Update({"update ", TABLE_NAME, " set comment_count = #{count} where id = #{id}"})
    void updateCommentCount(@Param("id") int id,
                            @Param("count") int count);
}
